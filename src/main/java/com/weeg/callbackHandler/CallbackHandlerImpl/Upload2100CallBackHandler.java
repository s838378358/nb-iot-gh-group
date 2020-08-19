package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.model.ResultErrorCode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/19
 */
@Component
public class Upload2100CallBackHandler extends CallBackHandler {
    @Override
    public JSONObject upload2100Handler(JSONObject object, String mid, String[] binaryData,String hexstr) {
        if (mid.equals("2100")) {
            if (hexstr.equals("04")) {
                //获取明文
                int daynum = Integer.parseInt(binaryData[0], 16);
                object.put("每日上传时间次数", daynum);

                String bindata = "";
                for (int i = 1; i < binaryData.length; i++) {
                    bindata += binaryData[i];
                }
                int m = 1;
                JSONArray array = new JSONArray();
                for (int i = 0; i < daynum; i++) {
                    JSONObject dataobject = new JSONObject();
                    String[] s1 = bindata.substring(i * 4, (i + 1) * 4).split("");
                    dataobject.put("上传时" + m, s1[0] + s1[1]);
                    dataobject.put("上传分" + m, s1[2] + s1[3]);
                    array.add(dataobject);
                    m++;
                }
                object.put("数据域", array);
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
        CallbackFactory.register("2100",this);
    }
}
