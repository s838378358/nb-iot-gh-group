package com.weeg.dao;


import com.weeg.bean.DevControlCmd;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevControlCmdMapper {
    int insert(DevControlCmd record);

    List<DevControlCmd> selectBySerialandTime(@Param(value = "devserial") String devserial, @Param(value = "startTime") String startTime, @Param(value = "endTime") String endTime);

    int updatecmdNo(@Param(value = "cmdNo") int cmdNo, @Param(value = "devserial") String devserial, @Param(value = "ctrlvalue") String ctrlvalue, @Param(value = "random") String random);

    List<DevControlCmd> selectBySerialandcmdFlag(@Param(value = "devserial") String devserial, @Param(value = "cmdFlag") String cmdFlag);
}