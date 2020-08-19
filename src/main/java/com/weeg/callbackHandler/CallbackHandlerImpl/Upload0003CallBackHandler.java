package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.model.ResultErrorCode;
import com.weeg.util.AESUtil;
import com.weeg.util.DataFomat;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload0003CallBackHandler extends CallBackHandler {


    @Override
    public JSONObject upload0003Handler(byte[] b,String mid, JSONObject object, String hexstr, String[] binaryData,String keyvalue) {
        DataFomat dataFomat = new DataFomat();
        AESUtil aesUtil = new AESUtil();
        if ("0003".equals(mid)) {
            //当前累计气量 R
            //判断是读数据--->上行(04)
            if ("04".equals(hexstr)) {
                //解密  获取明文
                String body = "";
                int length = b.length;
                int cd;
                if (length % 16 == 0) {
                    cd = length / 16;
                } else {
                    cd = length / 16 + 1;
                }
                for (int i = 0; i < cd; i++) {
                    // 创建一个长度是16的数组,用于存放每一段数组
                    byte[] newTxet = new byte[16];
                    for (int j = 0; j < 16; j++) {
                        if ((i * 16 + j) <= length - 1) {
                            newTxet[j] = b[i * 16 + j];
                        } else {
                            newTxet[j] = 0;
                        }
                    }
                    String originalString = dataFomat.bytes2HexString(aesUtil.decryptAES(newTxet, keyvalue));
                    body = body + originalString;
                }
                // 去掉body中的空格
                body = body.replaceAll(" ", "");
                // 将获取的数据域转换成byte[]
                byte[] b2 = dataFomat.toBytes(body);

                // 将byte转换成string
                String[] binaryData3 = new String[b2.length];
                for (int i = 0; i < b2.length; i++) {
                    binaryData3[i] = dataFomat.toHex(b2[i]);
                }
                String bindata = binaryData3[0] + binaryData3[1] + binaryData3[2] + binaryData3[3];

                long ct = Long.parseLong(bindata, 16);
                Double evs = Double.valueOf(ct * 1.0 / 1000);
                object.put("当前累计气量", String.format("%.3f", evs));

                //判断是写数据--->上行(05)
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
        CallbackFactory.register("0003",this);
    }
}
