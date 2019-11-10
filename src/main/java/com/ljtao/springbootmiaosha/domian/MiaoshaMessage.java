package com.ljtao.springbootmiaosha.domian;

import com.ljtao.springbootmiaosha.model.User;

public class MiaoshaMessage {
    private User user;
    private Long goodsId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
