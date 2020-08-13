package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0004Handler extends Handler {

    @Override
    public JSONObject upload0004Handler(JSONObject object,String mid,String[] binaryData) {
        if ("0004".equals(mid)) {
            //主电电压 R
            String bindata = binaryData[0] + binaryData[1];

            long ct = Long.parseLong(bindata, 16);
            Double evs = Double.valueOf(ct * 1.0 / 1000);
            object.put("主电电压", String.format("%.3f", evs) + "V");

        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("0004",this);
    }
}
