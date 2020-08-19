package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.model.ResultErrorCode;
import com.weeg.util.DataFomat;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload2012CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload2012Handler(JSONObject object, String mid, String[] binaryData, String hexstr) {
        DataFomat dataFomat = new DataFomat();
        if (mid.equals("2012")) {
            if (hexstr.equals("04")) {
                //APN
                String APN = "";
                for (int i = 0; i < 32; i++) {
                    if ("00".equals(binaryData[i])) {
                        break;
                    } else {
                        APN += binaryData[i];
                    }
                }
                String APNascii = dataFomat.convertHexToString(APN);
                object.put("APN", APNascii.toUpperCase());
            } else if (hexstr.equals("05")) {
                String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("2012",this);
    }
}
