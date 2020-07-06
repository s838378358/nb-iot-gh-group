package com.weeg.dao;

import com.weeg.bean.IotPushExceptionLogWithBLOBs;
import org.springframework.stereotype.Repository;

@Repository
public interface IotPushExceptionLogMapper {
    int insert(IotPushExceptionLogWithBLOBs record);

    int insertSelective(IotPushExceptionLogWithBLOBs record);
}