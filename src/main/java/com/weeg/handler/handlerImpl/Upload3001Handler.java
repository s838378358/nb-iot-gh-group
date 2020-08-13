package com.weeg.handler.handlerImpl;


import com.weeg.handler.Factory;
import com.weeg.handler.Handler;
import com.weeg.util.DataFomat;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/10
 */
@Component
public class Upload3001Handler extends Handler {

    /**
     * 3001上报处理的业务逻辑
     * @param binaryData
     * @param b
     * @param object
     * @return
     */
    @Override
    public JSONObject upload3001Handler(String[] binaryData, byte[] b, JSONObject object) {
        DataFomat dataFomat = new DataFomat();
        String devSerial = "";
        // 第10位表示表号参数的长度
        for (int i = 11; i < 42; i++) {
            devSerial = devSerial + binaryData[i];
        }
        devSerial = dataFomat.hexStr2Str(devSerial);

        // 模组型号
        String modelType = "";
        for (int i = 44; i < 54; i++) {
            if ("00".equals(binaryData[i])) {
                break;
            } else {
                modelType += binaryData[i];
            }
        }
        String modelTypeascii = dataFomat.convertHexToString(modelType);

        // 通信随机码
        String tongxinsuijima = "";
        for (int i = 61; i < 77; i++) {
            tongxinsuijima = tongxinsuijima + binaryData[i];
        }

        String Celled = "";
        for (int i = 82; i < 88; i++) {
            Celled = Celled + binaryData[i];
        }

        // imei转换成字符串
        String imei = "";
        for (int i = 90; i < 105; i++) {
            imei = imei + binaryData[i];
        }
        imei = dataFomat.hexStr2Str(imei);

        object.put("时钟",
                binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3] + binaryData[4] + binaryData[5]);
        object.put("表厂商ID", binaryData[6] + binaryData[7]);
        object.put("表型号", binaryData[8] + binaryData[9]);
        // 表号参数转换成十进制
        object.put("表号长度", Integer.parseInt(binaryData[10], 16));
        object.put("表号参数", devSerial.replaceAll("\\u0000", ""));
        object.put("模组厂商ID", binaryData[43]);
        object.put("模组型号", modelTypeascii);
//                System.out.println("模组型号：" + modelTypeascii);
        object.put("开户状态", binaryData[54]);
        object.put("运营商信息", binaryData[55]);
        object.put("通信模式", binaryData[56]);
        object.put("嵌软版本", binaryData[57] + binaryData[58]);
        object.put("应用协议版本", binaryData[59] + binaryData[60]);
        object.put("通信随机码", tongxinsuijima);
        // 信号强度转换成有符号整数
        object.put("NB网络信号强度RSRP", DataFomat.BigEndian(b, 77, 2));
        object.put("NB网络信号强度SNR", DataFomat.BigEndian(b, 79, 2));
        // 信噪比转换成十进制
//            object.put("信噪比", DataFomat.BigEndian(b, 79, 2));
        object.put("ECL覆盖等级", Integer.valueOf(binaryData[81], 16));
        object.put("Celled", Celled);
        // REAL_NEARFCN转换成十进制
        object.put("REAL_NEARFCN", Integer.parseInt(binaryData[88] + binaryData[89], 16));
        object.put("IMEI", imei);
        object.put("模组固件版本", Integer.parseInt(binaryData[105], 16));
        object.put("密钥版本号", binaryData[106]);

        return object;
    }


    /**
     * 注册handler
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("3001",this);
    }
}
