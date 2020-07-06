package com.weeg.service;

import com.weeg.bean.IotPushRecvReponse;

import java.util.Date;
import java.util.List;

public interface IotPushRecvReponseService {
	List<IotPushRecvReponse> selectBySerialandTime(String devserial, String startTime, String endTime);
	int insert(IotPushRecvReponse recode);

	 //根据classid更新did
    int updateDidByClassid(String did, String classid);

    IotPushRecvReponse selectDID(String classid);

	IotPushRecvReponse selectClassid(String devserial, String did);

	//根据classID 更新两个回应参数
	int updateConfirmtimeAndConfirmvlaue(Date confirmtime, String confirmvalue, String classid, String iotreponsemsg);
	
	IotPushRecvReponse selectReponse(String classid);

}