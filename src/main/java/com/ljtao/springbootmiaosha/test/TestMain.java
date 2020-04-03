package com.ljtao.springbootmiaosha.test;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.util.List;

public class TestMain {
    public static void main(String[] args) throws NotFoundException {
        fun1();
    }
    public static void fun1(){
        MessageProperties properties=new MessageProperties();
        properties.setHeader("head1","value1");
        properties.setHeader("head2","value2");
        Message message=new Message("text msg！".getBytes(),properties);
        System.out.println(message.toString());
        //List.of(1,2,3);
    }
}
