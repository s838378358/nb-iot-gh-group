package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0017CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload0017Handler(JSONObject object, String mid, String[] binaryData) {
        object.put("模组固件版本", binaryData[0]);
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("0017",this);
    }
}
