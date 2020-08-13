package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload2004Handler extends Handler {

    @Override
    public JSONObject upload2004Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("2004")) {
            object.put("带阀门", binaryData[0]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("2004",this);
    }
}
