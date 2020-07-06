package com.weeg.dao;

import com.weeg.bean.DevSecretKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DevSecretKeyMapper {

    DevSecretKey selectkeyvalue(@Param(value = "keyname") String keyname, @Param(value = "devserial") String devserial, @Param(value = "imei") String imei);

    DevSecretKey selectkeyvalueandname(@Param(value = "imei") String imei);


    DevSecretKey selectkeyname(@Param(value = "devserial") String devserial, @Param(value = "imei") String imei);

    //did 2009 如果没有新版本密钥 执行插入新版本密钥命令
    int insertnewsecret(DevSecretKey devSecretKey);

    DevSecretKey selectoldkeyvalue(@Param(value = "devserial") String devserial, @Param(value = "imei") String imei);


    //更新新密钥为不启用   did 2009 写命令执行
    int updatenewusekeynameclose(@Param(value = "imei") String imei, @Param(value = "devserial") String devserial, @Param(value = "keyname") String keyname, @Param(value = "keyvalue") String keyvalue, @Param(value = "keylength") String keylength);
    //更新初始密钥为启用   did 2009 写命令执行
    int updateoldusekeynameopen(@Param(value = "imei") String imei, @Param(value = "devserial") String devserial);




    //更新初始密钥为不启用   did 3001上报执行
    int updateoldusekeynameclose(@Param(value = "imei") String imei, @Param(value = "devserial") String devserial, @Param(value = "keyname") String keyname);
    //更新新版本密钥为启用   did 3001上报执行
    int updatenewusekeynameopen(@Param(value = "imei") String imei, @Param(value = "devserial") String devserial, @Param(value = "keyname") String keyname);


    //删除设备默认密钥 注销时调用
    int delSecretKey(@Param(value = "serial") String serial, @Param(value = "imei") String imei);

    //2009错误码返回00时调用，根据IMEI号,启用新版本密钥
    int enableNewSecretKey(@Param(value = "imei") String imei, @Param(value = "keyname") String keyname);
    //删除老版本密钥，(不包含启用密钥和默认版本密钥)
    int delOtherSecretKey(@Param(value = "imei") String imei, @Param(value = "keyname") String keyname);
    //停用非默认密钥
    int disableUndefaultSecretKey(@Param(value = "imei") String imei);
    //修改密钥失败，删除非默认的最新修改的版本
    int delNewSecretKey(@Param(value = "imei") String imei, @Param(value = "keyname") String keyname);

    //根据上报上来的密钥版本，查询密钥信息
    DevSecretKey selectSecretKeyvalue(@Param(value = "keyname") String keyname, @Param(value = "devserial") String devserial, @Param(value = "imei") String imei);

}
