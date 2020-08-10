package com.weeg.controller;

import com.weeg.util.Post;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by SJ on 2020/8/8
 * 推送OneNet平台的Controller
 */

public class SendToOneNetController {

    //配置日志
    private static final Logger LOG = LoggerFactory.getLogger(SendToOneNetController.class);
    /**
     * 命令下行（写操作） Description: Title: getDeviceInfo
     *
     * @author yuyan
     * @date 2018年6月14日
     */
    public static String postDeviceCmdTou(String params) {
        Post post = new Post();
        /* ........对参数进行处理......... */
        // 将接受到的参数转换成json对象，便于取用
        JSONObject param = JSONObject.fromObject(params);
        String imei = param.getString("imei");
        String cmds = param.getString("cmds");
        // 对应的平台信息是一个json格式的字符串，直接转换成json对象
        JSONObject operator = param.getJSONObject("operator");

        // 获取ca证书存放的地址
        String address = operator.getString("address");
        String host = operator.getString("host");
        String apiKey = operator.getString("apiKey");

        // 拼接请求地址
        String url = address + "/nbiot?imei=" + imei
                + "&obj_id=3202&obj_inst_id=0&mode=1";
        // String url = address + "/nbiot?imei=" + imei
        // + "&obj_id=3202&obj_inst_id=0&mode=2";

        // 拼接请求体
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject object1 = new JSONObject();
        object1.put("val", cmds);
        object1.put("res_id", 1001);
        object1.put("type", 1);
        array.add(object1);
        object.put("data", array);

        String result = null;
        try {
            result = post.post(url, object.toString(), host, apiKey);
        } catch (Exception e) {
            LOG.info("向移动云平台推送异常");
            e.printStackTrace();
        }
        // 将返回内容return
        return result;
    }


}
