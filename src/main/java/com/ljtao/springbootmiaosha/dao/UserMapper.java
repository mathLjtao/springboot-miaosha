package com.ljtao.springbootmiaosha.dao;


import com.ljtao.springbootmiaosha.model.User;

import java.util.List;

public interface UserMapper {
    User findByKeyword(Long id);

    List<User> findByPhone(String phone);
    Integer checkLogin(String phone,String password);
}
