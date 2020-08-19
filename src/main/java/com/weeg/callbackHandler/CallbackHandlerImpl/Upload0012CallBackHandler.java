package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0012CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload0012Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("0012")) {
            //通信失败计数R
            object.put("通信失败计数", binaryData[0] + binaryData[1]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("0012",this);
    }
}
