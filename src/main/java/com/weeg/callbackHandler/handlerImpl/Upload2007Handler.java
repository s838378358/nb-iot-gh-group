package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import com.weeg.model.ResultErrorCode;
import com.weeg.util.DataFomat;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload2007Handler extends Handler {

    @Override
    public JSONObject upload2007Handler(JSONObject object, String mid, String[] binaryData, String hexstr) {
        DataFomat dataFomat = new DataFomat();
        if (mid.equals("2007")) {
            //java.lang.NumberFormatException: For input string: "3138332e3233302e34302e3339202020"
            if (hexstr.equals("04")) {
                String ip = "";
                for (int i = 0; i < 16; i++) {
                    ip += binaryData[i];
                }
                byte[] ipbytes = dataFomat.toBytes(ip);
                String IP = new String(ipbytes);
                IP = IP.replaceAll(" ", "");
                IP = IP.replaceAll("\\u0000", "");
                object.put("采集服务参数", IP);
//                    String port = dataFomat.hexStr2Str(binaryData[16] + binaryData[17]);
                int port2 = Integer.valueOf(binaryData[16] + binaryData[17], 16);
                object.put("采集服务器端口", port2);
            } else if (hexstr.equals("05")) {
                String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("2007",this);
    }
}
