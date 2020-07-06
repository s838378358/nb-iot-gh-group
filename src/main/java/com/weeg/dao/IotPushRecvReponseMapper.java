package com.weeg.dao;

import com.weeg.bean.IotPushRecvReponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IotPushRecvReponseMapper {
    
    List<IotPushRecvReponse> selectBySerialandTime(@Param(value = "devserial") String devserial, @Param(value = "startTime") String startTime, @Param(value = "endTime") String endTime);

    int insert(IotPushRecvReponse recode);

    //根据classid更新did
    int updateDidByClassid(@Param(value = "did") String did, @Param(value = "classid") String classid);

    //根据classID查询DID  selectDID
    IotPushRecvReponse selectDID(@Param(value = "classid") String classid);

    //根据设备号、DID ， 查询 最新的设备注册请求 的 classID
    IotPushRecvReponse selectClassid(@Param(value = "devserial") String devserial, @Param(value = "did") String did);

    int updateConfirmtimeAndConfirmvlaue(@Param(value = "confirmtime") Date confirmtime, @Param(value = "confirmvalue") String confirmvalue, @Param(value = "classid") String classid, @Param(value = "iotreponsemsg") String iotreponsemsg);

    IotPushRecvReponse selectReponse(@Param(value = "classid") String classid);
}