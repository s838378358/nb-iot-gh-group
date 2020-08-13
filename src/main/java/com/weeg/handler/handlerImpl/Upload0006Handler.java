package com.weeg.handler.handlerImpl;

import com.weeg.handler.Factory;
import com.weeg.handler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0006Handler extends Handler {


    @Override
    public JSONObject upload0006Handler(JSONObject object, String mid, String[] binaryData) {
        if ("0006".equals(mid)) {
            //备电电压
            String bindata = binaryData[0] + binaryData[1];

            long ct = Long.parseLong(bindata, 16);
            Double evs = Double.valueOf(ct * 1.0 / 1000);
            object.put("备电电压", String.format("%.3f", evs) + "V");
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("0006",this);
    }
}
