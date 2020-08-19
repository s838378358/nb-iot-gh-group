package com.weeg.controller;

import com.weeg.util.Post;
import net.sf.json.JSONObject;

/**
 * Created by SJ on 2020/8/14
 */
public class RemoveDeviceController {

    /**
     * 删除设备
     *
     * @author yuyan
     * @date 2018年6月19日
     */
    public static String deleteDevice(String params) {
        /* ........对参数进行处理......... */
        // 将接受到的参数转换成json对象，便于取用
        JSONObject param = JSONObject.fromObject(params);
        String deviceId = param.getString("NBId");
        // 对应的平台信息是一个json格式的字符串，直接转换成json对象
        JSONObject operator = param.getJSONObject("operator");

        // 获取ca证书存放的地址
        String address = operator.getString("address");
        String host = operator.getString("host");
        String apiKey = operator.getString("apiKey");

        // 拼接请求地址
        String url = address + "/devices/" + deviceId;
        // 发起带有请求头部的网络请求
        String result = Post.delete(url, host, apiKey);

        JSONObject retObj = new JSONObject();

        if (JSONObject.fromObject(result).getString("errno").equals("0")) {
            retObj.put("result", true);
            retObj.put("message", "设备删除成功");
            retObj.put("code", "");
            retObj.put("data", "");
        } else {
            retObj.put("result", false);
            retObj.put("message",
                    JSONObject.fromObject(result).getString("error"));
            retObj.put("code", "");
            retObj.put("data", "");
        }

        // 将返回内容进行打印显示
        return retObj.toString();
    }

}
