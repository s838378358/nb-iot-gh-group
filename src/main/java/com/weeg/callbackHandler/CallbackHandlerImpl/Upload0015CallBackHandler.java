package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0015CallBackHandler extends CallBackHandler {


    @Override
    public JSONObject upload0015Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("0015")) {
            object.put("REAL_NEARFCN", binaryData[0] + binaryData[1]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("0015",this);
    }
}
