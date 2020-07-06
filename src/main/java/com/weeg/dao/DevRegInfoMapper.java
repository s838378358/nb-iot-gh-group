package com.weeg.dao;

import com.weeg.bean.DevRegInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DevRegInfoMapper {
    int deleteByPrimaryKey(String imei);

    int insert(DevRegInfo record);

    //根据设备序列号，查询设备信息
    DevRegInfo selectByDevSerial(@Param(value = "devserial") String devserial);
    //根据imei号，查询设备信息
    DevRegInfo selectByImei(@Param(value = "imei") String imei);

    int updateByPrimaryKey(DevRegInfo record);

    //根据imei，更新平台分配的设备唯一编码
    int updateByImei(@Param(value = "iotserial") String iotserial, @Param(value = "imei") String imei);
}