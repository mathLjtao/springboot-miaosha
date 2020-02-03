package com.ljtao.springbootmiaosha.test;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author ljtao
 * @date 2020/1/30
 */
public class TestOneValueCache {
    private final String userName;
    private final String token;
    public TestOneValueCache(String userName,String token){
        this.userName=userName;
        this.token=token;
    }
    public String getToken(String un){
        if(userName==null || !userName.equals(un)){
            return null;
        }else{
            return token;
        }
    }
}
