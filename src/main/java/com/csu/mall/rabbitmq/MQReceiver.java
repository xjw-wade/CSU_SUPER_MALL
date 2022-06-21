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

		List<OrderItem> orderItemList = orderItemService.listByUser(user);
		boolean found = false; //默认找不到
		//第一种情况
		//基于用户对象user，查询没有生成订单的订单项集合
		//找到对应的订单项然后进行操作
		for(OrderItem orderItem:orderItemList){
			//如果在对应用户对应商品中找到相同的订单项，则对该订单项进行操作
			if (orderItem.getProduct().getId()==p.getId()){
				orderItem.setNumber(orderItem.getNumber()+1);
				//将对应的orderItem对象更新到数据库上
				orderItemService.update(orderItem);
				found = true;
				break;
			}
		}

		//第二种情况 对应用户的购物车内没有找到对应产品的订单项，那么就需要生成一个订单项
		if (!found){
			OrderItem orderItem = new OrderItem();
			orderItem.setUser(user);
			orderItem.setProduct(p);
			orderItem.setNumber(1);
			orderItemService.add(orderItem);
		}
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
