package com.ljtao.springbootmiaosha.test;


import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

//将这个类设置为只返回json数据
@RestController
@RequestMapping("/test")
public class TestRestController {
    @Autowired
    private UserService userService;
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
}
