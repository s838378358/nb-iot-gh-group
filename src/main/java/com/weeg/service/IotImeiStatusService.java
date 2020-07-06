package com.weeg.service;

import com.weeg.bean.IotImeiStatus;

public interface IotImeiStatusService {
	//根据设备序列号查询设备在线状态
    IotImeiStatus selectBySerial(String serial);
    
    int insert(IotImeiStatus record);
    
    int updateStatus(String status, String imei);
    
    IotImeiStatus selectByimei(String imei);

    int deleteStatusByImei(String imei);
}