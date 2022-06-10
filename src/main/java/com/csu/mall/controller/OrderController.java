package com.csu.mall.controller;

import com.csu.mall.common.Result;
import com.csu.mall.common.ResultCode;
import com.csu.mall.pojo.*;
import com.csu.mall.rabbitmq.MQSender;
import com.csu.mall.rabbitmq.MiaoshaMessage;
import com.csu.mall.service.*;
import com.csu.mall.util.CookieUtil;
import com.csu.mall.util.Page4Navigator;
import com.csu.mall.util.RedisUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    MQSender mQSender;

    //标记
    Map <Integer,Boolean>localMap=new HashMap<Integer, Boolean>();
    //秒杀接口
    @PostMapping("/sec_kill")
    public Result<String> doSecKill(@RequestParam(value="productId",defaultValue="0") int productId, HttpServletRequest request) {
        //读取sessionID
        String loginToken = CookieUtil.readLoginToken(request);
        User user = (User)redisUtil.get(loginToken);
        //内存标记，减少对redis的访问 localMap.put(goodsId,false);
        boolean over=localMap.getOrDefault(productId, false);
        if(over) {
            return Result.createForError(ResultCode.MIAOSHA_OVER_ERROR.getMsg());
        }
        //2.预减少库存，减少redis里面的库存
        //redis分布式锁 10s后自动释放
        String LOCK_ID = String.valueOf(productId)+ "_lock";
        boolean lock = redisUtil.getLock(LOCK_ID, 10 * 1000);
        if (lock) {
            long stock=redisUtil.decr(String.valueOf(productId),1);
            //3.判断减少数量1之后的stock，区别于查数据库时候的stock<=0
            if(stock<0) {
                //在容量为0的时候，那么就打标记为true
                localMap.put(productId,true);
                redisUtil.releaseLock(LOCK_ID);
                return Result.createForError(ResultCode.MIAOSHA_OVER_ERROR.getMsg());
            } else {
                //5.正常请求，入队，发送一个秒杀message到队列里面去，入队之后客户端应该进行轮询。
                MiaoshaMessage mms=new MiaoshaMessage();
                mms.setUser(user);
                mms.setGoodsId(productId);
                mQSender.sendMiaoshaMessage(mms);
                redisUtil.releaseLock(LOCK_ID);
                return Result.createForSuccess("抢购成功!");
            }
        }else{
            while(lock == false){
                lock = redisUtil.getLock(LOCK_ID, 10 * 1000);
            }
            long stock=redisUtil.decr(String.valueOf(productId),1);
            //3.判断减少数量1之后的stock，区别于查数据库时候的stock<=0
            if(stock<0) {
                //在容量为0的时候，那么就打标记为true
                localMap.put(productId,true);
                redisUtil.releaseLock(LOCK_ID);
                return Result.createForError(ResultCode.MIAOSHA_OVER_ERROR.getMsg());
            } else {
                //5.正常请求，入队，发送一个秒杀message到队列里面去，入队之后客户端应该进行轮询。
                MiaoshaMessage mms=new MiaoshaMessage();
                mms.setUser(user);
                mms.setGoodsId(productId);
                mQSender.sendMiaoshaMessage(mms);
                redisUtil.releaseLock(LOCK_ID);
                return Result.createForSuccess("抢购成功!");
            }
        }
    }



    @GetMapping("/orders")
    public Result<Page4Navigator<Order>> list(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        //请求订单数据
        Page4Navigator<Order> page =orderService.orderList(start, size, 5);
        //为Order对象注入OrderItem数据
        orderItemService.fill(page.getContent());
//      removeOrderFromOrderItem，它的作用是把订单里的订单项的订单属性设置为空。。。
//      比如有个 order, 拿到它的 orderItems， 然后再把这些orderItems的order属性，设置为空。
//      为什么要做这个事情呢？ 因为SpringMVC ( springboot 里内置的mvc框架是 这个东西)的 RESTFUL 注解，在把一个Order转换为json的同时，
//      会把其对应的 orderItems 转换为 json数组， 而 orderItem对象上有 order属性， 这个order 属性又会被转换为 json对象，然后这个order 下又有 orderItems 。。。
//      就这样就会产生无穷递归，系统就会报错了。
//      所以这里采用 removeOrderFromOrderItem 把 OrderItem的order设置为空就可以了。
        orderService.removeOrderFromOrderItem(page.getContent());
        return Result.createForSuccess(page);
    }


    //发货 订单的增加和删除功能交由前台完成，后台不提供
    // var url =  "deliveryOrder/"+order.id;
    @PutMapping("/deliveryOrder/{oid}")
    public Result<String> deliveryOrder(@PathVariable int oid) throws IOException {
        Order o = orderService.getById(oid);
        //添加发货时间
        o.setDeliveryDate(new Date());
        o.setStatus(OrderService.waitConfirm);
        orderService.update(o);
        return Result.createForSuccess("发货成功");
    }

    //立即购买映射 接收Pid和num ,从Session中取User对象
    @GetMapping("/buy_one")
    public Result<Integer> buyone(@RequestParam("pid") int pid, @RequestParam("num") int num, HttpServletRequest request){
        //新增订单项OrderItem， 新增订单项要考虑两个情况
        //1、如果订单项存在某个商品的OrderItem还没有生成订单，
        // 并且存在于购物车中，就需要在对应的OrderItem基础上调整数据
        //2、如果不存在某个产品对应的OrderItem，那么就新增一个订单项OrderItem
        return Result.createForSuccess(buyoneAndAddCart(pid,num,request));
    }

    //加入购物车隐射,其逻辑和立即购物时一样的，
    //都是从数据库中校验某个用户加入购物车或者立即的产品有没有订单项，如果没有则添加
    //如果有订单项则获取该对象然后修改里面的数量等等
    //其核心都是为了生成订单项，用于往后生成订单的逻辑作铺垫
    @GetMapping("add_cart")
    public Result<String> addCart(int pid, int num, HttpServletRequest request) {
        buyoneAndAddCart(pid,num,request);
        return Result.createForSuccess("添加购物车成功");
    }

    //返回订单项id,用于跳转到对应的订单项页中，利用对应的订单项id生成购买的订单
    private int buyoneAndAddCart(int pid, int num, HttpServletRequest request) {

        Product p = productService.getById(pid);
        //读取sessionID
        String loginToken = CookieUtil.readLoginToken(request);
        User user = (User)redisUtil.get(loginToken);
        boolean found = false; //默认找不到
        int oiid = 0;
        //第一种情况
        //基于用户对象user，查询没有生成订单的订单项集合
        //找到对应的订单项然后进行操作
        List<OrderItem> orderItemList = orderItemService.listByUser(user);
        for(OrderItem orderItem:orderItemList){
            //如果在对应用户对应商品中找到相同的订单项，则对该订单项进行操作
            if (orderItem.getProduct().getId()==p.getId()){
                orderItem.setNumber(orderItem.getNumber()+num);
                //将对应的orderItem对象更新到数据库上
                orderItemService.update(orderItem);
                found = true;
                oiid = orderItem.getId();
                break;
            }
        }

        //第二种情况 对应用户的购物车内没有找到对应产品的订单项，那么就需要生成一个订单项
        if (!found){
            OrderItem orderItem = new OrderItem();
            orderItem.setUser(user);
            orderItem.setProduct(p);
            orderItem.setNumber(num);
            orderItemService.add(orderItem);
            oiid = orderItem.getId();
        }

        return oiid;
    }

    //利用Oiid获得对应订单项，在订单页中读出对应数据
    @GetMapping("/fore_buy")
    public Object buy(String[] oiid,HttpServletRequest request){
        //这里要用字符串数组试图获取多个oiid，而不是int类型仅仅获取一个oiid?
        // 因为根据购物流程环节与表关系，结算页面还需要显示在购物车中选中的多条OrderItem数据，
        // 所以为了兼容从购物车页面跳转过来的需求，要用字符串数组获取多个oiid
        List<OrderItem> orderItems = new ArrayList<>(); //当商品在购物车被多选立即购物时，就需要接收多个OrderItem
        float total = 0;//用于计算总价
        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi= orderItemService.getById(id);
            total += oi.getProduct().getPromotePrice()*oi.getNumber();
            //将当前的OrderItem添加到List<OrderItem>中
            orderItems.add(oi);
        }
        //为每个OrderItem对应的每个产品设置预览图，按顺序，这个设置是单纯的setter
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        //返回的OrderItems中的product上就会有上一部设置的预览图
        //读取sessionID
        String loginToken = CookieUtil.readLoginToken(request);
        redisUtil.set(loginToken+"ois", orderItems);


        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.createForSuccess(map);
    }

    //从购物车中获取订单项信息
    @GetMapping("/fore_cart")
    public Result<Object> cart(HttpServletRequest request){
        //读取sessionID
        String loginToken = CookieUtil.readLoginToken(request);
        User user = (User)redisUtil.get(loginToken);
        // List<OrderItem> findByUserAndOrderIsNull(User user);
        List<OrderItem> orderItemList = orderItemService.listByUser(user);
        //为每个订单项中的商品设置预览图
        productImageService.setFirstProdutImagesOnOrderItems(orderItemList);
        return Result.createForSuccess(orderItemList);
    }

    //从购物车中更新订单数量 var url = "forechangeOrderItem?pid="+pid+"&num="+num;
    @GetMapping("/fore_changeOrderItem")
    public Result<Object> changeOrderItem(@RequestParam("pid")int pid,@RequestParam("num")int num,HttpServletRequest request){
        //通过pid和user从List<orderItem>中读出对应的orderItem
        Product product = productService.getById(pid);
        //读取sessionID
        String loginToken = CookieUtil.readLoginToken(request);
        User user = (User)redisUtil.get(loginToken);
        //检查登录状态
        if(null==user)
            return Result.createForError("用户未登录");
        List<OrderItem> orderItemList = orderItemService.listByUser(user);
        for(OrderItem orderItem:orderItemList){
            if(orderItem.getProduct().getId()==product.getId()){
                orderItem.setNumber(num);
                //将数量更新在数据上
                orderItemService.update(orderItem);
                break;
            }
        }
        return Result.createForSuccess("修改购物车成功!");
    }

    //创建订单
    @PostMapping("/fore_createOrder")
    public Result<Object> createOrder(@RequestBody Order order,HttpServletRequest request){
//        1. 从redis中获取user对象
//        2. 根据当前时间加上一个4位随机数生成订单号
//        3. 根据上述参数，创建订单对象
//        4. 把订单状态设置为等待支付
//        5. 从session中获取订单项集合 ( 在结算功能的ForeRESTController.buy() ，订单项集合被放到了redis中 )
//        7. 把订单加入到数据库，并且遍历订单项集合，设置每个订单项的order，更新到数据库
//        8. 统计本次订单的总金额
//        9. 返回总金额 和oid方便跳转到付款页面
        //读取sessionID
        String loginToken = CookieUtil.readLoginToken(request);
        User user = (User)redisUtil.get(loginToken);
        if (null==user){
            return Result.createForError("用户未登录");
        }

        String orderCode =  new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(1000, 9999);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user); //设置user对象，用于设置uid
        order.setStatus(OrderService.waitPay);
        //将每个orderItem的order设置为当前order
        List<OrderItem> orderItemList = (List<OrderItem>)redisUtil.get(loginToken+"ois");
        redisUtil.del(loginToken+"ois");
        float total =orderService.sumPrice(order,orderItemList);

        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.createForSuccess(map);

    }

    //支付成功页面
    @GetMapping("/fore_payed")
    public Result<Object> payed(@RequestParam("oid")int oid){
        //更新支付时间到订单上和更改状态带待发货
        Order order = orderService.getById(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return Result.createForSuccess(order);
    }

    //获取已购买的订单列表
    @GetMapping("/fore_bought")
    public Result<Object> bought(HttpServletRequest request) {
        //读取sessionID
        String loginToken = CookieUtil.readLoginToken(request);
        User user = (User)redisUtil.get(loginToken);
        if (null==user){
            return Result.createForError("用户未登录");
        }
        List<Order> os= orderService.listByUserWithoutDelete(user);
        //设置rder底下的OrderItem的order为Null，避免重复json化。进入死循环
        orderService.removeOrderFromOrderItem(os);
        return Result.createForSuccess(os);
    }
    //确认收货的前提是需要在后台将订单状态设置为已发货才行
    //我的订单确认收货页 <a v-if="o.status=='waitConfirm'" :href="'confirmPay?oid='+o.id">
    @GetMapping(value = "/fore_confirmPay")
    public Result<Object> confirmPay(int oid){
        Order order = orderService.getById(oid);
        //为order填充orderItem数据
        orderItemService.fill(order);
        //移除当前order重复的List<OrderItem>下的orderItem的order
        orderService.cacl(order);
        orderService.removeOrderFromOrderItem(order);
        return Result.createForSuccess(order);
    }


    //我的订单确认收货页的确认支付页
    // var url =  foreorderConfirmed"+"?oid="+oid;
    @GetMapping(value = "/fore_orderConfirmed")
    public Result<Object> orderConfirmed(@RequestParam("oid") int oid) {
        //获取订单，并且将订单状态设置为待评价，并且设置支付时间,最后更新到数据库上
        Order order = orderService.getById(oid);
        order.setStatus(OrderService.waitReview);
        order.setConfirmDate(new Date());
        orderService.update(order);
        return Result.createForSuccess(order);
    }

    //删除订单 ，当然这个删除并不是真的删除而是将状态设置为delete
    @PutMapping(value = "/fore_deleteOrder")
    public Result<Object> deleteOrder(@RequestParam("oid") int oid){
        Order o = orderService.getById(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.createForSuccess("订单删除成功");
    }

    //请求评价（请求订单中订单项的评价）
    @GetMapping(value = "/fore_review")
    public Result<Object> review(@RequestParam("oid") int oid){
        //获得订单项
//        1 获取参数oid
//        2 根据oid获取订单对象o
//        3 为订单对象填充订单项
//        4 获取第一个订单项对应的产品,因为在评价页面需要显示一个产品图片，那么就使用这第一个产品的图片了。（这里没有对订单里的每种产品都评价，因为复杂度就比较高了，初学者学起来太吃力，有可能就放弃学习了，所以考虑到学习的平滑性，就仅仅提供对第一个产品的评价）
//        5 获取这个产品的评价集合
//        6 为产品设置评价数量和销量
//        7 把产品，订单和评价集合放在map上
//        8 通过 Result 返回这个map
        Order order = orderService.getById(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        Product product = order.getOrderItems().get(0).getProduct();
        //通过产品获得评价列表
        List<Review> reviews = reviewService.list(product);
        //为当前的产品对象设置销量和评价数量的，用于前端显示
        productService.setSaleAndReviewNumber(product);
        Map<String,Object> map = new HashMap<>();
        map.put("p", product);
        map.put("o", order);
        map.put("reviews", reviews);

        return Result.createForSuccess(map);
    }

    //提交评价  var url =  "foredoreview?oid="+vue.o.id+"&pid="+vue.p.id+"&content="+vue.content;
    @PostMapping(value = "/fore_do_review")
    public Result<Object> doreview(@RequestParam("oid")int oid,
                           @RequestParam("pid")int pid,
                           @RequestParam("content")String content,
                                   HttpServletRequest request
    ){
        //评价完成后获得订单对象，设置一下订单状态
        Order order = orderService.getById(oid);
        order.setStatus(OrderService.finish);
        //1、将状态更新到数据库中
        orderService.update(order);
        //2、过滤内容
        content = HtmlUtils.htmlEscape(content);
        //3、  将uid和pid、content写入review表中
        //基本添加对象到数据库中意义都是new，然后设置值再更新到数据库上
        Review review = new Review();
        Product product = productService.getById(pid);
        //读取sessionID
        String loginToken = CookieUtil.readLoginToken(request);
        User user = (User)redisUtil.get(loginToken);
        review.setProduct(product);
        review.setContent(content);
        review.setCreateDate(new Date());
        review.setUser(user);
        //将封装好的review更新到数据库上
        reviewService.addReview(review);
        return Result.createForSuccess("评价成功");
    }











}
