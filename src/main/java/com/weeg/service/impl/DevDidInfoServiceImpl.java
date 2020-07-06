package com.weeg.service.impl;

import com.weeg.bean.DevDidInfo;
import com.weeg.dao.DevDidInfoMapper;
import com.weeg.service.DevDidInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevDidInfoServiceImpl implements DevDidInfoService {

	@Autowired
    private DevDidInfoMapper devDidInfoMapper;
	
	@Override
	public int deleteByPrimaryKey(String did) {
		// TODO Auto-generated method stub
		return devDidInfoMapper.deleteByPrimaryKey(did);
	}

	@Override
	public int insert(DevDidInfo record) {
		// TODO Auto-generated method stub
		return devDidInfoMapper.insert(record);
	}

	@Override
	public int insertSelective(DevDidInfo record) {
		// TODO Auto-generated method stub
		return devDidInfoMapper.insertSelective(record);
	}

	@Override
	public DevDidInfo selectByPrimaryKey(String did) {
		// TODO Auto-generated method stub
		return devDidInfoMapper.selectByPrimaryKey(did);
	}

	@Override
	public int updateByPrimaryKeySelective(DevDidInfo record) {
		// TODO Auto-generated method stub
		return devDidInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKey(DevDidInfo record) {
		// TODO Auto-generated method stub
		return devDidInfoMapper.updateByPrimaryKeySelective(record);
	}
}