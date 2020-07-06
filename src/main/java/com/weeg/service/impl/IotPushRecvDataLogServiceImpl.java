package com.weeg.service.impl;

import com.weeg.bean.IotPushRecvDataLog;
import com.weeg.dao.IotPushRecvDataLogMapper;
import com.weeg.service.IotPushRecvDataLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IotPushRecvDataLogServiceImpl implements IotPushRecvDataLogService {

	@Autowired
    private IotPushRecvDataLogMapper iotPushRecvDataLogMapper;

	@Override
	public IotPushRecvDataLog selectByClassId(String classid) {
		// TODO Auto-generated method stub
		return iotPushRecvDataLogMapper.selectByClassId(classid);
	}

	@Override
	public int insert(IotPushRecvDataLog recvDataLog) {
		// TODO Auto-generated method stub
		return iotPushRecvDataLogMapper.insert(recvDataLog);
	}
	
}