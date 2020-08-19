package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0014CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload0014Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("0014")) {
            String data = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3] + binaryData[4] + binaryData[5];
            if (data.length() < 12) {
                String body = "";
                for (int i = 0; i < 12 - data.length(); i++) {
                    body += "0";
                }
                data = body + data;
                object.put("CellID", data);
            } else {
                object.put("CellID", data);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("0014",this);
    }

}
