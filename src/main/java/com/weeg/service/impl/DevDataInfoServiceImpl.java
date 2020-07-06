package com.weeg.service.impl;

import com.weeg.bean.DevDataInfo;
import com.weeg.dao.DevDataInfoMapper;
import com.weeg.service.DevDataInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevDataInfoServiceImpl implements DevDataInfoService {

	@Autowired
    private DevDataInfoMapper DevDataInfoMapper;
	
	@Override
	public DevDataInfo selectActualData(String did, String devserial) {
		// TODO Auto-generated method stub
		return DevDataInfoMapper.selectActualData(did, devserial);
				
	}

	@Override
	public int insert(DevDataInfo record) {
		// TODO Auto-generated method stub
		return DevDataInfoMapper.insert(record);
	}
}