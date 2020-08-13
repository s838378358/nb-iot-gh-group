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
public class Upload0008Handler extends Handler {

    @Override
    public JSONObject upload0008Handler(JSONObject object, String mid, String[] binaryData, String hexstr,String content) {
        if (mid.equals("0008")) {
            //预留量
            //判断是读数据--->上行(04)
            if (hexstr.equals("04")) {
                if (content.equals("00000000")) {
                    object.put("预留量", content);
                } else {
                    String bindata = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3];
                    long ct = Long.parseLong(bindata, 16);
                    Double evs = Double.valueOf(ct * 1.0 / 1000);
                    object.put("预留量", String.format("%.3f", evs));
                }
                //判断是写数据--->上行(05)
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
        Factory.register("0008",this);
    }
}
