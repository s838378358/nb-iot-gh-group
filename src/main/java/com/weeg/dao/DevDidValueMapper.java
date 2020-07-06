package com.weeg.dao;

import com.weeg.bean.DevDidValue;
import org.springframework.stereotype.Repository;

@Repository
public interface DevDidValueMapper {
    int insert(DevDidValue record);

    int insertSelective(DevDidValue record);
}