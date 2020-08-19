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
public class Upload2003CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload2003Handler(JSONObject object, String mid, String[] binaryData) {
        DataFomat dataFomat = new DataFomat();
        if (mid.equals("2003")) {
            object.put("表号长度", Integer.parseInt(binaryData[0], 16));
            String devSerial = "";
            for (int i = 1; i < 33; i++) {
                devSerial = devSerial + binaryData[i];
            }
            devSerial = dataFomat.hexStr2Str(devSerial);
            // 第10位表示表号参数的长度
            if (devSerial.length() < 32) {
                for (int j = 0; j < 32; j++) {
                    devSerial += "0";
                }
            }
            object.put("表号内容", devSerial.replaceAll("\\u0000", "0"));
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("2003",this);
    }
}
