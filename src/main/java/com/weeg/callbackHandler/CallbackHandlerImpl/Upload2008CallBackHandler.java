package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.callbackHandler.CallbackFactory;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/19
 */
@Component
public class Upload2008CallBackHandler extends CallBackHandler {
    @Override
    public JSONObject upload2008Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("2008")) {
            if (binaryData[0].equals("00")) {
                object.put("结算方式", binaryData[0]);
            } else {
                object.put("结算方式", binaryData[0]);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("2008",this);
    }
}
