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
public class Upload000eHandler extends Handler {

    @Override
    public JSONObject upload000eHandler(JSONObject object, String mid, String[] binaryData,String hexstr) {
        if ("000e".equals(mid) || "000E".equals(mid)) {
            //开户状态RW
            if (hexstr.equals("04")) {
                object.put("开户状态", binaryData[0]);
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
//        } else if ("0010".equals(mid)) {
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("000e",this);
    }
}
