package com.weeg.service.impl;

import com.weeg.bean.IotPushRecvReponse;
import com.weeg.dao.IotPushRecvReponseMapper;
import com.weeg.service.IotPushRecvReponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class IotPushRecvReponseServiceImpl implements IotPushRecvReponseService {

	@Autowired
    private IotPushRecvReponseMapper iotPushRecvReponseMapper;

	@Override
	public List<IotPushRecvReponse> selectBySerialandTime(String devserial,
                                                          String startTime, String endTime) {
		// TODO Auto-generated method stub
		return iotPushRecvReponseMapper.selectBySerialandTime(devserial, startTime, endTime);
	}

	@Override
	public int insert(IotPushRecvReponse recode) {
		// TODO Auto-generated method stub
		return iotPushRecvReponseMapper.insert(recode);
	}

	@Override
	public int updateDidByClassid(String did, String classid) {
		// TODO Auto-generated method stub
		return iotPushRecvReponseMapper.updateDidByClassid(did, classid);
	}
	
	@Override
	public IotPushRecvReponse selectDID(String classid) {
		// TODO Auto-generated method stub
		return iotPushRecvReponseMapper.selectDID(classid);
	}
	
	@Override
	public IotPushRecvReponse selectClassid(String devserial, String did) {
		// TODO Auto-generated method stub
		return iotPushRecvReponseMapper.selectClassid(devserial,did);
	}
	
	@Override
	public int updateConfirmtimeAndConfirmvlaue(Date confirmtime,String confirmvalue,String classid,String iotreponsemsg) {
		// TODO Auto-generated method stub
		return iotPushRecvReponseMapper.updateConfirmtimeAndConfirmvlaue(confirmtime, confirmvalue, classid, iotreponsemsg);
	}

	@Override
	public IotPushRecvReponse selectReponse(String classid) {
		// TODO Auto-generated method stub
		return iotPushRecvReponseMapper.selectReponse(classid);
	}

}