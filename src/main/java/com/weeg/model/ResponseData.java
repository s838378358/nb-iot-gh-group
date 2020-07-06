package com.weeg.model;

public class ResponseData<T> {

    private boolean result;

    private String errorCode1;

    private String errorCode2;

    private String message;

    private String token;

    private T data;

    private long total;


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorCode1() {
        return errorCode1;
    }

    public void setErrorCode1(String errorCode1) {
        this.errorCode1 = errorCode1;
    }

    public String getErrorCode2() {
        return errorCode2;
    }

    public void setErrorCode2(String errorCode2) {
        this.errorCode2 = errorCode2;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "result=" + result +
                ", errorCode1='" + errorCode1 + '\'' +
                ", errorCode2='" + errorCode2 + '\'' +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", data=" + data +
                ", total=" + total +
                '}';
    }
}
