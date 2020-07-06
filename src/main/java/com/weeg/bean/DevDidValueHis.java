package com.weeg.bean;

import java.util.Date;

public class DevDidValueHis {
    private String classid;

    private String childclassid;

    private String devserial;

    private String did;

    private Date updatetime;

    private String upvalue;

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid == null ? null : classid.trim();
    }

    public String getChildclassid() {
        return childclassid;
    }

    public void setChildclassid(String childclassid) {
        this.childclassid = childclassid == null ? null : childclassid.trim();
    }

    public String getDevserial() {
        return devserial;
    }

    public void setDevserial(String devserial) {
        this.devserial = devserial == null ? null : devserial.trim();
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did == null ? null : did.trim();
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getUpvalue() {
        return upvalue;
    }

    public void setUpvalue(String upvalue) {
        this.upvalue = upvalue == null ? null : upvalue.trim();
    }
}