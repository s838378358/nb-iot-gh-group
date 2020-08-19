package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.model.ResultErrorCode;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/19
 */
@Component
public class Upload000FCallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload000fHandler(JSONObject object, String mid, String[] binaryData, String hexstr, String content) {
        if (mid.equals("000f") || mid.equals("000F")) {
            if (hexstr.equals("04")) {
                if (content.equals("00000000")) {
                    object.put("剩余金额", content);
                } else {
                    String RA = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3];
                    int ra = Integer.valueOf(RA, 16);
                    Double evs = Double.valueOf(ra * 1.0 / 100);
                    object.put("剩余金额", String.format("%.2f", evs));
                }
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
        CallbackFactory.register("000f",this);
    }
}
