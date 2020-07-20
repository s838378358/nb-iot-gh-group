package com.weeg.util;


import org.apache.commons.lang.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class operationUtil {

    /**
     * 字符串补0
     * @param data
     * @param n
     * @return
     */
    public static String strFillzero(String data,int n){
        String databody;
        String fill = "";
        if(data.length() == n){
            databody = data;
        }else {
            for(int i=0; i<n; i++){
                if(i >= data.length()){
                    fill += "0";
                }
            }
            databody = fill + data;
        }
        return databody;
    }


    /**
     * 从map获取object参数，null的转成"",否则获取string类型
     * @param param
     * @param key
     * @return
     */
    public static String getString(Map param, String key){
        return ObjectUtils.defaultIfNull(param.get(key),"").toString();
    }

    /**
     * 判断今天是否为本月一号
     */
    public static boolean isMonthFirstDay(){
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        if (date == 1){
            return true;
        }
        return false;
    }



}
