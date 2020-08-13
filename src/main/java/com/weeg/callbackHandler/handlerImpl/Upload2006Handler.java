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
public class Upload2006Handler extends Handler {

    @Override
    public JSONObject upload2006Handler(JSONObject object, String mid, String[] binaryData, String hexstr) {
        if (mid.equals("2006")) {
            if (hexstr.equals("04")) {
                object.put("定时上传天数/月份区分位", binaryData[0]);
                object.put("定时上传周期值", binaryData[1]);
                object.put("定时上传时间/时", binaryData[2]);
                object.put("定时上传时间/分", binaryData[3]);
            } else if (hexstr.equals("05")) {
                String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("2006",this);
    }
}
