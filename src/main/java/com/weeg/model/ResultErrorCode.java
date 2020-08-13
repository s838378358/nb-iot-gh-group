package com.weeg.model;

import net.sf.json.JSONObject;

/**
 * Created by SJ on 2020/8/12
 */
public class ResultErrorCode {

    /**
     * 解析错误码
     * @param code
     * @return
     */
    public static String errorcode(String code) {
        JSONObject jsonobject = new JSONObject();
        //数据对象ID不正确
        if (code.equals("0001")) {
            //验证数据对象ID 空着
            jsonobject.put("数据对象ID不正确", code);
        } else if (code.equals("0002")) {
            //验证日期非法 空着
            jsonobject.put("日期非法", code);
        } else if (code.equals("0003")) {
            //验证协议类型 空着
            jsonobject.put("协议类型不支持", code);
        } else if (code.equals("0004")) {
            //
            jsonobject.put("协议框架版本不支持", code);
        } else if (code.equals("0005")) {
            //
            jsonobject.put("MAC认证错误", code);
        } else if (code.equals("0006")) {
            //
            jsonobject.put("应用协议版本不支持", code);
        } else if (code.equals("0007")) {
            //
            jsonobject.put("写参数值非法", code);
        } else if (code.equals("0008")) {
            //
            jsonobject.put("标号非法", code);
        } else if (code.equals("0000")) {
            jsonobject.put("无错误", code);
        }

        return jsonobject.toString();
    }

}
