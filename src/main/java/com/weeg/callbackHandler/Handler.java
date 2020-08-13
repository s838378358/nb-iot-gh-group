package com.weeg.callbackHandler;

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

}
