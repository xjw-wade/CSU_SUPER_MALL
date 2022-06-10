package com.csu.mall.rabbitmq;


import com.csu.mall.pojo.User;

public class MiaoshaMessage {
	private User user;
	private int goodsId;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
}
