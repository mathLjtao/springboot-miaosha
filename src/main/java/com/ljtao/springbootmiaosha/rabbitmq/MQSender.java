package com.ljtao.springbootmiaosha.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
    消息发送者
 */
@Service
public class MQSender {
    @Autowired
    private AmqpTemplate amqpTemplate;


    public void miaoshaSend(String msg){
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
    }

    public void inWordRecordSend(String msg){
        amqpTemplate.convertAndSend(MQConfig.INWORK_RECORD_QUEUE,msg);
    }


    public void testSend(String msg){
        System.out.println("send: "+msg);
        amqpTemplate.convertAndSend(MQConfig.TEXT_QUEUE_NAME,msg);
    }

    public void testTopic1(){
        String msg=" hello !!";
        System.out.println("testTopic1 send:"+msg);
        //amqpTemplate.convertAndSend(MQConfig.TOPIC_QUEUE1,msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key",msg+" 0");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+" 1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+" 2");
    }
    public void testFanout(){
        String msg=" hello !!";
        System.out.println("testFanout send:"+msg);
        //发送信息到所有绑定到MQConfig.FANOUT_EXCHANGE交换机的队列中去、
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
    }
    public void testHeader(){
        String msg=" hello !!";
        System.out.println("testHeader send:"+msg);
        //设置头部信息
        MessageProperties properties=new MessageProperties();
        properties.setHeader("header1","value1");
        properties.setHeader("header2","value2");

        Message obj=new Message(msg.getBytes(),properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
    }
}
