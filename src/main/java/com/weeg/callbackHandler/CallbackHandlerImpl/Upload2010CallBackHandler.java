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
public class Upload2010CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload2010Handler(JSONObject object, String mid, String[] binaryData, String hexstr) {
        if (mid.equals("2010")) {
            if (hexstr.equals("04")) {
                object.put("多天不上传关阀控制", binaryData[0]);
            } else if (hexstr.equals("05")) {
                String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("2010",this);
    }
}
