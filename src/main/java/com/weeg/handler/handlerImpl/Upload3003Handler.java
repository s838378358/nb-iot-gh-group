package com.weeg.handler.handlerImpl;

import com.weeg.handler.Factory;
import com.weeg.handler.Handler;
import com.weeg.util.AESUtil;
import com.weeg.util.DataFomat;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/10
 */
@Component
public class Upload3003Handler extends Handler {

    /**
     * 3003上报处理的业务逻辑
     * @param binaryData
     * @param b
     * @param object
     * @return
     */
    @Override
    public JSONObject upload3003Handler(String[] binaryData, byte[] b, JSONObject object,String keyvalue) {
        DataFomat dataFomat = new DataFomat();
        AESUtil aesUtil = new AESUtil();
        // 获取明文
        String body = "";
        int length = b.length / 16;

//        byte[] resultBytes = new byte[b.length];
        // 将密文按照16的长度进行分割
        for (int i = 0; i < length; i++) {
            // 创建一个长度是16的数组,用于存放每一段数组
            byte[] newTxet = new byte[16];
            for (int j = 0; j < 16; j++) {
                newTxet[j] = b[i * 16 + j];
            }
            String originalString = dataFomat.bytes2HexString(aesUtil.decryptAES(newTxet, keyvalue));
            body = body + originalString;
        }
        // 去掉body中的空格
        body = body.replace(" ", "");

        // 将获取的数据域转换成byte[]
        byte[] b2 = dataFomat.toBytes(body);

        // 将byte转换成string
//        String strdata3 = "";
        String[] binaryData3 = new String[b2.length];
        for (int i = 0; i < b2.length; i++) {
            binaryData3[i] = dataFomat.toHex(b2[i]);
//            strdata3 += binaryData3[i];
        }
//            System.out.println(strdata3);

        // 1、时钟
        object.put("时钟", binaryData3[0] + binaryData3[1] + binaryData3[2] + binaryData3[3] + binaryData3[4]
                + binaryData3[5]);

        // 2、当前累计气量
        String gas = "";
        for (int i = 6; i < 10; i++) {
            gas += binaryData3[i];
        }
//                int gass = Integer.parseInt(gas, 16) / 1000;
        long gass = Long.parseLong(gas, 16) / 1000;
//                System.out.println(gass);
        object.put("当前累计气量", gass);

        // 3、表状态

        byte[] binarybyte1 = dataFomat.toBytes(binaryData3[10]);
        byte[] binarybyte2 = dataFomat.toBytes(binaryData3[11]);
        byte data1 = binarybyte1[0];
        byte data2 = binarybyte2[0];
        String bit1 = dataFomat.byteToBit(data1);
        String bit2 = dataFomat.byteToBit(data2);
        char[] bit3 = bit1.toCharArray();
        char[] bit4 = bit2.toCharArray();
        String[] stb1 = new String[bit3.length];
        String[] stb2 = new String[bit4.length];
        for (int i = stb1.length - 1; i >= 0; i--) {
            stb1[stb1.length - i - 1] = String.valueOf(bit3[i]);
            stb2[stb1.length - i - 1] = String.valueOf(bit4[i]);
        }
//				for (int i = 0; i < stb1.length; i++) {
//					stb1[i] = String.valueOf(bit3[i]);
//					stb2[i] = String.valueOf(bit4[i]);
//				}
        // System.out.println(bit1+","+bit2);
        JSONObject btypeobject = new JSONObject();
        btypeobject.put("阀门状态", stb1[0]);
        btypeobject.put("表具被强制命令关阀", stb1[1]);
        btypeobject.put("主电电量不足", stb1[2]);
        btypeobject.put("备电电量不足", stb1[3]);
        btypeobject.put("无备电，系统不能正常工作", stb1[4]);
        btypeobject.put("过流", stb1[5]);
        btypeobject.put("阀门直通", stb1[6]);
        btypeobject.put("外部报警触发", stb1[7]);

        btypeobject.put("计量模块异常", stb2[0]);
        btypeobject.put("多少天不用气导致阀门关闭", stb2[1]);
        btypeobject.put("曾出现多天没有远传数据上发成功而导致阀门关闭", stb2[2]);
        btypeobject.put("电磁干扰", stb2[3]);
        btypeobject.put("未定义4", stb2[4]);
        btypeobject.put("未定义5", stb2[5]);
        btypeobject.put("未定义6", stb2[6]);
        btypeobject.put("未定义7", stb2[7]);

        object.put("表状态", btypeobject);

        // 4、NB网络信号强度
        object.put("NB网络信号强度RSRP", DataFomat.BigEndian(b2, 12, 2));
        object.put("NB网络信号强度SNR", DataFomat.BigEndian(b2, 14, 2));

        // 5、表厂自定义表状态
//                String tableStatus = "";
//                for (int i = 16; i < 20; i++) {
//                    tableStatus += binaryData3[i];
//                }
//            byte[] bytes = dataFomat.toBytes(binaryData3[16]);
//            byte byte1 = bytes[0];
//
//            //byte1 & 0x03
//            String byte1tobit = dataFomat.byteToBit(byte1);
//            char[] bittochar = byte1tobit.toCharArray();
//            String[] stb3 = new String[bittochar.length];
//            for (int i = 0; i < stb1.length; i++) {
//                stb3[i] = String.valueOf(bittochar[i]);
//            }
        JSONObject stb3object = new JSONObject();

        if("00".equals(binaryData3[16])){
            stb3object.put("阀门状态","开阀");
        }else if ("01".equals(binaryData3[16])){
            stb3object.put("阀门状态","关阀");
        }else if ("03".equals(binaryData3[16])){
            stb3object.put("阀门状态","异常");
        }
        object.put("表厂自定义表状态",stb3object);

        // 6、供电类型
        long powerType = Long.parseLong(binaryData3[20], 16);
        object.put("供电类型", powerType);

        // 7、主电电池电压
        String EV = "";
        for (int i = 21; i < 23; i++) {
            EV += binaryData3[i];
        }
        long EVs = Long.parseLong(EV, 16) > 0 ? Long.parseLong(EV, 16) : 0;
        Float Evs = Float.valueOf(EVs) / 1000F;
        object.put("主电电池电压", Evs);

        // 8、主电电池百分比
        object.put("主电电池百分比", Long.parseLong(binaryData3[23], 16));

        // 9、昨天每小时用气日志
        String yq = "";
        for (int i = 28; i < 124; i++) {
            yq += binaryData3[i];
        }
        object.put("昨天每小时用气日志", binaryData3[24] + binaryData3[25] + binaryData3[26] + binaryData3[27] + yq);

        // 10、前5天日用气记录
        String fd = "";
        for (int i = 128; i < 148; i++) {
            fd += binaryData3[i];
        }
        object.put("前5天日用气记录", binaryData3[124] + binaryData3[125] + binaryData3[126] + binaryData3[127] + fd);

        return object;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("3003",this);
    }
}
