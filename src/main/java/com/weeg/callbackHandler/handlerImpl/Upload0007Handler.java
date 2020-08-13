package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload0007Handler extends Handler {

    @Override
    public JSONObject upload0007Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("0007")) {
            //备电电量百分比
            object.put("备电电量百分比", binaryData[0]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("0007",this);
    }
}
