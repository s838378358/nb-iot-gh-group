package com.weeg.controller;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.weeg.configurer.ErrorEnmus;
import com.weeg.util.Ok;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : Dy
 * @Date : 2020/4/22 11:24
 **/
@Configuration
public class CoreController {

    @Value("${spring.id:0000}")
    private String projectId;

    protected Object ok() {
        return JSON.toJSON(new Ok(StrUtil.padPre(projectId,4,"0")));
    }

    protected Object ok(Object obj) {
        return JSON.toJSON(new Ok(StrUtil.padPre(projectId,4,"0"),obj));
    }

    protected Object ok(Boolean result, String errorCode2, String message, Object data) {
        return JSON.toJSON(new Ok(result, StrUtil.padPre(projectId,4,"0"), errorCode2, message, data));
    }

    protected Object fail(ErrorEnmus errorEnmu) {
        return JSON.toJSON(new Ok(StrUtil.padPre(projectId,4,"0"), errorEnmu));
    }

    protected Object fail(ErrorEnmus errorEnmu,Object obj) {
        return JSON.toJSON(new Ok(StrUtil.padPre(projectId,4,"0"), errorEnmu,obj));
    }

    protected Object fail(String errorCode2, String message) {
        return JSON.toJSON(new Ok(StrUtil.padPre(projectId,4,"0"), errorCode2, message));
    }

    protected Object fail(String errorCode2, String message, Object data) {
        return JSON.toJSON(new Ok(StrUtil.padPre(projectId,4,"0"), errorCode2, message, data));
    }

    protected Object fail(Boolean result,String errorCode1, String errorCode2, String message, Object data) {
        return JSON.toJSON(new Ok(result, StrUtil.padPre(errorCode1,4,"0"), errorCode2, message, data));
    }







}
