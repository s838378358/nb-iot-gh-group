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
public class Upload000DCallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload000dHandler(JSONObject object, String mid, String[] binaryData, String hexstr,String content) {
        if ("000d".equals(mid) || "000D".equals(mid)) {
            //单价
            if (hexstr.equals("04")) {
                if (content.equals("00000000")) {
                    object.put("单价", content);
                } else {
                    String bindata = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3];
                    long ct = Long.parseLong(bindata, 16);
                    Double evs = Double.valueOf(ct * 1.0 / 10000);
                    object.put("单价", String.format("%.4f", evs));
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
        CallbackFactory.register("000d",this);
    }
}
