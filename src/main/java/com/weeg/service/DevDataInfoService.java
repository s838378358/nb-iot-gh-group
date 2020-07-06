package com.weeg.service;

import com.weeg.bean.DevDataInfo;

public interface DevDataInfoService {
	// 添加记录
	int insert(DevDataInfo record);

	DevDataInfo selectActualData(String did, String devserial);
}