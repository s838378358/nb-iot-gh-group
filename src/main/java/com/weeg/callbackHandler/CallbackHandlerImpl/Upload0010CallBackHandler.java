package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.model.ResultErrorCode;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0010CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload0010Handler(JSONObject object, String mid, String[] binaryData, String hexstr) {
        if ("0010".equals(mid)) {
            //余量状态RW
            if (hexstr.equals("04")) {
                object.put("余量状态", binaryData[0]);
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("0010",this);
    }
}
