package com.weeg.service;

import com.weeg.bean.DevSecretKey;

public interface DevSecretKeyService {

    DevSecretKey selectkeyvalue(String keyname, String devserial, String imei);


    DevSecretKey selectkeyname(String devserial, String imei);

    //did 2009 如果没有新版本密钥 执行插入新版本密钥命令
    int insertnewsecret(DevSecretKey devSecretKey);

    //更新新密钥为不启用   did 2009 写命令执行
    int updatenewusekeynameclose(String imei, String devserial, String keyname, String keyvalue, String keylength);
    //更新初始密钥为启用   did 2009 写命令执行
    int updateoldusekeynameopen(String imei, String devserial);


    //查询初始版本密钥参数
    DevSecretKey selectoldkeyvalue(String devserial, String imei);


    //更新初始密钥为不启用   did 3001上报执行
    int updateoldusekeynameclose(String imei, String devserial, String keyname);
    //更新新版本密钥为启用   did 3001上报执行
    int updatenewusekeynameopen(String imei, String devserial, String keyname);

    DevSecretKey selectkeyvalueandname(String imei);

    //删除设备默认密钥
    int delSecretKey(String serial, String imei);

    //2009错误码返回00时调用，根据IMEI号,启用新版本密钥
    int enableNewSecretKey(String imei, String keyname);
    //删除旧版本密钥，(不包含启用的密钥和默认版本密钥)
    int delOtherSecretKey(String imei, String keyname);
    //停用非默认密钥
    int disableUndefaultSecretKey(String imei);
    //修改密钥失败，删除非默认的最新修改的版本
    int delNewSecretKey(String imei, String keyname);

    //根据上报上来的密钥版本，查询密钥信息
    DevSecretKey selectSecretKeyvalue(String keyname, String devserial, String imei);

}
