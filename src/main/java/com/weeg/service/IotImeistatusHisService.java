package com.weeg.service;

import com.weeg.bean.IotImeiStatusHis;

public interface IotImeistatusHisService {
	int insert(IotImeiStatusHis record);

    int insertSelective(IotImeiStatusHis record);
}