package com.weeg.handler.handlerImpl;

import com.weeg.handler.Factory;
import com.weeg.handler.Handler;
import com.weeg.util.DataFomat;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/12
 */
@Component
public class Upload000bHandler extends Handler {

    @Override
    public JSONObject upload000bHandler(JSONObject object, String mid, String[] binaryData) {
        DataFomat dataFomat = new DataFomat();
        if ("000b".equals(mid) || "000B".equals(mid)) {
            byte[] binarybyte1 = dataFomat.toBytes(binaryData[0]);
            byte[] binarybyte2 = dataFomat.toBytes(binaryData[1]);
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
            JSONObject btypeobject = new JSONObject();
            //第一位
            btypeobject.put("阀门状态", stb1[0]);
            btypeobject.put("表具被强制命令关阀", stb1[1]);
            btypeobject.put("主电电量不足", stb1[2]);
            btypeobject.put("备电电量不足", stb1[3]);
            btypeobject.put("无备电，系统不能正常工作", stb1[4]);
            btypeobject.put("过流", stb1[5]);
            btypeobject.put("阀门直通", stb1[6]);
            btypeobject.put("外部报警触发", stb1[7]);
            //第二位
            btypeobject.put("计量模块异常", stb2[0]);
            btypeobject.put("多少天不用气导致阀门关闭", stb2[1]);
            btypeobject.put("曾出现多天没有远传数据上发成功而导致阀门关闭", stb2[2]);
            btypeobject.put("电磁干扰", stb2[3]);
            btypeobject.put("面罩拆卸", stb2[4]);
            btypeobject.put("未定义5", stb2[5]);
            btypeobject.put("未定义6", stb2[6]);
            btypeobject.put("未定义7", stb2[7]);

            object.put("表状态", btypeobject);
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("000b",this);
    }
}
