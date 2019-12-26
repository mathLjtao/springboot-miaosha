package com.ljtao.springbootmiaosha.test;


import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.rabbitmq.MQSender;
import com.ljtao.springbootmiaosha.service.UserService;
import com.ljtao.springbootmiaosha.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

//将这个类设置为只返回json数据
@RestController
@RequestMapping("/test")
public class TestRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private MQSender mqSender;
    @RequestMapping("/run")
    public String run(){
        return "success";
    }
    /*
    测试从数据库拿数据
     */
    @RequestMapping("/getUserById")
    public User getUserById(Long id){
        id=8l;
        return userService.getUserById(id);
    }
    /*
        测试redis
     */
    public void fun1(){


    }
    /*
        测试全局异常处理器
     */
    @RequestMapping("/exception")
    public String fun2(){
        int i=1/0;
        return "aa";

    }
    /*
        测试rabbitmq 的Direct模式，最简单的模式
     */
    @RequestMapping("/direct_mq")
    public JsonData fun3(){
        mqSender.testSend("hello mq");
        return JsonData.success("success");
    }
    /*
        测试rabbitmq 的Topic模式，
     */
    @RequestMapping("/topic_mq")
    public JsonData fun4(){
        mqSender.testTopic1();
        return JsonData.success("success");
    }
    /*
        测试rabbitmq 的Fanout模式，
     */
    @RequestMapping("/fanout_mq")
    public JsonData fun5(){
        mqSender.testFanout();
        return JsonData.success("success");
    }
    /*
        测试rabbitmq 的Header模式，
     */
    @RequestMapping("/header_mq")
    public JsonData fun6(){
        mqSender.testHeader();
        return JsonData.success("success");
    }

}
