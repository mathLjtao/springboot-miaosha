package com.ljtao.springbootmiaosha.controller;

import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.redis.RedisService;
import com.ljtao.springbootmiaosha.service.UserService;
import com.ljtao.springbootmiaosha.util.JsonData;
import com.ljtao.springbootmiaosha.util.MD5Util;
import com.ljtao.springbootmiaosha.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ClusterOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/miaosha/login")
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @ResponseBody
    @RequestMapping("/do_login")
    public JsonData doLogin( HttpServletRequest req, HttpServletResponse res ,
                            @RequestParam("mobile") String phone, @RequestParam("password") String password){

        List<User> userList=userService.findByPhone(phone);
        if(userList.isEmpty()){
            return JsonData.fail("用户不存在！");
        }else{
            User user = userList.get(0);
            String dbSalt = user.getSalt();
            String dbPassword = user.getPassword();
            String s = MD5Util.formPassToDbPass(password, dbSalt);
            if(s.equals(dbPassword)){
                //设置cookie信息，将生成的token保存到cookie中
                String uuid = UUIDUtil.uuid();
                userService.addCookie(res,uuid,user);
                return JsonData.success();
            }
        }
        return JsonData.fail("账号密码不匹配！");
    }
    @RequestMapping("/to_login")
    public String toLogin(){

        return "login";
    }
}
