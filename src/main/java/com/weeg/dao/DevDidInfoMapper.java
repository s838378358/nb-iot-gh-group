package com.weeg.dao;

import com.weeg.bean.DevDidInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface DevDidInfoMapper {
    int deleteByPrimaryKey(String did);

    int insert(DevDidInfo record);

    int insertSelective(DevDidInfo record);

    DevDidInfo selectByPrimaryKey(String did);

    int updateByPrimaryKeySelective(DevDidInfo record);

    int updateByPrimaryKey(DevDidInfo record);
}