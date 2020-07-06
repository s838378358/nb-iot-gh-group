package com.weeg.service;

import com.weeg.bean.DevDidValue;

public interface DevDidValueService {
    int insert(DevDidValue record);

    int insertSelective(DevDidValue record);
}