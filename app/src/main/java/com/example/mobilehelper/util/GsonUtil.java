package com.example.mobilehelper.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GsonUtil {

    /**
     * 把json字符串转化为相对应的bean对象
     * @param jsonString json字符串
     * @param clazzBean 要封装成的目标对象
     * @return 目标对象
     */
    public static <T> T parserJsonToArrayBean(String jsonString,Class<T> clazzBean){
        if(TextUtils.isEmpty(jsonString)){
            throw new RuntimeException("json字符串为空");
        }
        JsonElement jsonElement = new JsonParser().parse(jsonString);
        if(jsonElement.isJsonNull()){
            throw new RuntimeException("json字符串为空");
        }
        if(!jsonElement.isJsonObject()){
            throw new RuntimeException("json不是一个对象");
        }
        return new Gson().fromJson(jsonElement, clazzBean);
    }

    /**
     * 把bean对象转化为json字符串
     * @param obj bean对象
     * @return 返回的是json字符串
     */
    public static String toJsonString(Object obj){
        if(obj!=null){
            return new Gson().toJson(obj);
        }else{
            throw new RuntimeException("对象不能为空");
        }
    }


}
