package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.util.AESUtil;
import com.weeg.util.DataFomat;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/19
 */
@Component
public class Upload1006CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload1006Handler(JSONObject object, String mid, String[] binaryData, String hexstr, byte[] b, String keyvalue) {
        DataFomat dataFomat = new DataFomat();
        AESUtil aesUtil = new AESUtil();
        if (mid.equals("1006")) {
            if (hexstr.equals("07")) {
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
                for (int i = 0; i < binaryData3.length; i++) {
                    binaryData3[i] = dataFomat.toHex(b2[i]);
                }

                int year = Integer.valueOf(binaryData3[0]);
                object.put("读月用气记录年份", year);
                String bindata = "";
                for (int i = 1; i < binaryData3.length; i++) {
                    bindata += binaryData3[i];
                }
                JSONArray array = new JSONArray();
                int m = 1;
                for (int i = 0; i < 12; i++) {
                    JSONObject dataobject = new JSONObject();
                    String[] s1 = bindata.substring(i * 8, (i + 1) * 8).split("");
                    long n1 = Long.parseLong(s1[0] + s1[1] + s1[2] + s1[3] + s1[4] + s1[5] + s1[6] + s1[7], 16);
                    double d1 = Double.valueOf(n1 * 1.0) / 1000;
                    dataobject.put("第" + m + "个月用气累计量", String.format("%.3f", d1));
                    array.add(dataobject);
                    m++;
                }
                object.put("数据域", array);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("1006",this);
    }
}
