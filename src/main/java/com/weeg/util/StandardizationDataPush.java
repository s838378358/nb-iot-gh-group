package com.weeg.util;


import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SJ on 2020/7/13
 * 标准化数据推送信息
 */
public class StandardizationDataPush {
    static Post post = new Post();
    //读取配置文件
    static Props dataprops = PropsUtil.get("properties/data.properties");

    /**
     * 标准化数据推送
     *
     * @return
     */

    public static boolean dataPush(Map<String,Object> data, String devserial) {

        JSONObject jsonObject = new JSONObject();

        //数据类型 NB默认的是8
        jsonObject.put("dataType", "8");
        //驱动号 唯一 NB默认的是 000001
        jsonObject.put("driveId", "000001");
        //设备号
        jsonObject.put("drvFlag1", devserial);
        //通道号 NB默认的是 8990
        jsonObject.put("drvFlag2", "8990");
        //标准化数据
        List<Map<String,Object>> list =  StandardizationData(data);
        jsonObject.put("realDataVos", list);

        //将数据标准化推送给weegDat
        String weegDatStandardizedRealDataUrl = dataprops.getStr("weegDatStandardizedRealData");
        //请求
        String weegDatRegistResult = post.post(weegDatStandardizedRealDataUrl, jsonObject.toString());

        Ok ok = JSONUtil.toBean(weegDatRegistResult, Ok.class);
        if ("00000".equals(ok.getErrorCode2())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 格式化标准数据，将最新的数据放入map
     * @param data
     * 1:无线信号强度
     * 4:标况累计体积
     * 5:电池电压
     * 6:结算日体积
     * 7:阀位状态
     * @return
     */
    public static List<Map<String,Object>> StandardizationData(Map<String, Object> data){
        List<Map<String,Object>> list = new ArrayList<>();

        //无线信号强度RSRP
        String WSSrsrp = operationUtil.getString(data,"WSSrsrp");
        //无线信号强度SNR
        String WSSsnr = operationUtil.getString(data,"WSSsnr");
        //标况累计体积
        String CVISC = operationUtil.getString(data,"CVISC");
        //电池电压
        String BV = operationUtil.getString(data,"BV");
        //结算日体积
        String SDV = operationUtil.getString(data,"SDV");
        //阀门状态
        String SOTV = operationUtil.getString(data,"SOTV");

        for (int i = 0; i < 36; i++) {
            Map<String,Object> map = new HashMap<>(2);
            map.put("offSet",i);
            if (i == 1){
                map.put("value",WSSrsrp);
            }else if (i == 4){
                map.put("value",CVISC);
            }else if (i == 5){
                map.put("value",BV);
            }else if (i == 6){
                map.put("value",SDV);
            }else if (i == 7){
                map.put("value",SOTV);
            }else if (i == 35){
                map.put("value",WSSsnr);
            }else {
                map.put("value","");
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 格式化数据类型
     */
    public Map<String,Object> dataMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("WSS","");
        map.put("CVISC","");
        map.put("BV","");
        map.put("SDV","");
        map.put("SOTV","");
        return map;
    }
}
