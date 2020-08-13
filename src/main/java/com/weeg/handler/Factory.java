package com.weeg.handler;


import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SJ on 2020/8/10
 */
public class Factory {

    private static Map<String,Handler> strategyMap = new HashMap<>();

    /**
     * 根据 did 获取 相对应的handler
     * @param str
     * @return
     */
    public static Handler getInvokeStrategy(String str){
        return strategyMap.get(str);
    }


    /**
     * 根据did 讲方法注册成一个handler
     * @param str
     * @param handler
     */
    public static void register(String str,Handler handler){
        if (StringUtils.isEmpty(str) || null == handler){
            return;
        }
        strategyMap.put(str,handler);
    }



}
