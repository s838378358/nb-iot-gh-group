package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.util.AESUtil;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/19
 */
@Component
public class Upload0105CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload0105Handler(JSONObject object, String mid, String[] binaryData, String hexstr, byte[] b, String keyvalue) {
        if (mid.equals("0105")) {
            if (hexstr.equals("04")) {
                String[] binarydata3 = AESUtil.decryptAESstr(b, keyvalue);
                String bindata = binarydata3[0] + binarydata3[1] + binarydata3[2] + binarydata3[3];
                long n = Long.parseLong(bindata, 16);
                Double evs = Double.valueOf(n * 1.0 / 1000);
                object.put("当前压力", String.format("%.3f", evs));
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("0105",this);
    }
}
