package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0013CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload0013Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("0013")) {
            //ECL覆盖等级R
            object.put("ECL覆盖等级", binaryData[0]);
            //object.put("ECL覆盖等级", binaryData[0] + binaryData[1]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("0013",this);
    }
}
