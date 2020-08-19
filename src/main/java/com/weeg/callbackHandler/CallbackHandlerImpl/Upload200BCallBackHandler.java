package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.callbackHandler.CallbackFactory;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/19
 */
@Component
public class Upload200BCallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload200BHandler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("200b") || mid.equals("200B")) {
            if (binaryData[0].equals("00")) {
                object.put("运营商信息", "电信");
            } else if (binaryData[0].equals("01")) {
                object.put("运营商信息", "移动");
            } else if (binaryData[0].equals("02")) {
                object.put("运营商信息", "联通");
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("200b",this);
    }
}
