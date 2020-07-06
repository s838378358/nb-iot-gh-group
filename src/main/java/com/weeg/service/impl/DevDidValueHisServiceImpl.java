package com.weeg.service.impl;

import com.weeg.bean.DevDidValueHis;
import com.weeg.dao.DevDidValueHisMapper;
import com.weeg.service.DevDidValueHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @ClassName: DevDidValueHisServiceImpl
* @Description: TODO(这里用一句话描述这个类的作用)
* @author yuyan
* @date 2019年11月15日
*/
@Service
public class DevDidValueHisServiceImpl implements DevDidValueHisService {
	@Autowired
    private DevDidValueHisMapper devDidValueHisMapper;
	
	@Override
	public int insert(DevDidValueHis record) {
		// TODO Auto-generated method stub
		return devDidValueHisMapper.insert(record);
	}

	@Override
	public int insertSelective(DevDidValueHis record) {
		// TODO Auto-generated method stub
		return devDidValueHisMapper.insert(record);
	}

}
