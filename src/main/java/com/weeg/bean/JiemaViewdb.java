package com.weeg.bean;

import java.util.Date;

public class JiemaViewdb {
    private String devserial;

    private String iotserial;

    private String did;

    private Date uploadtime;

    private String uploadvalue;

    private String data;

    private Date updatetime;

    private String childclassid;

    private String confirmvalue;

    private String confirmcode;

    private String confirmmsg;

    private Date iotreponsetime;
    private Date confirmtime;
    private String iotreponsetime1;
    private String confirmtime1;

    private String iotreponsecode;

    private String iotreponsemsg;
    private String classid;
    

    public String getDevserial() {
        return devserial;
    }

    public void setDevserial(String devserial) {
        this.devserial = devserial == null ? null : devserial.trim();
    }

    public String getIotserial() {
        return iotserial;
    }

    public void setIotserial(String iotserial) {
        this.iotserial = iotserial == null ? null : iotserial.trim();
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did == null ? null : did.trim();
    }

    public Date getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(Date uploadtime) {
        this.uploadtime = uploadtime;
    }

    public String getUploadvalue() {
        return uploadvalue;
    }

    public void setUploadvalue(String uploadvalue) {
        this.uploadvalue = uploadvalue == null ? null : uploadvalue.trim();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data == null ? null : data.trim();
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getChildclassid() {
        return childclassid;
    }

    public void setChildclassid(String childclassid) {
        this.childclassid = childclassid == null ? null : childclassid.trim();
    }

    public String getConfirmvalue() {
        return confirmvalue;
    }

    public void setConfirmvalue(String confirmvalue) {
        this.confirmvalue = confirmvalue == null ? null : confirmvalue.trim();
    }

    public String getConfirmcode() {
        return confirmcode;
    }

    public void setConfirmcode(String confirmcode) {
        this.confirmcode = confirmcode == null ? null : confirmcode.trim();
    }

    public String getConfirmmsg() {
        return confirmmsg;
    }

    public void setConfirmmsg(String confirmmsg) {
        this.confirmmsg = confirmmsg == null ? null : confirmmsg.trim();
    }

    public Date getIotreponsetime() {
        return iotreponsetime;
    }

    public void setIotreponsetime(Date iotreponsetime) {
        this.iotreponsetime = iotreponsetime;
    }

    public String getIotreponsecode() {
        return iotreponsecode;
    }

    public void setIotreponsecode(String iotreponsecode) {
        this.iotreponsecode = iotreponsecode == null ? null : iotreponsecode.trim();
    }

    public String getIotreponsemsg() {
        return iotreponsemsg;
    }

    public void setIotreponsemsg(String iotreponsemsg) {
        this.iotreponsemsg = iotreponsemsg == null ? null : iotreponsemsg.trim();
    }

	public Date getConfirmtime() {
		return confirmtime;
	}

	public void setConfirmtime(Date confirmtime) {
		this.confirmtime = confirmtime;
	}

	public String getIotreponsetime1() {
		return iotreponsetime1;
	}

	public void setIotreponsetime1(String iotreponsetime1) {
		this.iotreponsetime1 = iotreponsetime1;
	}

	public String getConfirmtime1() {
		return confirmtime1;
	}

	public void setConfirmtime1(String confirmtime1) {
		this.confirmtime1 = confirmtime1;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}
}