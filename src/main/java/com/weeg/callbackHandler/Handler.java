package com.weeg.callbackHandler;

import com.weeg.service.DevDataLogService;
import com.weeg.service.DevRegInfoService;
import com.weeg.service.DevSecretKeyService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by SJ on 2020/8/10
 * 模板方法设计模式
 */
public abstract class Handler implements InitializingBean {


    public JSONObject upload3001Handler(String[] binaryData, byte[] b, JSONObject object){
        throw new UnsupportedOperationException();
    }


    public JSONObject upload3003Handler(String[] binaryData,byte[] b,JSONObject object,String keyvalue){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0001Handler(String mid,JSONObject object,String hexstr,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0002Handler(String mid,JSONObject object,String hexstr,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0003Handler(byte[] b,String mid,JSONObject object,String hexstr,String[] binaryData,String keyvalue){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0004Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0005Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0006Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload000bHandler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload000eHandler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0010Handler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0011Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0012Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0013Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0014Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0015Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0016Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0017Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload1000Handler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload1001Handler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload1004Handler(JSONObject object,String mid,byte[] b,String[] binaryData,String hexstr,String keyvalue){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2001Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2002Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2003Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2004Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2005Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2006Handler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2007Handler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2009Handler(
            JSONObject object, String mid, String[] binaryData, String imeikey,DevRegInfoService devRegInfoService,
            DevDataLogService devDataLogService, DevSecretKeyService devSecretKeyService){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload200aHandler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload200eHandler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload200fHandler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2010Handler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2011Handler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2012Handler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload2020Handler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0007Handler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0008Handler(JSONObject object,String mid,String[] binaryData,String hexstr,String content){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload0009Handler(JSONObject object,String mid,String[] binaryData,String hexstr,String content){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload000aHandler(JSONObject object,String mid,String[] binaryData,String hexstr){
        throw new UnsupportedOperationException();
    }

    public JSONObject upload000cHandler(JSONObject object,String mid,String[] binaryData){
        throw new UnsupportedOperationException();
    }







}
