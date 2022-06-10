package com.csu.mall.rabbitmq;


import com.csu.mall.pojo.Order;
import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.User;
import com.csu.mall.service.OrderItemService;
import com.csu.mall.service.OrderService;
import com.csu.mall.service.ProductService;
import com.csu.mall.util.RedisUtil;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//接收者
@Service
public class MQReceiver {
	@Autowired
	ProductService productService;
	@Autowired
	RedisUtil redisUtil;
	//作为秒杀功能事务的Service
	@Autowired
	OrderService orderService;
	@Autowired
	OrderItemService  orderItemService;
	
	private static Logger log= LoggerFactory.getLogger(MQReceiver.class);
	
	
	@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)//指明监听的是哪一个queue
	public void receiveMiaosha(String message) {
		log.info("receiveMiaosha message:"+message);
		//通过string类型的message还原成bean
		//拿到了秒杀信息之后。开始业务逻辑秒杀，
		MiaoshaMessage mm=redisUtil.stringToBean(message, MiaoshaMessage.class);
		User user=mm.getUser();
		int goodsId=mm.getGoodsId();
		//更新数据库
		Product p = productService.getById(goodsId);
		p.setStock(p.getStock() - 1);
		productService.update(p);

		List<OrderItem> orderItemList = orderItemService.listByProduct(p);

		if (orderItemList == null){
			OrderItem orderItem = new OrderItem();
			orderItem.setUser(user);
			orderItem.setProduct(p);
			orderItem.setNumber(1);
			orderItemList.add(orderItem);
			orderItemService.add(orderItem);
		}else{
			OrderItem orderItem = orderItemList.get(0);
			orderItem.setNumber(orderItem.getNumber()+1);
			//将对应的orderItem对象更新到数据库上
			orderItemList.get(0).setNumber(orderItem.getNumber()+1);
			orderItemService.update(orderItem);
		}
        Order order = new Order();
		String orderCode =  new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(1000, 9999);
		order.setOrderCode(orderCode);
		order.setCreateDate(new Date());
		order.setUser(user); //设置user对象，用于设置uid
		order.setStatus(OrderService.waitPay);
		float total =orderService.sumPrice(order,orderItemList);
		order.setTotal(total);
		orderService.update(order);
	}
	
	
	
	
	
//	@RabbitListener(queues=MQConfig.QUEUE)//指明监听的是哪一个queue
//	public void receive(String message) {
//		log.info("receive message:"+message);
//	}
//	
//	@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)//指明监听的是哪一个queue
//	public void receiveTopic1(String message) {
//		log.info("receiveTopic1 message:"+message);
//	}
//	
//	@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)//指明监听的是哪一个queue
//	public void receiveTopic2(String message) {
//		log.info("receiveTopic2 message:"+message);
//	}
//	
//	@RabbitListener(queues=MQConfig.HEADER_QUEUE)//指明监听的是哪一个queue
//	public void receiveHeaderQueue(byte[] message) {
//		log.info("receive Header Queue message:"+new String(message));
//	}
}
