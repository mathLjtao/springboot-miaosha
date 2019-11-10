package com.ljtao.springbootmiaosha.util;

import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSON;

public class JsonData {
	private boolean ret;
	private String msg;
	private Object data;
	public JsonData(){}
	public JsonData(boolean ret){
		this.ret=ret;
	}
	public static  JsonData success(String msg,Object data){
		JsonData jd=new JsonData(true);
		jd.msg=msg;
		jd.data=data;
		return jd;
	}
	public static  JsonData success(Object data){
		JsonData jd=new JsonData(true);
		jd.data=data;
		return jd;
	}
	public static JsonData success(){
		return new JsonData(true);
	}
	public static JsonData fail(String msg){
		JsonData jd=new JsonData(false);
		jd.msg=msg;
		return jd;
	}
	public Map<String,Object> toMap(){
		Map<String,Object> hm=new HashMap<String,Object>();
		hm.put("ret", ret);
		hm.put("msg", msg);
		hm.put("data", data);
		return hm;
	}
	public boolean isRet() {
		return ret;
	}
	public void setRet(boolean ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}


	public static <T> String beanToString(T value) {
		if(value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if(clazz == int.class || clazz == Integer.class) {
			return ""+value;
		}else if(clazz == String.class) {
			return (String)value;
		}else if(clazz == long.class || clazz == Long.class) {
			return ""+value;
		}else {
			return JSON.toJSONString(value);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T stringToBean(String str, Class<T> clazz) {
		if(str == null || str.length() <= 0 || clazz == null) {
			return null;
		}
		if(clazz == int.class || clazz == Integer.class) {
			return (T)Integer.valueOf(str);
		}else if(clazz == String.class) {
			return (T)str;
		}else if(clazz == long.class || clazz == Long.class) {
			return  (T)Long.valueOf(str);
		}else {
			return JSON.toJavaObject(JSON.parseObject(str), clazz);
		}
	}
	
}
