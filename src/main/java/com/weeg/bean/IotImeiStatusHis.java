package com.weeg.bean;

import java.util.Date;

public class IotImeiStatusHis {
    private String imei;

    private String devserial;

    private String status;

    private String iotserial;

    private String at;

    private String login_type;

    private String type;

    private String msg_signature;

    public String getLogin_type() {
		return login_type;
	}

	public void setLogin_type(String login_type) {
		this.login_type = login_type == null ? null : login_type.trim();
	}

	public String getMsg_signature() {
		return msg_signature;
	}

	public void setMsg_signature(String msg_signature) {
		this.msg_signature = msg_signature == null ? null : msg_signature.trim();
	}

	private String nonce;

    private Date statustime;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    public String getDevserial() {
        return devserial;
    }

    public void setDevserial(String devserial) {
        this.devserial = devserial == null ? null : devserial.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getIotserial() {
        return iotserial;
    }

    public void setIotserial(String iotserial) {
        this.iotserial = iotserial == null ? null : iotserial.trim();
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at == null ? null : at.trim();
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }


    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce == null ? null : nonce.trim();
    }

    public Date getStatustime() {
        return statustime;
    }

    public void setStatustime(Date statustime) {
        this.statustime = statustime;
    }
}