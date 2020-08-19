package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.util.DataFomat;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload200ACallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload200aHandler(JSONObject object, String mid, String[] binaryData) {
        DataFomat dataFomat = new DataFomat();
        if (mid.equals("200a") || mid.equals("200A")) {
            String SIM = "";
            for (int i = 0; i < 20; i++) {
                SIM += binaryData[i];
            }
            String SIMascii = dataFomat.convertHexToString(SIM);
            object.put("SIM卡信息", SIMascii);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("200a",this);
    }
}
