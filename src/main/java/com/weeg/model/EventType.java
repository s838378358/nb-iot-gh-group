package com.weeg.model;

/**
 * Created by SJ on 2020/8/13
 */
public class EventType {

    /**
     * 事件代码
     * 传入事件代码 返回事件名称
     *
     * @param num
     * @return
     */
    public static String eventType(String num) {
        String eventName = "";
        int n = Integer.valueOf(num, 16);
        if (num.equals("0001")) {
            eventName = "开阀：执行开阀动作";
        } else if (num.equals("0002")) {
            eventName = "关阀：执行关阀动作";
        } else if (num.equals("0003")) {
            eventName = "重新启动";
        } else if (num.equals("0004")) {
            eventName = "电量低";
        } else if (num.equals("0005")) {
            eventName = "电量不足";
        } else if (num.equals("0006")) {
            eventName = "磁干扰";
        } else if (num.equals("0007")) {
            eventName = "电源断电";
        } else if (num.equals("0008")) {
            eventName = "异常流量";
        } else if (num.equals("0009")) {
            eventName = "计量处理单元异常";
        } else if (n >= 40960 && n <= 45055) {
            eventName = "用户自定义事件码";
        }
        return eventName;
    }
}
