package com.weeg.util;



public class operationUtil {

    /**
     * 字符串补0
     * @param data
     * @param n
     * @return
     */
    public static String strFillzero(String data,int n){
        String databody;
        String fill = null;
        if(data.length() == n){
            databody = data;
        }else {
            for(int i=0; i<n; i++){
                if(i >= data.length()){
                    fill += "0";
                }
            }
            databody = fill + n;
        }
        return databody;
    }

}
