package com.ljtao.springbootmiaosha.model;

import java.io.Serializable;

/*
对象 User 必须实现序列化，因为需要将对象序列化后存储到 Redis。如果没实现 Serializable ，控制台会爆出以下异常：
 */
public class User implements Serializable {
    private static final long serialVersionUID = -1L;

    private Long id;
    private String phone;
    private String name;
    private String password;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    private String salt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
