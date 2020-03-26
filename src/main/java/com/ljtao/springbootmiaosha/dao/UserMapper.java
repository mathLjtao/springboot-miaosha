package com.ljtao.springbootmiaosha.dao;


import com.ljtao.springbootmiaosha.model.User;

import java.util.List;

public interface UserMapper {
    int insert(User user);

    User findByKeyword(Long id);

    List<User> findByPhone(String phone);
    Integer checkLogin(String phone,String password);
}
