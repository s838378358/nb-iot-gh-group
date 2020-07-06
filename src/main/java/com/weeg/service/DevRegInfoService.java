package com.weeg.service;

import com.weeg.bean.DevRegInfo;

public interface DevRegInfoService {
    int deleteByPrimaryKey(String imei);

    int insert(DevRegInfo record);

    //根据设备序列号，查询设备信息
    DevRegInfo selectByDevSerial(String devserial);

    int updateByPrimaryKey(DevRegInfo record);
    
    int updateByImei(String iotserial, String imei);
    
    //根据imei号，查询设备信息
    DevRegInfo selectByImei(String imei);

    
    
}