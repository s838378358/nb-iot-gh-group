package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload2005CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload2005Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("2005")) {
            object.put("通信模式", binaryData[0]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("2005",this);
    }
}
