package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload000cHandler extends Handler {

    @Override
    public JSONObject upload000cHandler(JSONObject object, String mid, String[] binaryData) {
        if ("000c".equals(mid) || "000C".equals(mid)) {
            String suijima = "";
            for (int i = 0; i < 16; i++) {
                suijima += binaryData[i];
            }
            object.put("通信随机码", suijima);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("000c",this);
    }
}
