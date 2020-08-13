package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import com.weeg.model.ResultErrorCode;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0002Handler extends Handler {


    @Override
    public JSONObject upload0002Handler(String mid, JSONObject object, String hexstr, String[] binaryData) {
        if ("0002".equals(mid)) {
            //时钟 RW
            if ("04".equals(hexstr)) {
                String value = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3] + binaryData[4] + binaryData[5];
                object.put("时钟", value);
            } else if ("05".equals(hexstr)) {
                //错误码
                String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("0002",this);
    }
}
