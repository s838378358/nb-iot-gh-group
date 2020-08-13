package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0017Handler extends Handler {

    @Override
    public JSONObject upload0017Handler(JSONObject object, String mid, String[] binaryData) {
        object.put("模组固件版本", binaryData[0]);
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("0017",this);
    }
}
