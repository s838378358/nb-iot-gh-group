package com.weeg.configurer;

/**
 * 数据源
 *
 * @author dy
 */
public enum ErrorEnmus {
    /**
     * 00000
     */
    ERROR_00000("00000", "成功"),
    /**
     * 00001
     */
    ERROR_00001("00001", "登陆失败"),
    /**
     * 00002
     */
    ERROR_00002("00002", "参数错误"),
    /**
     * 00003
     */
    ERROR_00003("00003", "未找到用户"),
    /**
     * 00004
     */
    ERROR_00004("00004", "密码错误"),
    /**
     * 00005
     */
    ERROR_00005("00005", "登出错误"),
    /**
     * 00006
     */
    ERROR_00006("00006", "未找到该用户"),
    /**
     * 00007
     */
    ERROR_00007("00007", "更新用户失败"),
    /**
     * 00008
     */
    ERROR_00008("00008", "原密码错误"),
    /**
     * 00009
     */
    ERROR_00009("00009", "输入两次密码不一样"),
    /**
     * 00010
     */
    ERROR_00010("00010", "变量组输入不正确"),
    /**
     * 00011
     */
    ERROR_00011("00011", "请不要重复插入"),
    /**
     * 00012
     */
    ERROR_00012("00012", "ad下限不能大于ad上限"),
    /**
     * 00013
     */
    ERROR_00013("00013", "输出下限不能大于输出上限"),
    /**
     * 00014
     */
    ERROR_00014("00014", "量程下限不能大于量程上限"),
    /**
     * 00015
     */
    ERROR_00015("00015", "请不要重复插入"),
    /**
     * 00016
     */
    ERROR_00016("00016", "请不要重复插入"),
    /**
     * 00017
     */
    ERROR_00017("00017", "请不要重复插入"),
    /**
     * 00018
     */
    ERROR_00018("00018", "请不要重复插入"),
    /**
     * 00019
     */
    ERROR_00019("00019", "数学表达式错误"),
    /**
     * 00020
     */
    ERROR_00020("00020", "数据类型错误"),


    /**
     * 100000
     */
    ERROR_10000("10000", "只读，不允许下发"),
    /**
     * 100001
     */
    ERROR_10001("10001", "没有查询到该设备"),


    /**
     * 99996
     */
    ERROR_99996("99996", "系统错误，请联系管理员"),
    /**
     * 99997
     */
    ERROR_99997("99997", "操作太频繁，请稍后再试"),
    /**
     * 99998
     */
    ERROR_99998("99998", "token过期"),
    /**
     * 10002
     */
    ERROR_10002("10002","设备不在线,命令已存库"),
    /**
     * 10003
     */
    ERROR_10003("10003","OtherMessage");


    /**
     * code
     */
    private String code;

    /**
     * message
     */
    private String message;

    ErrorEnmus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
