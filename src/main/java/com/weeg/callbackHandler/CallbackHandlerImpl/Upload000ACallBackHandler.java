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
public class Upload000ACallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload000aHandler(JSONObject object, String mid, String[] binaryData, String hexstr) {
        if (mid.equals("000a") || mid.equals("000A")) {
            //透支状态
            if (hexstr.equals("04")) {
                object.put("透支状态", binaryData[0]);
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
        CallbackFactory.register("000a",this);
    }
}
