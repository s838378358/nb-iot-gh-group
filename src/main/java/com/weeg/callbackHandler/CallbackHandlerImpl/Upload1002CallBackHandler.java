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
public class Upload1002CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload1002Handler(JSONObject object, String mid, String[] binaryData, String hexstr, byte[] b, String keyvalue) {
        DataFomat dataFomat = new DataFomat();
        AESUtil aesUtil = new AESUtil();
        if ("1002".equals(mid)) {
            if ("07".equals(hexstr)) {
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

                int daynum = Integer.parseInt(binaryData3[3], 16);

                String bindata = "";
                for (int i = 4; i < binaryData3.length; i++) {
                    bindata += binaryData3[i];
                }
                JSONArray array = new JSONArray();
                for (int i = 0; i < daynum; i++) {
                    JSONObject dataobject = new JSONObject();
                    dataobject.put("读每小时用气记录日期", binaryData3[0] + binaryData3[1] + binaryData3[2]);
                    dataobject.put("读每小时用气记录天数", binaryData3[3]);
                    array.add(dataobject);
                    String s1 = bindata.substring(i * 192, (i + 1) * 192);
                    String[] s2 = s1.split("");
                    int m = 1;
                    for (int j = 0; j < 192; j = j + 8) {
                        JSONObject db = new JSONObject();
                        long n1 = Long.parseLong(s2[j] + s2[j + 1] + s2[j + 2] + s2[j + 3] + s2[j + 4] + s2[j + 5] + s2[j + 6] + s2[j + 7], 16);
                        double d1 = Double.valueOf(n1 * 1.0) / 1000;
//                            double d1 = Double.valueOf(n1 * 1.0 /1000);
                        db.put("第" + m + "小时用气量", String.format("%.3f", d1));
                        m++;
                        array.add(db);
                    }
                }
                object.put("数据域", array);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("1002",this);
    }
}
