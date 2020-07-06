package com.weeg.dao;

import com.weeg.bean.IotImeiStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IotImeiStatusMapper {
	int deleteByPrimaryKey(String imei);

    int insert(IotImeiStatus record);

	//根据设备序列号查询设备在线状态
    IotImeiStatus selectBySerial(@Param(value = "serial") String serial);

    int updateByPrimaryKey(IotImeiStatus record);

    IotImeiStatus selectByPrimaryKey(String imei);

    int updateStatus(@Param(value = "status") String status, @Param(value = "imei") String ime);

    IotImeiStatus selectByimei(@Param(value = "imei") String imei);

    int deleteStatusByImei(@Param(value = "imei") String imei);
}