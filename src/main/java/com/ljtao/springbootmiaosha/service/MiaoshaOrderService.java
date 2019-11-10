package com.ljtao.springbootmiaosha.service;

import com.ljtao.springbootmiaosha.dao.MiaoshaOrderMapper;
import com.ljtao.springbootmiaosha.model.MiaoshaOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaOrderService {
    @Autowired
    private MiaoshaOrderMapper miaoshaOrderMapper;
    public int countByUserIdAndGoodsId(Long userId, Long goodsId) {
        return miaoshaOrderMapper.countByUserIdAndGoodsId(userId,goodsId);
    }

    public MiaoshaOrder selectByUserIdAndGoodsId(Long userId, Long goodsId) {
        return miaoshaOrderMapper.selectByUserIdAndGoodsId(userId,goodsId);
    }
}
