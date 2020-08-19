package com.weeg.controller;

import com.weeg.util.Post;
import net.sf.json.JSONObject;

/**
 * Created by SJ on 2020/8/14
 */
public class RegistDeviceController {

    /**
     * 注册设备 Description: Title: createDevice
     *
     * @author yuyan
     * @date 2018年6月19日
     */
    public static String createDevice(String params) {
        Post post = new Post();
        /* ........对参数进行处理......... */
        // 将接受到的参数转换成json对象，便于取用
        JSONObject param = JSONObject.fromObject(params);
        String name = param.getString("name");
        String imei = param.getString("imei");
        String imsi = param.getString("imsi");
        // 对应的平台信息是一个json格式的字符串，直接转换成json对象
        JSONObject operator = param.getJSONObject("operator");

        // 获取ca证书存放的地址
        String address = operator.getString("address");
        String host = operator.getString("host");
        String apiKey = operator.getString("apiKey");

        // 拼接请求地址
        String url = address + "/devices";

        // 创建请求体对象
        JSONObject object = new JSONObject();
        // 创建设备对象的名称
        object.put("title", name);
        // 创建设备对象的描述
        object.put("desc", "Weeg");
        // 设备的接入协议，使用NBCoAP/LWM2M
        object.put("protocol", "LWM2M");

        JSONObject object1 = new JSONObject();
        // 按照设备的imei:imsi进行填写
        object1.put(imei, imsi);

        object.put("auth_info", object1);
        // 是否订阅设备资源,默认对资源进行订阅
        object.put("obsv", true);
        // 其他信息
        object.put("other", null);

        String result = post.post(url, object.toString(), host, apiKey);

        JSONObject resultObj = JSONObject.fromObject(result);
        JSONObject returnObj = new JSONObject();
        if (resultObj.getString("errno").equals("0")) {
            returnObj.put("result", true);
            returnObj.put("code", "success");
            returnObj.put("message", "Fsuccess");
            returnObj.put("data",
                    resultObj.getJSONObject("data").getString("device_id"));
        } else {
            returnObj.put("result", false);
            returnObj.put("code", "");
            returnObj.put("message", resultObj.getString("error"));
            returnObj.put("data", "");
        }

        // 将返回内容进行打印显示
        return returnObj.toString();
    }

}
