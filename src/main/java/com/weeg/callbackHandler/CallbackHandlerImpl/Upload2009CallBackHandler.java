package com.weeg.callbackHandler.CallbackHandlerImpl;

import com.weeg.bean.DevDataLog;
import com.weeg.bean.DevRegInfo;
import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
import com.weeg.model.ResultErrorCode;
import com.weeg.service.DevDataLogService;
import com.weeg.service.DevRegInfoService;
import com.weeg.service.DevSecretKeyService;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by SJ on 2020/8/13
 */
@Component
public class Upload2009CallBackHandler extends CallBackHandler {

    @Override
    public JSONObject upload2009Handler(JSONObject object, String mid, String[] binaryData,String imeikey,DevRegInfoService devRegInfoService, DevDataLogService devDataLogService, DevSecretKeyService devSecretKeyService) {
        if (mid.equals("2009")) {
            String errornum = ResultErrorCode.errorcode(binaryData[0] + binaryData[1]);
            object.put("错误码", errornum);

            //根据IMEI 查询出设备编号
            DevRegInfo devRegInfo = devRegInfoService.selectByImei(imeikey);
            String devserial = devRegInfo.getDevserial();

            //根据设备编号 devserial 查询出 最新的3001 上报信息，获取里面的密钥版本号
            DevDataLog devDataLog = devDataLogService.selectDataByDevserialAndDid(devserial, "3001");
            String j = JSONObject.fromObject(devDataLog.getData()).getString("数据域");
            String keyname = JSONObject.fromObject(j).getString("密钥版本号");

            String errorcode = binaryData[0] + binaryData[1];
            //判断返回的2009 错误码是否是00，00表示下发的2009修改密钥已成功
            if (!"0000".equals(errorcode)) {
                //修改密钥失败，删除非默认的最新修改的版本
                int delnewSecretKey = devSecretKeyService.delNewSecretKey(imeikey, keyname);
                //System.out.println(delnewSecretKey);
            }
        }
        return object;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallbackFactory.register("2009",this);
    }
}
