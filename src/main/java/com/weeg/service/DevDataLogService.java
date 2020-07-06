package com.weeg.service;

import com.weeg.bean.DevDataLog;

public interface DevDataLogService {
	int insert(DevDataLog record);
	
    DevDataLog selectByChildclassId(String childclassid);

    //根据设备编号 devserial 查询出 最新的3001 上报信息
    DevDataLog selectDataByDevserialAndDid(String devserial, String did);
}