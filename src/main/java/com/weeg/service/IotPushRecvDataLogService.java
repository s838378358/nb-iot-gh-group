package com.weeg.service;

import com.weeg.bean.IotPushRecvDataLog;

public interface IotPushRecvDataLogService {

	int insert(IotPushRecvDataLog record);

    IotPushRecvDataLog selectByClassId(String classid);
}