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
public class Upload0001Handler extends Handler {

    @Override
    public JSONObject upload0001Handler(String mid, JSONObject object, String hexstr, String[] binaryData) {
        if ("0001".equals(mid)) {
            //阀门状态 RW
            if ("04".equals(hexstr)) {
                object.put("阀门状态", binaryData[0]);
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
        Factory.register("0001",this);
    }
}
