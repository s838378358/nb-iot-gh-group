package com.weeg.dao;

import com.weeg.bean.DevDidValueHis;
import org.springframework.stereotype.Repository;

@Repository
public interface DevDidValueHisMapper {
    int insert(DevDidValueHis record);

    int insertSelective(DevDidValueHis record);
}