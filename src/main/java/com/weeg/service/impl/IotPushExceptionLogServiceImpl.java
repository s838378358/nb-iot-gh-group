package com.weeg.service.impl;

import com.weeg.bean.IotPushExceptionLogWithBLOBs;
import com.weeg.dao.IotPushExceptionLogMapper;
import com.weeg.service.IotPushExceptionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @ClassName: IotPushExceptionLogServiceImpl
* @Description: TODO(这里用一句话描述这个类的作用)
* @author yuyan
* @date 2019年11月15日
*/
@Service
public class IotPushExceptionLogServiceImpl implements IotPushExceptionLogService {
	@Autowired
    private IotPushExceptionLogMapper iotPushExceptionLogMapper;
	@Override
	public int insert(IotPushExceptionLogWithBLOBs record) {
		// TODO Auto-generated method stub
		return iotPushExceptionLogMapper.insert(record);
	}

	@Override
	public int insertSelective(IotPushExceptionLogWithBLOBs record) {
		// TODO Auto-generated method stub
		return 0;
	}

}
