package com.weeg.bean;

public class IotPushExceptionLogWithBLOBs extends IotPushExceptionLog {
    private String pushinfo;

    private String exceptinfo;

    public String getPushinfo() {
        return pushinfo;
    }

    public void setPushinfo(String pushinfo) {
        this.pushinfo = pushinfo == null ? null : pushinfo.trim();
    }

    public String getExceptinfo() {
        return exceptinfo;
    }

    public void setExceptinfo(String exceptinfo) {
        this.exceptinfo = exceptinfo == null ? null : exceptinfo.trim();
    }
}