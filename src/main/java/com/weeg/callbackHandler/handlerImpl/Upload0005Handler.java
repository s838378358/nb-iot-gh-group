package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0005Handler extends Handler {


    @Override
    public JSONObject upload0005Handler(JSONObject object, String mid, String[] binaryData) {
        if ("0005".equals(mid)) {
            //主电电量百分比 R
            object.put("主电电量百分比", binaryData[0]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("0005",this);
    }
}
