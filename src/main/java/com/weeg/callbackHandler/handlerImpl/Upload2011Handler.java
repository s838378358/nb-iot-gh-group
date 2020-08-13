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
public class Upload2011Handler extends Handler {

    @Override
    public JSONObject upload2011Handler(JSONObject object, String mid, String[] binaryData, String hexstr) {
        if (mid.equals("2011")) {
            if (hexstr.equals("04")) {
                object.put("过流报警使能", binaryData[0]);
            } else if (hexstr.equals("05")) {
                String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("2011",this);
    }
}
