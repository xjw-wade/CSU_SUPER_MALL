package com.csu.mall.controller;


import com.csu.mall.common.CONSTANT;
import com.csu.mall.common.Result;
import com.csu.mall.pojo.Order;
import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.User;
import com.csu.mall.service.OrderItemService;
import com.csu.mall.service.OrderService;
import com.csu.mall.service.ProductImageService;
import com.csu.mall.service.ProductService;
import com.csu.mall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
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
    public Result<Integer> buyone(@RequestParam("pid") int pid, @RequestParam("num") int num, HttpSession session){
        //新增订单项OrderItem， 新增订单项要考虑两个情况
        //1、如果订单项存在某个商品的OrderItem还没有生成订单，
        // 并且存在于购物车中，就需要在对应的OrderItem基础上调整数据
        //2、如果不存在某个产品对应的OrderItem，那么就新增一个订单项OrderItem
        return Result.createForSuccess(buyoneAndAddCart(pid,num,session));
    }

    //加入购物车隐射,其逻辑和立即购物时一样的，
    //都是从数据库中校验某个用户加入购物车或者立即的产品有没有订单项，如果没有则添加
    //如果有订单项则获取该对象然后修改里面的数量等等
    //其核心都是为了生成订单项，用于往后生成订单的逻辑作铺垫
    @GetMapping("add_cart")
    public Result<String> addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid,num,session);
        return Result.createForSuccess("添加购物车成功");
    }

    //返回订单项id,用于跳转到对应的订单项页中，利用对应的订单项id生成购买的订单
    private int buyoneAndAddCart(int pid, int num, HttpSession session) {

        Product p = productService.getById(pid);
        User user = (User)session.getAttribute(CONSTANT.LOGIN_USER);
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
    @GetMapping("/buy")
    public Object buy(String[] oiid,HttpSession session){
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
        session.setAttribute("ois", orderItems);

        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.createForSuccess(map);
    }


}
