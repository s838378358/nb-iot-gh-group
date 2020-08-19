package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.model.ResultErrorCode;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload2020CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload2020Handler(JSONObject object, String mid, String[] binaryData, String hexstr) {
        if (mid.equals("2020")) {
            if (hexstr.equals("04")) {
                object.put("液晶显示", binaryData[0]);
            } else if (hexstr.equals("05")) {
                String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("2020",this);
    }
}
