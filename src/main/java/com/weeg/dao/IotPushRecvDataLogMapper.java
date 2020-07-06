package com.weeg.dao;

import com.weeg.bean.IotPushRecvDataLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IotPushRecvDataLogMapper {
    int insert(IotPushRecvDataLog record);

    IotPushRecvDataLog selectByClassId(@Param(value = "classid") String classid);
}