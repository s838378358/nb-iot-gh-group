package com.weeg.service.impl;

import com.weeg.bean.DevDataLog;
import com.weeg.dao.DevDataLogMapper;
import com.weeg.service.DevDataLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevDataLogServiceImpl implements DevDataLogService {

	@Autowired
    private DevDataLogMapper DevDataLogMapper;

	@Override
	public DevDataLog selectByChildclassId(String childclassid) {
		// TODO Auto-generated method stub
		return DevDataLogMapper.selectByChildclassId(childclassid);
	}

	@Override
	public DevDataLog selectDataByDevserialAndDid(String devserial, String did) {
		return DevDataLogMapper.selectDataByDevserialAndDid(devserial,did);
	}

	@Override
	public int insert(DevDataLog record) {
		// TODO Auto-generated method stub
		return DevDataLogMapper.insert(record);
	}
	
}