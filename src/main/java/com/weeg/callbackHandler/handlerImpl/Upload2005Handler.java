package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload2005Handler extends Handler {

    @Override
    public JSONObject upload2005Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("2005")) {
            object.put("通信模式", binaryData[0]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("2005",this);
    }
}
