package com.weeg.service.impl;

import com.weeg.bean.DevRegInfo;
import com.weeg.dao.DevRegInfoMapper;
import com.weeg.service.DevRegInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevRegInfoServiceImpl implements DevRegInfoService {

	@Autowired
    private DevRegInfoMapper devRegInfoMapper;
	
	@Override
	public int deleteByPrimaryKey(String imei) {
		// TODO Auto-generated method stub
		return devRegInfoMapper.deleteByPrimaryKey(imei);
	}

	@Override
	public int insert(DevRegInfo record) {
		// TODO Auto-generated method stub
		return devRegInfoMapper.insert(record);
	}

	@Override
	public DevRegInfo selectByDevSerial(String devserial) {
		// TODO Auto-generated method stub
		return devRegInfoMapper.selectByDevSerial(devserial);
	}

	@Override
	public int updateByPrimaryKey(DevRegInfo record) {
		// TODO Auto-generated method stub
		return devRegInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByImei(String iotserial, String imei) {
		// TODO Auto-generated method stub
		return devRegInfoMapper.updateByImei(iotserial, imei);
	}

	@Override
	public DevRegInfo selectByImei(String imei) {
		// TODO Auto-generated method stub
		return devRegInfoMapper.selectByImei(imei);
	}
}