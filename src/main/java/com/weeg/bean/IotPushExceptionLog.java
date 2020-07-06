package com.weeg.bean;

import java.util.Date;

public class IotPushExceptionLog {
    private String pushsource;

    private String classid;

    private Date pushtime;

    public String getPushsource() {
        return pushsource;
    }

    public void setPushsource(String pushsource) {
        this.pushsource = pushsource == null ? null : pushsource.trim();
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid == null ? null : classid.trim();
    }

    public Date getPushtime() {
        return pushtime;
    }

    public void setPushtime(Date pushtime) {
        this.pushtime = pushtime;
    }
}