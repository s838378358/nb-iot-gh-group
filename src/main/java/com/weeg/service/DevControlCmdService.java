package com.weeg.service;

import com.weeg.bean.DevControlCmd;

import java.util.List;

public interface DevControlCmdService {
    int insert(DevControlCmd record);
    
    List<DevControlCmd> selectBySerialandTime(String devserial, String startTime, String endTime);

    int updatecmdNo(int cmdNo, String devserial, String ctrlvalue, String random);

    List<DevControlCmd> selectBySerialandcmdFlag(String devserial, String cmdFlag);

}