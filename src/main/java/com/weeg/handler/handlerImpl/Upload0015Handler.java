package com.weeg.handler.handlerImpl;

import com.weeg.handler.Factory;
import com.weeg.handler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0015Handler extends Handler {


    @Override
    public JSONObject upload0015Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("0015")) {
            object.put("REAL_NEARFCN", binaryData[0] + binaryData[1]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("0015",this);
    }
}
