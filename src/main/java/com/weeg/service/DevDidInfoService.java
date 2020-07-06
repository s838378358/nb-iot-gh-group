package com.weeg.service;

import com.weeg.bean.DevDidInfo;

public interface DevDidInfoService {
    int deleteByPrimaryKey(String did);

    int insert(DevDidInfo record);

    int insertSelective(DevDidInfo record);

    DevDidInfo selectByPrimaryKey(String did);

    int updateByPrimaryKeySelective(DevDidInfo record);

    int updateByPrimaryKey(DevDidInfo record);
}