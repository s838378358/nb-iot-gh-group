package com.weeg.service;

import com.weeg.bean.IotPushExceptionLogWithBLOBs;

public interface IotPushExceptionLogService {
    int insert(IotPushExceptionLogWithBLOBs record);

    int insertSelective(IotPushExceptionLogWithBLOBs record);
}