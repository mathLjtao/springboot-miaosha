package com.ljtao.springbootmiaosha.service;


import com.ljtao.springbootmiaosha.dao.UserMapper;
import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisService redisService;
    public User getUserById(Long id){
        User byKeyword = userMapper.findByKeyword(id);
        return byKeyword;
    }

    public List<User> findByPhone(String phone) {
        return userMapper.findByPhone(phone);
    }

    public void addCookie(HttpServletResponse response,String token,User user){
        Cookie cookie =new Cookie("userToken",token);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        //加入到缓存中
        redisService.set(token,user,3600);
        response.addCookie(cookie);
    }
    /*
        从request中获取用户，如果有用户的话，就延长用户的登录有效期
     */
    public User getUserByRequest(HttpServletRequest req,HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        String cookieToken="";
        User userByToken=null;
        if(cookies==null){
            return null;
        }
        for(Cookie c:cookies){
            if("userToken".equals(c.getName())){
                cookieToken=c.getValue();
            }
        }
        if(cookieToken!=null && !"".equals(cookieToken)){
            //从redis中根据cookie获取用户信息
            userByToken=(User)redisService.get(cookieToken);
            //判断用户是否在缓存还存在
            if(userByToken!=null){
                addCookie(res,cookieToken,userByToken);//延长cookie的有效期

            }
        }
        return userByToken;
    }
}
