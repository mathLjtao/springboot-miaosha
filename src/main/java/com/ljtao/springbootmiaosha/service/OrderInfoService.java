package com.ljtao.springbootmiaosha.service;

import com.ljtao.springbootmiaosha.dao.OrderInfoMapper;
import com.ljtao.springbootmiaosha.model.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    public OrderInfo getOrderById(Long orderId){
        return orderInfoMapper.selectByPrimaryKey(orderId);
    }
}
