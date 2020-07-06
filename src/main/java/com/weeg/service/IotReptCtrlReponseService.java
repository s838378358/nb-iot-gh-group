package com.weeg.service;

import com.weeg.bean.IotReptCtrlReponse;

import java.util.List;

public interface IotReptCtrlReponseService {
	int insert(IotReptCtrlReponse record);

	List<IotReptCtrlReponse> selectByClassId(String classid);
}