package com.weeg.bean;

import java.util.Date;

public class IotPushRecvReponse {
    private String classid;

    private String childclassid;

    private String devserial;

    private String iotserial;

    private String devtype;

    private String did;

    private Date uploadtime;
    private String uploadtime1;

    private String uploadvalue;

    private String uploadvalueI;

    private String uploadmsg;

    private Date confirmtime;

    private String confirmvalue;

    private String confirmcode;

    private String confirmmsg;

    private Date iotreponsetime;

    private String iotreponsecode;

    private String iotreponsemsg;

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

    public String getIotserial() {
        return iotserial;
    }

    public void setIotserial(String iotserial) {
        this.iotserial = iotserial == null ? null : iotserial.trim();
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype == null ? null : devtype.trim();
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

    public String getUploadvalueI() {
        return uploadvalueI;
    }

    public void setUploadvalueI(String uploadvalueI) {
        this.uploadvalueI = uploadvalueI == null ? null : uploadvalueI.trim();
    }

    public String getUploadmsg() {
        return uploadmsg;
    }

    public void setUploadmsg(String uploadmsg) {
        this.uploadmsg = uploadmsg == null ? null : uploadmsg.trim();
    }

    public Date getConfirmtime() {
        return confirmtime;
    }

    public void setConfirmtime(Date confirmtime) {
        this.confirmtime = confirmtime;
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

	/**
	 * @return the uploadtime1
	 */
	public String getUploadtime1() {
		return uploadtime1;
	}

	/**
	 * @param uploadtime1 the uploadtime1 to set
	 */
	public void setUploadtime1(String uploadtime1) {
		this.uploadtime1 = uploadtime1;
	}
}