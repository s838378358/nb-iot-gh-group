package com.weeg.service.impl;

import com.weeg.bean.DevSecretKey;
import com.weeg.dao.DevSecretKeyMapper;
import com.weeg.service.DevSecretKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevSecretKeyServiceImpl implements DevSecretKeyService {

    @Autowired
    DevSecretKeyMapper devSecretKeyMapper;

    @Override
    public DevSecretKey selectkeyvalue(String keyname, String devserial, String imei) {
        // TODO Auto-generated method stub
        return devSecretKeyMapper.selectkeyvalue(keyname,devserial,imei);
    }

    @Override
    public DevSecretKey selectkeyname(String devserial, String imei) {
        // TODO Auto-generated method stub
        return devSecretKeyMapper.selectkeyname(devserial,imei);
    }

    @Override
    public int insertnewsecret(DevSecretKey devSecretKey) {
        // TODO Auto-generated method stub
        return devSecretKeyMapper.insertnewsecret(devSecretKey);
    }

    @Override
    public int updatenewusekeynameclose(String imei, String devserial, String keyname, String keyvalue, String keylength) {
        // TODO Auto-generated method stub
        return devSecretKeyMapper.updatenewusekeynameclose(imei,devserial,keyname,keyvalue,keylength);
    }


    @Override
    public int updateoldusekeynameopen(String imei, String devserial) {
        // TODO Auto-generated method stub
        return devSecretKeyMapper.updateoldusekeynameopen(imei,devserial);
    }

    @Override
    public DevSecretKey selectoldkeyvalue(String devserial, String imei) {
        // TODO Auto-generated method stub
        return devSecretKeyMapper.selectoldkeyvalue(devserial,imei);
    }

    @Override
    public DevSecretKey selectkeyvalueandname(String imei) {
        // TODO Auto-generated method stub
        return devSecretKeyMapper.selectkeyvalueandname(imei);
    }

    @Override
    public int delSecretKey(String serial,String imei) {
        return devSecretKeyMapper.delSecretKey(serial,imei);
    }

    @Override
    public int enableNewSecretKey(String imei, String keyname) {
        return devSecretKeyMapper.enableNewSecretKey(imei,keyname);
    }

    @Override
    public int delOtherSecretKey(String imei, String keyname) {
        return devSecretKeyMapper.delOtherSecretKey(imei,keyname);
    }

    @Override
    public int disableUndefaultSecretKey(String imei) {
        return devSecretKeyMapper.disableUndefaultSecretKey(imei);
    }

    @Override
    public int delNewSecretKey(String imei, String keyname) {
        return devSecretKeyMapper.delNewSecretKey(imei,keyname);
    }

    @Override
    public DevSecretKey selectSecretKeyvalue(String keyname, String devserial, String imei) {
        return devSecretKeyMapper.selectSecretKeyvalue(keyname,devserial,imei);
    }


    @Override
    public int updateoldusekeynameclose(String imei, String devserial,String keyname) {
        // TODO Auto-generated method stub
        return devSecretKeyMapper.updateoldusekeynameclose(imei,devserial,keyname);
    }

    @Override
    public int updatenewusekeynameopen(String imei, String devserial, String keyname) {
        // TODO Auto-generated method stub
        return devSecretKeyMapper.updatenewusekeynameopen(imei,devserial,keyname);
    }




}
