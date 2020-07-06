package com.weeg.service.impl;

import com.weeg.bean.IotImeiStatusHis;
import com.weeg.dao.IotImeistatusHisMapper;
import com.weeg.service.IotImeistatusHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IotImeistatusHisServiceImpl implements IotImeistatusHisService {

	@Autowired
    private IotImeistatusHisMapper iotImeistatusHisMapper;
	

	@Override
	public int insert(IotImeiStatusHis record) {
		// TODO Auto-generated method stub
		return iotImeistatusHisMapper.insert(record);
	}

	@Override
	public int insertSelective(IotImeiStatusHis record) {
		// TODO Auto-generated method stub
		return iotImeistatusHisMapper.insertSelective(record);
	}
}