package com.weeg.service.impl;

import com.weeg.bean.IotImeiStatus;
import com.weeg.dao.IotImeiStatusMapper;
import com.weeg.service.IotImeiStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IotImeiStatusServiceImpl implements IotImeiStatusService {

	@Autowired
    private IotImeiStatusMapper iotImeiStatusMapper;
	
	@Override
	public IotImeiStatus selectBySerial(String serial) {
		// TODO Auto-generated method stub
		return iotImeiStatusMapper.selectBySerial(serial);
	}
	
	@Override
	public int insert(IotImeiStatus record) {
		// TODO Auto-generated method stub
		return iotImeiStatusMapper.insert(record);
	}
	
	@Override
	public int updateStatus(String status,String ime) {
		// TODO Auto-generated method stub
		return iotImeiStatusMapper.updateStatus(status,ime);
	}

	@Override
	public IotImeiStatus selectByimei(String imei) {
		// TODO Auto-generated method stub
		return iotImeiStatusMapper.selectByimei(imei);
	}

	@Override
	public int deleteStatusByImei(String imei) {
		return iotImeiStatusMapper.deleteStatusByImei(imei);
	}
}