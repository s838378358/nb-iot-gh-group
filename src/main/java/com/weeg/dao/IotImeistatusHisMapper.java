package com.weeg.dao;

import com.weeg.bean.IotImeiStatusHis;
import org.springframework.stereotype.Repository;

@Repository
public interface IotImeistatusHisMapper {
    int insert(IotImeiStatusHis record);

    int insertSelective(IotImeiStatusHis record);
}