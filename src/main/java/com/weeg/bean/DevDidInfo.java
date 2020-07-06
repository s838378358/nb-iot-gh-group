package com.weeg.bean;

public class DevDidInfo {
    private String did;

    private String devtype;

    private String didname;

    private String didattrib;

    private String didlen;

    private String datainv;

    private String didcode;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did == null ? null : did.trim();
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype == null ? null : devtype.trim();
    }

    public String getDidname() {
        return didname;
    }

    public void setDidname(String didname) {
        this.didname = didname == null ? null : didname.trim();
    }

    public String getDidattrib() {
        return didattrib;
    }

    public void setDidattrib(String didattrib) {
        this.didattrib = didattrib == null ? null : didattrib.trim();
    }

    public String getDidlen() {
        return didlen;
    }

    public void setDidlen(String didlen) {
        this.didlen = didlen == null ? null : didlen.trim();
    }

    public String getDatainv() {
        return datainv;
    }

    public void setDatainv(String datainv) {
        this.datainv = datainv == null ? null : datainv.trim();
    }

    public String getDidcode() {
        return didcode;
    }

    public void setDidcode(String didcode) {
        this.didcode = didcode == null ? null : didcode.trim();
    }
}