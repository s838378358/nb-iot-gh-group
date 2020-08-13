package com.weeg.callbackHandler.handlerImpl;

import com.weeg.callbackHandler.Factory;
import com.weeg.callbackHandler.Handler;
import com.weeg.model.EventType;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload1001Handler extends Handler {

    @Override
    public JSONObject upload1001Handler(JSONObject object, String mid, String[] binaryData, String hexstr) {
        if ("1001".equals(mid)) {
            //读最新事件记录条数 转成10进制
            if ("07".equals(hexstr)) {
                //事件记录条数 转成10进制
                int n = Integer.parseInt(binaryData[0], 16);
                object.put("读最新事件记录条数", binaryData[0]);
                //事件记录数据
                String count = "";
                for (int i = 1; i < binaryData.length; i++) {
                    count += binaryData[i];
                }
                if (n > 0) {
                    for (int i = 0; i < n; i++) {
                        //根据事件代码，获取事件名称  eventType
                        String[] s1 = count.substring(i * 18, (i + 1) * 18).split("");
                        object.put("读最新事件类型" + i, EventType.eventType((s1[0] + s1[1] + s1[2] + s1[3])));
                        object.put("读最新事件时间" + i, (s1[4] + s1[5] + s1[6] + s1[7] + s1[8]
                                + s1[9] + s1[10] + s1[11] + s1[12] + s1[13] + s1[14] + s1[15]));
                        object.put("读最新事件详情" + i, s1[16] + s1[17]);
                    }
                } else {
                    //根据事件代码，获取事件名称
                    object.put("读最新事件记录数据", count);
                }
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("1001",this);
    }
}
