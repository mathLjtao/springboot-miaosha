package com.ljtao.springbootmiaosha.util;

public class CodeMsg {
    private int code;
    private String msg;

    //通用的错误码
    //登录模块 5002XX
    //商品模块 5003XX
    //订单模块 5004XX
    //秒杀模块 5005XX
    public  static CodeMsg MIAOSHA_OVAE=new CodeMsg(500500,"商品已经秒杀完毕");
    public  static CodeMsg MIAOSHA_REPEATE=new CodeMsg(500501,"不能重复秒杀");

    private CodeMsg(){}
    private CodeMsg(int code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
