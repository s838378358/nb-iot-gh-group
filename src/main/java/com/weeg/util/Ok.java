package com.weeg.util;

import com.weeg.configurer.ErrorEnmus;
import lombok.Data;

/**
 * @Author : Dy
 * @Date : 2020/4/22 11:25
 **/
@Data
public class Ok {


    /** true false */
    private Boolean result;
    /** 000 某个系统错误 */
    private String errorCode1;
    /** 0000具体错误 */
    private String errorCode2;
    /** 成功/错误原因 */
    private String message;
    /** 返回数据 */
    private Object data;

    Ok() {
    }

    public Ok(String errorCode1) {
        this.result = true;
        this.errorCode1 = errorCode1;
        this.errorCode2 = "00000";
        this.message = "成功";
        this.data = "";
    }

    public Ok(String errorCode1,Object obj) {
        this.result = false;
        this.errorCode1 = errorCode1;
        this.errorCode2 = "00000";
        this.message = "成功";
        this.data = obj;
    }

    /** all */
    public Ok(Boolean result, String errorCode1, String errorCode2, String message, Object data) {
        this.result = result;
        this.errorCode1 = errorCode1;
        this.errorCode2 = errorCode2;
        this.message = message;
        this.data = data;
    }

    public Ok(String errorCode1, ErrorEnmus errorEnmu) {
        this.result = false;
        this.errorCode1 = errorCode1;
        this.errorCode2 = errorEnmu.getCode();
        this.message = errorEnmu.getMessage();
        this.data = "";
    }

    public Ok(String errorCode1, ErrorEnmus errorEnmu,Object obj) {
        this.result = false;
        this.errorCode1 = errorCode1;
        this.errorCode2 = errorEnmu.getCode();
        this.message = errorEnmu.getMessage();
        this.data = obj;
    }

    /** fail1 */
    public Ok(String errorCode1, String errorCode2, String message) {
        this.result = false;
        this.errorCode1 = errorCode1;
        this.errorCode2 = errorCode2;
        this.message = message;
        this.data = "";
    }
    /** fail2 */
    public Ok(String errorCode1, String errorCode2, String message, Object data) {
        this.result = false;
        this.errorCode1 = errorCode1;
        this.errorCode2 = errorCode2;
        this.message = message;
        this.data = data;
    }
}
