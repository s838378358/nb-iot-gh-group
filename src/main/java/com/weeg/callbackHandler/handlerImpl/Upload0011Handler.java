package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0011Handler extends Handler {

    @Override
    public JSONObject upload0011Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("0011")) {
            //NB网络信号强度R
            object.put("RSRP", binaryData[0] + binaryData[1]);
            object.put("SNR", binaryData[2] + binaryData[3]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("0011",this);
    }
}
