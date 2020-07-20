package com.weeg.model;

import lombok.Data;

@Data
public class ResponseData {

    private boolean result;
    private String errorCode1;
    private String errorCode2;
    private String message;
    private String token;
    private String data;
    private long total;


    public ResponseData() {
        super();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "result=" + result +
                ", errorCode1='" + errorCode1 + '\'' +
                ", errorCode2='" + errorCode2 + '\'' +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", data='" + data + '\'' +
                ", total=" + total +
                '}';
    }
}
