package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import com.weeg.model.ResultErrorCode;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload200eHandler extends Handler {

    @Override
    public JSONObject upload200eHandler(JSONObject object, String mid, String[] binaryData, String hexstr) {
        if (mid.equals("200e") || mid.equals("200E")) {
            if (hexstr.equals("04")) {
                object.put("错峰间隔时间", binaryData[0] + binaryData[1]);
            } else if (hexstr.equals("05")) {
                String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("200e",this);
    }
}
