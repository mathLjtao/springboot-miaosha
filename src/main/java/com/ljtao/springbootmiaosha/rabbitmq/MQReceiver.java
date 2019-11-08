package com.ljtao.springbootmiaosha.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
/*
    消息接收者
 */
@Service
public class MQReceiver {

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void miaoshaReceive(String msg){
        System.out.println(msg);
    }

    @RabbitListener(queues = MQConfig.TEXT_QUEUE_NAME)
    public void textReceive(String msg){
        System.out.println("receive:"+msg);
    }
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void testReceive1(String msg){
        System.out.println("topic queue1 receive:"+msg);
    }
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void testReceive2(String msg){
        System.out.println("topic queue2 receive:"+msg);
    }
    @RabbitListener(queues = MQConfig.HEADER_QUEUE1)
    public void testReceive3(byte[] msg){
        System.out.println("header queue1 receive:"+new String(msg));
    }
}
