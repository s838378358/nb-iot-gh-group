package com.weeg.dao;

import com.weeg.bean.DevDataLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DevDataLogMapper {
	int insert(DevDataLog record);
    
    DevDataLog selectByChildclassId(@Param(value = "childclassid") String childclassid);

    ////根据设备编号 devserial 查询出 最新的3001 上报信息
    DevDataLog selectDataByDevserialAndDid(@Param(value = "devserial") String devserial, @Param(value = "did") String did);

}