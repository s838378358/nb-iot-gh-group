package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.model.ResultErrorCode;
import com.weeg.util.AESUtil;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/19
 */
@Component
public class Upload0101CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload0101Handler(JSONObject object, String mid, String[] binaryData, String hexstr,byte[] b,String keyvalue) {
        if (mid.equals("0101")) {
            if (hexstr.equals("04")) {
                String[] binarydata3 = AESUtil.decryptAESstr(b, keyvalue);
                String bindata = binarydata3[0] + binarydata3[1] + binarydata3[2] + binarydata3[3];
                long n = Long.parseLong(bindata, 16);
                Double evs = Double.valueOf(n * 1.0 / 1000);
                object.put("当前工况累积量", String.format("%.3f", evs));
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
        CallbackFactory.register("0101",this);
    }
}
