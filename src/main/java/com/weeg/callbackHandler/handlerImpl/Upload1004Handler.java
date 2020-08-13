package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import com.weeg.util.AESUtil;
import com.weeg.util.DataFomat;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload1004Handler extends Handler {

    @Override
    public JSONObject upload1004Handler(JSONObject object, String mid,byte[] b, String[] binaryData, String hexstr,String keyvalue) {
        DataFomat dataFomat = new DataFomat();
        AESUtil aesUtil = new AESUtil();
        if (mid.equals("1004")) {
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

                int daynum = Integer.parseInt(binaryData3[3], 16);

                String bindata = "";
                for (int i = 4; i < binaryData3.length; i++) {
                    bindata += binaryData3[i];
                }
                object.put("读日用气记录起始日期", binaryData3[0] + binaryData3[1] + binaryData3[2]);
                object.put("读日用气记录天数", binaryData3[3]);
                JSONArray array = new JSONArray();
                for (int i = 0; i < daynum; i++) {
                    JSONObject dataobject = new JSONObject();
                    String s1 = bindata.substring(i * 8, (i + 1) * 8);
                    long n1 = Long.parseLong(s1, 16);
                    double d1 = Double.valueOf(n1 * 1.0) / 1000;
                    dataobject.put("读日用气累计量", String.format("%.3f", d1));
                    array.add(dataobject);
                }
                object.put("数据域", array);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("1004",this);
    }
}
