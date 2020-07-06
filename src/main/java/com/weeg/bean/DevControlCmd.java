package com.weeg.bean;

import java.util.Date;

public class DevControlCmd {
    private String classid;

    private String devserial;

    private String iotserial;

    private String devtype;

    private String opttype;

    private String did;

    private String ctrlvalue;

    private String ctrltype;

    private Date ctrltime;
    
    private String ctrltime1;
    
    private String cmdFlag;
    
    private int cmdNo;

    private String cmdCache;

    private String cmdType;

    private String cmdData;

    private String reRead;

    public int getCmdNo() {
        return cmdNo;
    }

    public void setCmdNo(int cmdNo) {
        this.cmdNo = cmdNo;
    }

    public String getCmdCache() {
        return cmdCache;
    }

    public void setCmdCache(String cmdCache) {
        this.cmdCache = cmdCache == null ? null : cmdCache.trim();
    }

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType == null ? null : cmdType.trim();
    }

    public String getCmdData() {
        return cmdData;
    }

    public void setCmdData(String cmdData) {
        this.cmdData = cmdData == null ? null : cmdData.trim();
    }

    public String getReRead() {
        return reRead;
    }

    public void setReRead(String reRead) {
        this.reRead = reRead == null ? null : reRead.trim();
    }

    public String getCmdFlag() {
		return cmdFlag;
	}

	public void setCmdFlag(String cmdFlag) {
		this.cmdFlag = cmdFlag;
	}



	public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid == null ? null : classid.trim();
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

    public String getOpttype() {
        return opttype;
    }

    public void setOpttype(String opttype) {
        this.opttype = opttype == null ? null : opttype.trim();
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did == null ? null : did.trim();
    }

    public String getCtrlvalue() {
        return ctrlvalue;
    }

    public void setCtrlvalue(String ctrlvalue) {
        this.ctrlvalue = ctrlvalue == null ? null : ctrlvalue.trim();
    }

    public String getCtrltype() {
        return ctrltype;
    }

    public void setCtrltype(String ctrltype) {
        this.ctrltype = ctrltype == null ? null : ctrltype.trim();
    }

    public Date getCtrltime() {
        return ctrltime;
    }

    public void setCtrltime(Date ctrltime) {
        this.ctrltime = ctrltime;
    }

	/**
	 * @return the ctrltime1
	 */
	public String getCtrltime1() {
		return ctrltime1;
	}

	/**
	 * @param ctrltime1 the ctrltime1 to set
	 */
	public void setCtrltime1(String ctrltime1) {
		this.ctrltime1 = ctrltime1;
	}
}