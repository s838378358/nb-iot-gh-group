package com.weeg.service.impl;

import com.weeg.bean.DevDidValue;
import com.weeg.dao.DevDidValueMapper;
import com.weeg.service.DevDidValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevDidValueServiceImpl implements DevDidValueService {

	@Autowired
    private DevDidValueMapper devDidValueMapper;
	
	@Override
	public int insert(DevDidValue record) {
		// TODO Auto-generated method stub
		return devDidValueMapper.insert(record);
	}

	@Override
	public int insertSelective(DevDidValue record) {
		// TODO Auto-generated method stub
		return devDidValueMapper.insertSelective(record);
	}
}