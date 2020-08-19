package com.weeg.callbackHandler;


import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SJ on 2020/8/10
 */
public class CallbackFactory {

    private static Map<String, CallBackHandler> strategyMap = new HashMap<>();

    /**
     * 根据 did 获取 相对应的handler
     * @param str
     * @return
     */
    public static CallBackHandler getInvokeStrategy(String str){
        return strategyMap.get(str);
    }


    /**
     * 根据did 讲方法注册成一个handler
     * @param str
     * @param callBackHandler
     */
    public static void register(String str, CallBackHandler callBackHandler){
        if (StringUtils.isEmpty(str) || null == callBackHandler){
            return;
        }
        strategyMap.put(str, callBackHandler);
    }



}
