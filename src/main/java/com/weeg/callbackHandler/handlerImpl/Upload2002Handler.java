package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload2002Handler extends Handler {

    @Override
    public JSONObject upload2002Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("2002")) {
            object.put("表型号", binaryData[0] + binaryData[1]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("2002",this);
    }
}
