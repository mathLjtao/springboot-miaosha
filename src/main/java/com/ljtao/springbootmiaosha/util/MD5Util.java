package com.ljtao.springbootmiaosha.util;

import org.springframework.util.DigestUtils;

public class MD5Util {
    private static final String SALT="ljtao3";
    public static String md5(String str){
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }
    public static String  inputPassToFromPass(String str){
        str = ""+SALT.charAt(1)+SALT.charAt(0)+str+SALT.charAt(1)+SALT.charAt(1);
        return md5(str);
    }
    public static String formPassToDbPass(String str, String dbSalt){
        str=inputPassToFromPass(str);
        String dbPass = ""+dbSalt.charAt(0)+dbSalt.charAt(1)+str+dbSalt.charAt(0)+dbSalt.charAt(0);
        return md5(dbPass);
    }
    public static void main(String[] args) {
        System.out.println(formPassToDbPass("123456","asdfgh"));
    }
}
