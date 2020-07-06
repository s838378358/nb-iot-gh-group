package com.weeg.dao;

import com.weeg.bean.DevDataInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DevDataInfoMapper {
	//添加记录
	int insert(DevDataInfo record);
	
	DevDataInfo selectActualData(@Param(value = "did") String did, @Param(value = "devserial") String devserial);
}