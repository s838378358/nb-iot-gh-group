package com.weeg.handler.handlerImpl;

import com.weeg.handler.Factory;
import com.weeg.handler.Handler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0012Handler extends Handler {

    @Override
    public JSONObject upload0012Handler(JSONObject object, String mid, String[] binaryData) {
        if (mid.equals("0012")) {
            //通信失败计数R
            object.put("通信失败计数", binaryData[0] + binaryData[1]);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("0012",this);
    }
}
