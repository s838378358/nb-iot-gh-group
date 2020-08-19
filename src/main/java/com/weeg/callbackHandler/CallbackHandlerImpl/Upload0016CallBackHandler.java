package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.util.DataFomat;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0016CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload0016Handler(JSONObject object, String mid, String[] binaryData) {
        DataFomat dataFomat = new DataFomat();
        if (mid.equals("0016")) {
            // imei转换成字符串
            String imei = "";
            for (int i = 0; i < 15; i++) {
                imei = imei + binaryData[i];
            }
            imei = dataFomat.hexStr2Str(imei);
            object.put("IMEI", imei);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("0016",this);
    }
}
