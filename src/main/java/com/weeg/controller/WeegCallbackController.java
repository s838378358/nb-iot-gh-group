package com.weeg.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.weeg.bean.*;
import com.weeg.configurer.ThreadConfig;
import com.weeg.handler.Factory;
import com.weeg.handler.Handler;
import com.weeg.service.*;
import com.weeg.util.AESUtil;
import com.weeg.util.DataFomat;
import com.weeg.util.MAC;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @Description: 提供给平台的回调接口
 * @ClassName: WeegCallbackController.java
 * @author: yuyan
 * @date: 2019年11月5日 下午1:14:04
 */
@RestController
@RequestMapping("/NBWeegServer/weeg")
public class WeegCallbackController {
    //配置日志
    private static final Logger LOG = LoggerFactory.getLogger(WeegCallbackController.class);
//    static Post post = new Post();
    //读取配置文件
    Props dataprops = PropsUtil.get("properties/data.properties");

    @Autowired
    IotPushRecvDataLogService iotPushRecvDataLogService;
    @Autowired
    DevRegInfoService devRegInfoService;
    @Autowired
    IotPushRecvReponseService iotPushRecvReponseService;
    @Autowired
    DevDataLogService devDataLogService;
    @Autowired
    DevControlCmdService devControlCmdService;
    @Autowired
    IotImeiStatusService iotimeistatusService;
    @Autowired
    IotImeistatusHisService iotimeistatushisService;
    @Autowired
    DevSecretKeyService devSecretKeyService;
    @Resource
    private ThreadConfig threadConfig;


//    /**
//     * NB订阅消息所用的回调地址，供平台验证推送地址
//     *
//     * @throws IOException
//     */
//    @RequestMapping(value = "/callbackIn", method = RequestMethod.GET)
//    public static void token(String msg, HttpServletResponse response) throws IOException {
//        String data = msg;
//        // 默认验证成功
//        // 设置响应头部,设置主体的编码格式是UTF-8
//        response.setHeader("content-type", "text/html;charset=UTF-8");
//        // 设置传输的编码格式
//        response.setCharacterEncoding("UTF-8");
//        Writer writer = response.getWriter();
//        // 将 字符串内容写入缓存
//        writer.write(data);
//        // 将缓存输出
//        writer.flush();
//        writer.close();
//    }

    /*
     * @Author SJ
     * NB订阅消息所用的回调地址
     * @Description
     * @Date 14:52 2020/7/14
     * @Param [body, response]
     * @return void
     **/
    @PostMapping(value = "/callbackIn")
    public void callbackIn (@RequestBody String body, HttpServletResponse response) throws Exception {
        // 接收到推送数据，首先向平台回复200 OK 设置响应头部,设置主体的编码格式是UTF-8
        response.setHeader("content-type", "text/html;charset=UTF-8");
        // 设置传输的编码格式
        response.setCharacterEncoding("UTF-8");
        Writer writer = response.getWriter();
        // 将 字符串内容写入缓存
        writer.write("200 OK");
        // 将缓存输出
        writer.flush();
        writer.close();

        //开启多线程
        CountDownLatch countDownLatch = new CountDownLatch(100);
        threadConfig.asyncServiceExecutor().execute(() -> {
            try{
                //        StandardizationDataPush dataPush = new StandardizationDataPush();
                //        Map<String, Object> ObjMap = dataPush.dataMap();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date date = new Date();
                // 将接受到的数据转换成json，只有里面带有value，才向服务器推送
                JSONObject object = JSONObject.fromObject(body);
                if (object.has("msg")) {
                    JSONObject obj = object.getJSONObject("msg");

                    //生成唯一识别码
                    String classid = UUID.randomUUID().toString();

                    //iotpushrecvdatalog 数据中有msg，将原始数据插入数据库
                    IotPushRecvDataLog iotPushRecvDataLog = new IotPushRecvDataLog();
                    iotPushRecvDataLog.setPushsource("iotPush");
                    iotPushRecvDataLog.setClassid(classid);
                    iotPushRecvDataLog.setPushinfo(body);
                    iotPushRecvDataLog.setPushtime(new Date());

                    //不管推送的信息是不是正确，将推送的信息原文存入数据库
                    iotPushRecvDataLogService.insert(iotPushRecvDataLog);

                    if (obj.has("value")) {
                        //获取推送地址中的value部分
                        String content = obj.getString("value");

                        //将value部分存入数据库
                        String iotSerial = obj.getString("dev_id");
                        String imei = obj.getString("imei");

                        //将imei对应的iotSerial更新到数据库
                        DevRegInfo regInfo = devRegInfoService.selectByImei(imei);
                        String DevserialID = regInfo.getDevserial();

                        //将value原文放入数据库
                        String date1 = formatter.format(date);
                        IotPushRecvReponse iotPushRecvReponse = new IotPushRecvReponse();
                        Date uploadtime = new Date();
                        iotPushRecvReponse.setClassid(classid);
                        iotPushRecvReponse.setIotserial(iotSerial);
                        iotPushRecvReponse.setUploadtime(uploadtime);
                        iotPushRecvReponse.setUploadtime1(date1);
                        iotPushRecvReponse.setUploadvalue(content);
                        iotPushRecvReponse.setDevserial(DevserialID);
                        iotPushRecvReponseService.insert(iotPushRecvReponse);

                        //获取data中platformcode对应的平台信息
                        String value = dataprops.getStr(regInfo.getPlatformcode());

                        //对数据进行解析
                        String dateBody = getDateBody(content, classid, imei);
                        //获取解析出来的 ：did-数据对象ID  mid-消息序号
                        String obj2 = JSONObject.fromObject(dateBody).getString("obj");
                        String did = JSONObject.fromObject(obj2).getString("数据对象id");
                        String mid = JSONObject.fromObject(obj2).getString("消息序号");
                        String j = JSONObject.fromObject(obj2).getString("数据域");

                        JSONObject returnObj = new JSONObject();
                        //将解析出来的数据插入数据库中
                        DevDataLog dataLog = new DevDataLog();
                        dataLog.setChildclassid(classid);
                        dataLog.setData(obj2);
                        dataLog.setTableData(JSONObject.fromObject(dateBody).getString("arr"));
                        dataLog.setDevserial(DevserialID);
                        dataLog.setIotserial(iotSerial);
                        dataLog.setDid(did);
                        dataLog.setDevtype(regInfo.getDevtype());
                        dataLog.setUpdatetime(date);
                        devDataLogService.insert(dataLog);


                        //如果是3001上报 注册请求 ，提取参数
//                        if (did.equals("3001")) {
//
//
//
//                        } else {
//                            //先查出最新的3001请求的classID，再根据classID查出3001请求的数据域
//                            IotPushRecvReponse iprr = iotPushRecvReponseService.selectClassid(regInfo.getDevserial(), "3001");
//                            String resclassid = iprr.getClassid();
//                            DevDataLog resdataLog = devDataLogService.selectByChildclassId(resclassid);
//                            String datas = resdataLog.getData();
//                            String datastr = JSONObject.fromObject(datas).getString("数据域");
//                            //                    //获取密钥版本号
//                            String keyname = JSONObject.fromObject(datastr).getString("密钥版本号");
//                            //根据密钥版本号、设备编号、IMEI号获取对应的密钥keyvalue
//                            DevSecretKey devSecretKey = null;
//                            try {
//                                devSecretKey = devSecretKeyService.selectSecretKeyvalue(keyname, DevserialID, imei);
//                            } catch (Exception e) {
//                                LOG.info("查询密钥异常：" + "密钥>>>" + keyname + "设备号>>>" + DevserialID + "imei>>>" + imei);
//                                e.printStackTrace();
//                            }
//                            String keyvalue = devSecretKey.getKeyvalue();
//                        }

                        //生成对3001的回复命令
                        if (did.equals("3001")) {
                            LOG.info("Start>>>>3001上报:" + content);
                            LOG.info("设备号:" + DevserialID);
                            String keyname = JSONObject.fromObject(j).getString("密钥版本号");
//                            LOG.info("当前密钥版本号"+keyname);

                            //更新 当前版本密钥为启用 必定执行
                            devSecretKeyService.updatenewusekeynameopen(imei, DevserialID, keyname);
//                            LOG.info("当前版本密钥是否启用"+upnewkey);

//                            //更新 根据密钥版本，更新另外一个密钥为不启用
//                            devSecretKeyService.updateoldusekeynameclose(imei, DevserialID, keyname);
//                            LOG.info("非当前版本密钥是否停用"+upoldkey);

                            //根据密钥版本号以及IMEI号, 数据库执行删除老版本密钥(默认版本除外)
                            devSecretKeyService.delOtherSecretKey(imei, keyname);
//                            LOG.info("删除老版本密钥(默认版本除外)"+deleteSecretKeyByImei);

                            //如果设备上报上来的是3001命令，先去获取3001里面设备的密钥版本号，查询对应的密钥参数，并启用新版本密钥，停用老版本密钥
                            DevSecretKey devSecretKey = devSecretKeyService.selectkeyvalue(keyname, DevserialID, imei);
                            String keyvalue = devSecretKey.getKeyvalue();
//                            LOG.info("密钥："+keyvalue);


                            String tongxunsuijima = JSONObject.fromObject(j).getString("通信随机码");
                            String repData = response3001("0000", tongxunsuijima, mid, keyvalue);

                            //拼接下发平台命令需要的信息
                            JSONObject params = new JSONObject();
                            params.put("NBId", regInfo.getIotserial());
                            params.put("imei", regInfo.getImei());
                            params.put("cmds", repData);
                            params.put("operator", value);

//                            String postUrl = dataprops.getStr(regInfo.getPlatformcode().substring(0, 1)) + "postDeviceCmdTou";

                            String result = SendToOneNetController.postDeviceCmdTou(params.toString());

                            LOG.info("回复3001参数:" + repData);
                            LOG.info("回复3001结果:" + result);
                            //对返回数据进行判断

                            if (JSONObject.fromObject(result).getString("errno").equals("0")) {
                                // 命令下发成功
                                returnObj.put("result", true);
                                returnObj.put("code", "");
                                returnObj.put("data", "");
                                returnObj.put("msg", "命令下发成功");

                                //将数据原文添加进数据库 ...
                                DevControlCmd devControlCmd = new DevControlCmd();
                                //生成唯一识别码
                                String randomID = UUID.randomUUID().toString();
                                Date ctrltime = new Date();
                                devControlCmd.setClassid(randomID);
                                devControlCmd.setDevserial(regInfo.getDevserial());
                                devControlCmd.setIotserial(regInfo.getIotserial());
                                devControlCmd.setDevtype(regInfo.getDevtype());
                                devControlCmd.setOpttype(regInfo.getPlatformcode());
                                devControlCmd.setDid(did);
                                devControlCmd.setCtrlvalue(repData);
                                devControlCmd.setCtrltime(ctrltime);
                                devControlCmd.setCtrltime1(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                                devControlCmd.setCmdFlag("1");
                                devControlCmd.setCmdNo(0);
                                devControlCmd.setCtrltype(tongxunsuijima);
                                devControlCmdService.insert(devControlCmd);
                                JSONArray arrays = new JSONArray();
                                JSONObject objres = new JSONObject();
                                objres.put("name", "上传时间");
                                objres.put("info", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(uploadtime));
                                arrays.add(objres);
                                objres.put("name", "回复时间");
                                objres.put("info", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(ctrltime));
                                arrays.add(objres);
                                iotPushRecvReponseService.updateConfirmtimeAndConfirmvlaue(ctrltime, repData, classid, arrays.toString());

                            } else {
                                returnObj.put("result", false);
                                returnObj.put("code", "");
                                returnObj.put("data", "");
                                returnObj.put("msg", JSONObject.fromObject(result).getString("error"));
                            }

                        } else if (did.equals("3003")) {


                            /**
                             * 数据标准化推送暂时不用，先注释
                             */
                            //数据标准化，推送至weegDat

                            //NB网络信号强度
//                            String RSRP = JSONObject.fromObject(j).getString("NB网络信号强度RSRP");
//                            String SNR = JSONObject.fromObject(j).getString("NB网络信号强度SNR");
//
//                            //当前累计气量
//                            String CVISC = JSONObject.fromObject(j).getString("当前累计气量");
//
//                            //阀门状态
//                            String devType = JSONObject.fromObject(j).getString("表状态");
//                            String SOTV = JSONObject.fromObject(devType).getString("阀门状态");
//
//                            //主电电池电压
//                            String BV = JSONObject.fromObject(j).getString("主电电池电压");
//
//                            //判断上报时间是否为每个月1号，（结算日） 来定义结算日体积
//                            boolean isMonthFirstDay = operationUtil.isMonthFirstDay();
//                            if(isMonthFirstDay){
//                                ObjMap.put("SDV",CVISC);
//                                LOG.info("结算日，用量："+CVISC);
//                            }else {
//                                ObjMap.put("SDV","");
//                            }
//
//                            //设置标准化推送数据
//                            ObjMap.put("WSSrsrp",RSRP);
//                            ObjMap.put("WSSsnr",SNR);
//                            ObjMap.put("CVISC",CVISC);
//                            ObjMap.put("SOTV",SOTV);
//                            ObjMap.put("BV",BV);
//                            //调用推送接口
//                            StandardizationDataPush objStandardization = new StandardizationDataPush();
//                            Object objsta = objStandardization.dataPush(ObjMap,DevserialID);
//
//                            LOG.info("数据标准化推送信息:" + objsta.toString());

                            DevRegInfo regInfo2 = devRegInfoService.selectByImei(imei);
                            String devserial = regInfo2.getDevserial();
                            //回复3001成功，接收到3003以后，继续判断是否有无缓存命令  遍历list   cmdFlag=0表示有未下发的缓存命令
                            List<DevControlCmd> dctc = devControlCmdService.selectBySerialandcmdFlag(devserial, "0");
                            //将缓存命令按cmdNo的编号来排序    -----正序排序
                            Collections.sort(dctc, Comparator.comparingInt(DevControlCmd::getCmdNo));
                            //  >0 有缓存命令
                            if (dctc.size() > 0) {

                                loop:
                                for (int i = 0; i < dctc.size(); i++) {
                                    DevControlCmd devcontrolcmd = dctc.get(i);
                                    //下发一条命令
                                    //int cmdNo = Integer.valueOf(devcontrolcmd.getCmdNo());
                                    //先查出最新的3001请求的classID，再根据classID查出3001请求的数据域
                                    IotPushRecvReponse iprr = iotPushRecvReponseService.selectClassid(devcontrolcmd.getDevserial(), "3001");
                                    String resclassid = iprr.getClassid();
                                    DevDataLog resdataLog = devDataLogService.selectByChildclassId(resclassid);
                                    String datas = resdataLog.getData();
                                    String datastr = JSONObject.fromObject(datas).getString("数据域");
                                    String random = JSONObject.fromObject(datastr).getString("通信随机码");
                                    String keyname = JSONObject.fromObject(datastr).getString("密钥版本号");
                                    DevSecretKey devSecretKey = devSecretKeyService.selectkeyvalue(keyname, DevserialID, imei);
                                    String keyvalue = devSecretKey.getKeyvalue();
                                    CmdController cmdctroller = new CmdController();
                                    String cmdType = devcontrolcmd.getCmdType();
                                    String dida = devcontrolcmd.getDid();
                                    String cmddata = devcontrolcmd.getCmdData();
                                    String reread = devcontrolcmd.getReRead();
                                    String cmd = "";
                                    if (cmdType.equals("RData")) {
                                        //读状态数据
                                        cmd = cmdctroller.cmdReadBody(dida);
                                    } else if (cmdType.equals("WData")) {
                                        if (dida.equals("2009")) {
                                            cmd = writedata2009(dida, cmddata, reread, random, keyvalue, regInfo.getDevserial(), imei);
                                        } else {
                                            //写状态数据
                                            cmd = cmdctroller.cmdWriteBody(dida, cmddata, reread, random, keyvalue, regInfo.getDevserial(), imei);
                                        }
                                    } else if (cmdType.equals("Record")) {
                                        //读记录数据
                                        cmd = cmdctroller.cmdRecodeBody(dida, cmddata, random, keyvalue, regInfo.getDevserial(), imei);
                                    }
                                    //拼接下发平台命令需要的信息
                                    JSONObject params = new JSONObject();
                                    params.put("NBId", regInfo2.getIotserial());
                                    params.put("imei", regInfo2.getImei());
                                    params.put("cmds", cmd);
                                    params.put("operator", value);
                                    // 向平台下发命令
//                                    String postUrl = dataprops.getStr(regInfo2.getPlatformcode().substring(0, 1)) + "postDeviceCmdTou";
//                                    String result = post.post(postUrl, params.toString());

                                    String result = SendToOneNetController.postDeviceCmdTou(params.toString());
                                    if (JSONObject.fromObject(result).getString("errno").equals("0")) {
                                        // 命令下发成功
                                        returnObj.put("result", true);
                                        returnObj.put("code", "");
                                        returnObj.put("data", "");
                                        returnObj.put("msg", "命令下发成功");
                                        //更新缓存命令的cmdno = 0 、   cmdFlag = 1(已下发)
                                        devControlCmdService.updatecmdNo(devcontrolcmd.getCmdNo(), devcontrolcmd.getDevserial(), cmd, random);

                                        //下发的命令向平台请求成功以后，break loop，跳出最外层循环，因为下发出去的命令要判断是否有回复，所以命令是一条一条发
                                        break loop;
                                    } else {
                                        returnObj.put("result", false);
                                        returnObj.put("code", "");
                                        returnObj.put("data", "");
                                        returnObj.put("msg", JSONObject.fromObject(result).getString("error"));
                                    }
                                }
                            } else {
                                //没有缓存命令，线程等待2秒，下发3002
//                                ThreadUtil.sleep(8000);
                                ResponseController responseController = new ResponseController();
                                responseController.response3002(devSecretKeyService, devDataLogService, iotPushRecvReponseService,
                                        devControlCmdService, devRegInfoService, iotimeistatusService, devserial);
                            }
                        } else {
                            DevRegInfo regInfo2 = devRegInfoService.selectByImei(imei);
                            String devserial = regInfo2.getDevserial();
                            //回复3001成功，接收到3003以后，继续判断是否有无缓存命令  遍历list   cmdFlag=0表示有未下发的缓存命令
                            List<DevControlCmd> dctc = devControlCmdService.selectBySerialandcmdFlag(devserial, "0");
                            //将缓存命令按cmdNo的编号来排序    -----正序排序
                            Collections.sort(dctc, Comparator.comparingInt(DevControlCmd::getCmdNo));
                            //  >0 有缓存命令
                            if (dctc.size() > 0) {

                                loop:
                                for (int i = 0; i < dctc.size(); i++) {
                                    DevControlCmd devcontrolcmd = dctc.get(i);
                                    //下发一条命令
//							int cmdNo = Integer.valueOf(devcontrolcmd.getCmdNo());
                                    //先查出最新的3001请求的classID，再根据classID查出3001请求的数据域
                                    IotPushRecvReponse iprr = iotPushRecvReponseService.selectClassid(devcontrolcmd.getDevserial(), "3001");
                                    String resclassid = iprr.getClassid();
                                    DevDataLog resdataLog = devDataLogService.selectByChildclassId(resclassid);
                                    String datas = resdataLog.getData();
                                    String datastr = JSONObject.fromObject(datas).getString("数据域");
                                    String random = JSONObject.fromObject(datastr).getString("通信随机码");
                                    String keyname = JSONObject.fromObject(datastr).getString("密钥版本号");
                                    DevSecretKey devSecretKey = devSecretKeyService.selectkeyvalue(keyname, DevserialID, imei);
                                    String keyvalue = devSecretKey.getKeyvalue();
                                    CmdController cmdctroller = new CmdController();
                                    String cmdType = devcontrolcmd.getCmdType();
                                    String dida = devcontrolcmd.getDid();
                                    String cmddata = devcontrolcmd.getCmdData();
                                    String reread = devcontrolcmd.getReRead();
                                    String cmd = "";
                                    if (cmdType.equals("RData")) {
                                        //读状态数据
                                        cmd = cmdctroller.cmdReadBody(dida);
                                    } else if (cmdType.equals("WData")) {
                                        if (dida.equals("2009")) {
                                            cmd = writedata2009(dida, cmddata, reread, random, keyvalue, regInfo.getDevserial(), imei);
                                        } else {
                                            //写状态数据
                                            cmd = cmdctroller.cmdWriteBody(dida, cmddata, reread, random, keyvalue, regInfo.getDevserial(), imei);
                                        }
                                    } else if (cmdType.equals("Record")) {
                                        //读记录数据
                                        cmd = cmdctroller.cmdRecodeBody(dida, cmddata, random, keyvalue, regInfo.getDevserial(), imei);
                                    }
                                    //拼接下发平台命令需要的信息
                                    JSONObject params = new JSONObject();
                                    params.put("NBId", regInfo2.getIotserial());
                                    params.put("imei", regInfo2.getImei());
                                    params.put("cmds", cmd);
                                    params.put("operator", value);
                                    // 向平台下发命令
//                                    String postUrl = dataprops.getStr(regInfo2.getPlatformcode().substring(0, 1)) + "postDeviceCmdTou";
//                                    String result = post.post(postUrl, params.toString());
                                    String result = SendToOneNetController.postDeviceCmdTou(params.toString());

                                    if (JSONObject.fromObject(result).getString("errno").equals("0")) {
                                        // 命令下发成功
                                        returnObj.put("result", true);
                                        returnObj.put("code", "");
                                        returnObj.put("data", "");
                                        returnObj.put("msg", "命令下发成功");
                                        //更新缓存命令的cmdno = 0 、   cmdFlag = 1(已下发)
                                        devControlCmdService.updatecmdNo(devcontrolcmd.getCmdNo(), devcontrolcmd.getDevserial(), cmd, random);

                                        //下发的命令向平台请求成功以后，break loop，跳出最外层循环，因为下发出去的命令要判断是否有回复，所以命令是一条一条发
                                        break loop;

                                    } else {
                                        returnObj.put("result", false);
                                        returnObj.put("code", "");
                                        returnObj.put("data", "");
                                        returnObj.put("msg", JSONObject.fromObject(result).getString("error"));
                                    }
                                }
                            } else {
                                //没有缓存命令，线程等待2秒，下发3002
//                                ThreadUtil.sleep(8000);
                                ThreadUtil.waitForDie();
                                ResponseController responseController = new ResponseController();
                                responseController.response3002(devSecretKeyService, devDataLogService, iotPushRecvReponseService,
                                        devControlCmdService, devRegInfoService, iotimeistatusService, devserial);
                            }
                        }
                    } else if (obj.has("status")) {
                        //获取推送地址中的status部分
                        String status = obj.getString("status");
                        String imei = obj.getString("imei");
                        //根据IMEI号查询设备状态信息
                        IotImeiStatus iis = iotimeistatusService.selectByimei(imei);

                        //有设备状态信息 更新最新设备状态
                        if (iis != null) {
                            //获取设备编号
                            String devserial = iis.getDevserial();

                            //如果有数据，更新这台设备的设备状态  status = 1
                            iotimeistatusService.updateStatus(status, imei);

                            //同时将设备状态插入历史数据表
                            String iotserial = iis.getIotserial();
                            String at = obj.getString("at");
                            String login_type = obj.getString("login_type");
                            String type = obj.getString("type");
                            String msg_signature = object.getString("msg_signature");
                            String nonce = object.getString("nonce");
                            IotImeiStatusHis iotimeistatushis = new IotImeiStatusHis();
                            iotimeistatushis.setImei(imei);
                            iotimeistatushis.setDevserial(devserial);
                            iotimeistatushis.setStatus(status);
                            iotimeistatushis.setIotserial(iotserial);
                            iotimeistatushis.setAt(at);
                            iotimeistatushis.setLogin_type(login_type);
                            iotimeistatushis.setType(type);
                            iotimeistatushis.setMsg_signature(msg_signature);
                            iotimeistatushis.setNonce(nonce);
                            iotimeistatushis.setStatustime(new Date());
                            iotimeistatushisService.insert(iotimeistatushis);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                Long l = countDownLatch.getCount();
//            logger.info("current threads --> " + l);
                countDownLatch.countDown();  // 这个不管是否异常都需要数量减,否则会被堵塞无法结束
            }
        });


//        ThreadUtil.execAsync(() -> {
//            try {


//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        });

    }

    /**
     * 接收到的推送数据进行解析，获取通用格式
     *
     * @throws Exception
     * @Function: WeegCallbackController.java
     * @param:描述1描述
     * @return：返回结果描述
     * @throws：异常描述
     * @author: yuyan
     * @date: 2019年11月5日 下午1:16:58
     */
    public String getDateBody(String content, String classid, String imei) throws Exception {
        JSONObject object = new JSONObject();
        DataFomat dataFomat = new DataFomat();
        byte[] b = dataFomat.toBytes(content);

        // 将byte数组的每一位都转换成十六进制
        String[] binaryData = new String[b.length];
        for (int i = 0; i < b.length; i++) {
            binaryData[i] = dataFomat.toHex(b[i]);
        }

        // 获取数据域
        String text = "";
        for (int i = 9; i < binaryData.length - 3; i++) {
            text = text + binaryData[i];
        }

        // 获取数据域部分内容
        String dateBody = date(text, binaryData[7] + binaryData[8], content, imei);
//        System.out.println("did:" + binaryData[7] + binaryData[8]);
        //将每一条记录对应的did更新到数据库
        String did = binaryData[7] + binaryData[8];
        iotPushRecvReponseService.updateDidByClassid(did, classid);

        object.put("帧头", binaryData[0]);
        object.put("协议类型", binaryData[1]);
        object.put("协议框架版本", binaryData[2]);
        object.put("帧长度", binaryData[3] + binaryData[4]);
        object.put("消息序号", binaryData[5]);
        object.put("控制域", binaryData[6]);
        object.put("数据对象id", binaryData[7] + binaryData[8]);
        object.put("数据域", dateBody);
        object.put("校验域", binaryData[binaryData.length - 3] + binaryData[binaryData.length - 2]);
        object.put("帧尾", binaryData[binaryData.length - 1]);

        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();

        obj.put("name", "帧头");
        obj.put("value", binaryData[0]);
        array.add(obj);
        obj.put("name", "协议类型");
        obj.put("value", binaryData[1]);
        array.add(obj);
        obj.put("name", "协议框架版本");
        obj.put("value", binaryData[2]);
        array.add(obj);
        obj.put("name", "帧长度");
        obj.put("value", Integer.parseInt(binaryData[3] + binaryData[4], 16));
        array.add(obj);
        obj.put("name", "消息序号");
        obj.put("value", binaryData[5]);
        array.add(obj);
        obj.put("name", "控制域");
        obj.put("value", binaryData[6]);
        array.add(obj);
        obj.put("name", "数据对象id");
        obj.put("value", binaryData[7] + binaryData[8]);
        array.add(obj);
        obj.put("name", "数据域");
        obj.put("value", dateBody);
        array.add(obj);
        obj.put("name", "校验域");
        obj.put("value", binaryData[binaryData.length - 3] + binaryData[binaryData.length - 2]);
        array.add(obj);
        obj.put("name", "帧尾");
        obj.put("value", binaryData[binaryData.length - 1]);
        array.add(obj);

        JSONObject re = new JSONObject();
        re.put("obj", object);
        re.put("arr", array);

        return re.toString();

    }

//    public static void main(String[] args) throws Exception {
//		callbackIn(null,null);
//		String dateBody2 = getDateBody(content,"123");
//		System.out.println(dateBody2);
//		String content = "680001000d0f040001018c1816";
//		WeegCallbackController wcc = new WeegCallbackController();
//		String st = wcc.getDateBody(content,"123");
//		System.out.println(st);
//    }


    // 对数据域进行解析
    public String date(String content, String mid, String ori, String imeikey) {
        JSONObject object = new JSONObject();
        AESUtil aesUtil = new AESUtil();
        DataFomat dataFomat = new DataFomat();
        String keyvalue = "";

        //如果是3001的上报数据，不去获取解密密钥
        if(!"3001".equals(mid)){
            //获取解密密钥
            DevSecretKey devSecretKey2 = devSecretKeyService.selectkeyvalueandname(imeikey);
            keyvalue = devSecretKey2.getKeyvalue();
        }

        //获取控制域
        DataFomat dataFomatori = new DataFomat();
        byte[] bori = dataFomatori.toBytes(ori);
        //ori 将byte数组的每一位都转换成十六进制
        String[] conData = new String[bori.length];
        for (int i = 0; i < bori.length; i++) {
            conData[i] = dataFomatori.toHex(bori[i]);
        }
        //获取控制域参数 C
        String constr = conData[6];
        //16进制字符串转成 二进制字符串   04 - 0000 0100
        String conbyte = dataFomatori.hexString2binaryString(constr);
        //二进制字符串转成16进制字符串
        String hexstr = dataFomatori.binaryString2hexString(conbyte);

        byte[] b = dataFomat.toBytes(content);
        //content 将byte数组的每一位都转换成十六进制
        String[] binaryData = new String[b.length];
        for (int i = 0; i < b.length; i++) {
            binaryData[i] = dataFomat.toHex(b[i]);
        }

        JSONObject resultObject = null;
        //3001上报
        Handler strategy = Factory.getInvokeStrategy(mid);
        if ("3001".equals(mid)){
            resultObject = strategy.upload3001Handler(binaryData,b,object);
        }else if ("3003".equals(mid)){
            resultObject = strategy.upload3003Handler(binaryData,b,object,keyvalue);
        }else if ("0001".equals(mid)){
            resultObject = strategy.upload0001Handler(mid,object,hexstr,binaryData);
        }else if ("0002".equals(mid)){
            resultObject = strategy.upload0002Handler(mid,object,hexstr,binaryData);
        }else if ("0003".equals(mid)){
            resultObject = strategy.upload0003Handler(b,mid,object,hexstr,binaryData,keyvalue);
        }else if ("0004".equals(mid)){
            resultObject = strategy.upload0004Handler(object,mid,binaryData);
        }else if ("0005".equals(mid)){
            resultObject = strategy.upload0005Handler(object,mid,binaryData);
        }else if ("0006".equals(mid)){
            resultObject = strategy.upload0006Handler(object,mid,binaryData);
        }else if ("000b".equals(mid)){
            resultObject = strategy.upload000bHandler(object,mid,binaryData);
        }else if ("000e".equals(mid)){
            resultObject = strategy.upload000eHandler(object,mid,binaryData,hexstr);
        }else if ("0010".equals(mid)){
            resultObject = strategy.upload0010Handler(object,mid,binaryData,hexstr);
        }else if ("0011".equals(mid)){
            resultObject = strategy.upload0011Handler(object,mid,binaryData);
        }else if ("0012".equals(mid)){
            resultObject = strategy.upload0012Handler(object,mid,binaryData);
        }else if ("0013".equals(mid)){
            resultObject = strategy.upload0013Handler(object,mid,binaryData);
        }else if ("0014".equals(mid)){
            resultObject = strategy.upload0014Handler(object,mid,binaryData);
        }else if ("0015".equals(mid)){
            resultObject = strategy.upload0015Handler(object,mid,binaryData);
        }else if ("0016".equals(mid)){
            resultObject = strategy.upload0016Handler(object,mid,binaryData);
        }else if ("0017".equals(mid)){
            resultObject = strategy.upload0017Handler(object,mid,binaryData);
        }else if ("1000".equals(mid)){
            resultObject = strategy.upload1000Handler(object,mid,binaryData,hexstr);
        }



        //3003上报
//        Factory.getInvokeStrategy(mid)
        // 针对3001协议的解析
//        if (mid.equals("3001")) {

//            String devSerial = "";
//            // 第10位表示表号参数的长度
//            for (int i = 11; i < 42; i++) {
//                devSerial = devSerial + binaryData[i];
//            }
//            devSerial = dataFomat.hexStr2Str(devSerial);
//
//            // 模组型号
//            String modelType = "";
//            for (int i = 44; i < 54; i++) {
//                if ("00".equals(binaryData[i])) {
//                    break;
//                } else {
//                    modelType += binaryData[i];
//                }
//            }
//            String modelTypeascii = dataFomat.convertHexToString(modelType);
//
//            // 通信随机码
//            String tongxinsuijima = "";
//            for (int i = 61; i < 77; i++) {
//                tongxinsuijima = tongxinsuijima + binaryData[i];
//            }
//
//            String Celled = "";
//            for (int i = 82; i < 88; i++) {
//                Celled = Celled + binaryData[i];
//            }
//
//            // imei转换成字符串
//            String imei = "";
//            for (int i = 90; i < 105; i++) {
//                imei = imei + binaryData[i];
//            }
//            imei = dataFomat.hexStr2Str(imei);
//
//
//            object.put("时钟",
//                    binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3] + binaryData[4] + binaryData[5]);
//            object.put("表厂商ID", binaryData[6] + binaryData[7]);
//            object.put("表型号", binaryData[8] + binaryData[9]);
//            // 表号参数转换成十进制
//            object.put("表号长度", Integer.parseInt(binaryData[10], 16));
//            object.put("表号参数", devSerial.replaceAll("\\u0000", ""));
//            object.put("模组厂商ID", binaryData[43]);
//            object.put("模组型号", modelTypeascii);
////                System.out.println("模组型号：" + modelTypeascii);
//            object.put("开户状态", binaryData[54]);
//            object.put("运营商信息", binaryData[55]);
//            object.put("通信模式", binaryData[56]);
//            object.put("嵌软版本", binaryData[57] + binaryData[58]);
//            object.put("应用协议版本", binaryData[59] + binaryData[60]);
//            object.put("通信随机码", tongxinsuijima);
//            // 信号强度转换成有符号整数
//            object.put("NB网络信号强度RSRP", DataFomat.BigEndian(b, 77, 2));
//            object.put("NB网络信号强度SNR", DataFomat.BigEndian(b, 79, 2));
//            // 信噪比转换成十进制
////            object.put("信噪比", DataFomat.BigEndian(b, 79, 2));
//            object.put("ECL覆盖等级", Integer.valueOf(binaryData[81], 16));
//            object.put("Celled", Celled);
//            // REAL_NEARFCN转换成十进制
//            object.put("REAL_NEARFCN", Integer.parseInt(binaryData[88] + binaryData[89], 16));
//            object.put("IMEI", imei);
//            object.put("模组固件版本", Integer.parseInt(binaryData[105], 16));
//            object.put("密钥版本号", binaryData[106]);

//			String mac = tongxinsuijima + content;
//			byte[] b1 = dataFomat.toBytes(mac);
//			MAC mac2 = new MAC();
//			String maca = mac2.HMACSHA256(b1, tongxinsuijima);
//			System.out.println("maca:"+maca);



//        if (mid.equals("3003")) {
//        } else if (mid.equals("3003")) {

//            // 获取明文
//            String body = "";
//            int length = b.length / 16;
//
//            byte[] resultBytes = new byte[b.length];
//            // 将密文按照16的长度进行分割
//            for (int i = 0; i < length; i++) {
//                // 创建一个长度是16的数组,用于存放每一段数组
//                byte[] newTxet = new byte[16];
//                for (int j = 0; j < 16; j++) {
//                    newTxet[j] = b[i * 16 + j];
//                }
//                String originalString = dataFomat.bytes2HexString(aesUtil.decryptAES(newTxet, keyvalue));
//                body = body + originalString;
//            }
//            // 去掉body中的空格
//            body = body.replace(" ", "");
//
//            // 将获取的数据域转换成byte[]
//            byte[] b2 = dataFomat.toBytes(body);
//
//            // 将byte转换成string
//            String strdata3 = "";
//            String[] binaryData3 = new String[b2.length];
//            for (int i = 0; i < b2.length; i++) {
//                binaryData3[i] = dataFomat.toHex(b2[i]);
//                strdata3 += binaryData3[i];
//            }
////            System.out.println(strdata3);
//
//            // 1、时钟
//            object.put("时钟", binaryData3[0] + binaryData3[1] + binaryData3[2] + binaryData3[3] + binaryData3[4]
//                    + binaryData3[5]);
//
//            // 2、当前累计气量
//            String gas = "";
//            for (int i = 6; i < 10; i++) {
//                gas += binaryData3[i];
//            }
////                int gass = Integer.parseInt(gas, 16) / 1000;
//            long gass = Long.parseLong(gas, 16) / 1000;
////                System.out.println(gass);
//            object.put("当前累计气量", gass);
//
//            // 3、表状态
//
//            byte[] binarybyte1 = dataFomat.toBytes(binaryData3[10]);
//            byte[] binarybyte2 = dataFomat.toBytes(binaryData3[11]);
//            byte data1 = binarybyte1[0];
//            byte data2 = binarybyte2[0];
//            String bit1 = dataFomat.byteToBit(data1);
//            String bit2 = dataFomat.byteToBit(data2);
//            char[] bit3 = bit1.toCharArray();
//            char[] bit4 = bit2.toCharArray();
//            String[] stb1 = new String[bit3.length];
//            String[] stb2 = new String[bit4.length];
//            for (int i = stb1.length - 1; i >= 0; i--) {
//                stb1[stb1.length - i - 1] = String.valueOf(bit3[i]);
//                stb2[stb1.length - i - 1] = String.valueOf(bit4[i]);
//            }
////				for (int i = 0; i < stb1.length; i++) {
////					stb1[i] = String.valueOf(bit3[i]);
////					stb2[i] = String.valueOf(bit4[i]);
////				}
//            // System.out.println(bit1+","+bit2);
//            JSONObject btypeobject = new JSONObject();
//            btypeobject.put("阀门状态", stb1[0]);
//            btypeobject.put("表具被强制命令关阀", stb1[1]);
//            btypeobject.put("主电电量不足", stb1[2]);
//            btypeobject.put("备电电量不足", stb1[3]);
//            btypeobject.put("无备电，系统不能正常工作", stb1[4]);
//            btypeobject.put("过流", stb1[5]);
//            btypeobject.put("阀门直通", stb1[6]);
//            btypeobject.put("外部报警触发", stb1[7]);
//
//            btypeobject.put("计量模块异常", stb2[0]);
//            btypeobject.put("多少天不用气导致阀门关闭", stb2[1]);
//            btypeobject.put("曾出现多天没有远传数据上发成功而导致阀门关闭", stb2[2]);
//            btypeobject.put("电磁干扰", stb2[3]);
//            btypeobject.put("未定义4", stb2[4]);
//            btypeobject.put("未定义5", stb2[5]);
//            btypeobject.put("未定义6", stb2[6]);
//            btypeobject.put("未定义7", stb2[7]);
//
//            object.put("表状态", btypeobject);
//
//            // 4、NB网络信号强度
//            object.put("NB网络信号强度RSRP", DataFomat.BigEndian(b2, 12, 2));
//            object.put("NB网络信号强度SNR", DataFomat.BigEndian(b2, 14, 2));
//
//            // 5、表厂自定义表状态
////                String tableStatus = "";
////                for (int i = 16; i < 20; i++) {
////                    tableStatus += binaryData3[i];
////                }
////            byte[] bytes = dataFomat.toBytes(binaryData3[16]);
////            byte byte1 = bytes[0];
////
////            //byte1 & 0x03
////            String byte1tobit = dataFomat.byteToBit(byte1);
////            char[] bittochar = byte1tobit.toCharArray();
////            String[] stb3 = new String[bittochar.length];
////            for (int i = 0; i < stb1.length; i++) {
////                stb3[i] = String.valueOf(bittochar[i]);
////            }
//            JSONObject stb3object = new JSONObject();
//
//            if("00".equals(binaryData3[16])){
//                stb3object.put("阀门状态","开阀");
//            }else if ("01".equals(binaryData3[16])){
//                stb3object.put("阀门状态","关阀");
//            }else if ("03".equals(binaryData3[16])){
//                stb3object.put("阀门状态","异常");
//            }
//            object.put("表厂自定义表状态",stb3object);
//
//            // 6、供电类型
//            long powerType = Long.parseLong(binaryData3[20], 16);
//            object.put("供电类型", powerType);
//
//            // 7、主电电池电压
//            String EV = "";
//            for (int i = 21; i < 23; i++) {
//                EV += binaryData3[i];
//            }
//            long EVs = Long.parseLong(EV, 16) > 0 ? Long.parseLong(EV, 16) : 0;
//            Float Evs = Float.valueOf(EVs) / 1000F;
//            object.put("主电电池电压", Evs);
//
//            // 8、主电电池百分比
//            object.put("主电电池百分比", Long.parseLong(binaryData3[23], 16));
//
//            // 9、昨天每小时用气日志
//            String yq = "";
//            for (int i = 28; i < 124; i++) {
//                yq += binaryData3[i];
//            }
//            object.put("昨天每小时用气日志", binaryData3[24] + binaryData3[25] + binaryData3[26] + binaryData3[27] + yq);
//
//            // 10、前5天日用气记录
//            String fd = "";
//            for (int i = 128; i < 148; i++) {
//                fd += binaryData3[i];
//            }
//            object.put("前5天日用气记录", binaryData3[124] + binaryData3[125] + binaryData3[126] + binaryData3[127] + fd);
//        } else if ("0001".equals(mid)) {
//        if ("0001".equals(mid)) {
//            //阀门状态 RW
//            if ("04".equals(hexstr)) {
//                object.put("阀门状态", binaryData[0]);
//            } else if ("05".equals(hexstr)) {
//                //错误码
//                String errornum = errorcode(binaryData[0] + binaryData[1]);
//                object.put("错误码", errornum);
//            }
//        } else if ("0002".equals(mid)) {
//        if ("0002".equals(mid)) {
//            //时钟 RW
//            if ("04".equals(hexstr)) {
//                String value = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3] + binaryData[4] + binaryData[5];
//                object.put("时钟", value);
//            } else if ("05".equals(hexstr)) {
//                //错误码
//                String errornum = errorcode(binaryData[0] + binaryData[1]);
//                object.put("错误码", errornum);
//            }
//        } else if ("0003".equals(mid)) {
//        if ("0003".equals(mid)) {
//
//            //当前累计气量 R
//            //判断是读数据--->上行(04)
//            if ("04".equals(hexstr)) {
//                //解密  获取明文
//                String body = "";
//                int length = b.length;
//                int cd;
//                if (length % 16 == 0) {
//                    cd = length / 16;
//                } else {
//                    cd = length / 16 + 1;
//                }
//                for (int i = 0; i < cd; i++) {
//                    // 创建一个长度是16的数组,用于存放每一段数组
//                    byte[] newTxet = new byte[16];
//                    for (int j = 0; j < 16; j++) {
//                        if ((i * 16 + j) <= length - 1) {
//                            newTxet[j] = b[i * 16 + j];
//                        } else {
//                            newTxet[j] = 0;
//                        }
//                    }
//                    String originalString = dataFomat.bytes2HexString(aesUtil.decryptAES(newTxet, keyvalue));
//                    body = body + originalString;
//                }
//                // 去掉body中的空格
//                body = body.replaceAll(" ", "");
//                // 将获取的数据域转换成byte[]
//                byte[] b2 = dataFomat.toBytes(body);
//
//                // 将byte转换成string
//                String[] binaryData3 = new String[b2.length];
//                for (int i = 0; i < b2.length; i++) {
//                    binaryData3[i] = dataFomat.toHex(b2[i]);
//                }
//                String bindata = binaryData3[0] + binaryData3[1] + binaryData3[2] + binaryData3[3];
//
//                long ct = Long.parseLong(bindata, 16);
//                Double evs = Double.valueOf(ct * 1.0 / 1000);
//                object.put("当前累计气量", String.format("%.3f", evs));
//
//                //判断是写数据--->上行(05)
//            } else if (hexstr.equals("05")) {
//                //错误码
//                String errornum = errorcode(binaryData[0] + binaryData[1]);
//                object.put("错误码", errornum);
//            }
//        } else if ("0004".equals(mid)) {
//        if ("0004".equals(mid)) {
//            //主电电压 R
//            String bindata = binaryData[0] + binaryData[1];
//
//            long ct = Long.parseLong(bindata, 16);
//            Double evs = Double.valueOf(ct * 1.0 / 1000);
//            object.put("主电电压", String.format("%.3f", evs) + "V");

//        } else if ("0005".equals(mid)) {
//        if ("0005".equals(mid)) {
//
//            //主电电量百分比 R
//            object.put("主电电量百分比", binaryData[0]);
//        } else if (mid.equals("0006")) {
//        if (mid.equals("0006")) {
//            //备电电压
//            String bindata = binaryData[0] + binaryData[1];
//
//            long ct = Long.parseLong(bindata, 16);
//            Double evs = Double.valueOf(ct * 1.0 / 1000);
//            object.put("备电电压", String.format("%.3f", evs) + "V");

//        } else if ("000b".equals(mid) || "000B".equals(mid)) {
//        if ("000b".equals(mid) || "000B".equals(mid)) {
//            byte[] binarybyte1 = dataFomat.toBytes(binaryData[0]);
//            byte[] binarybyte2 = dataFomat.toBytes(binaryData[1]);
//            byte data1 = binarybyte1[0];
//            byte data2 = binarybyte2[0];
//            String bit1 = dataFomat.byteToBit(data1);
//            String bit2 = dataFomat.byteToBit(data2);
//            char[] bit3 = bit1.toCharArray();
//            char[] bit4 = bit2.toCharArray();
//            String[] stb1 = new String[bit3.length];
//            String[] stb2 = new String[bit4.length];
//            for (int i = stb1.length - 1; i >= 0; i--) {
//                stb1[stb1.length - i - 1] = String.valueOf(bit3[i]);
//                stb2[stb1.length - i - 1] = String.valueOf(bit4[i]);
//            }
//            JSONObject btypeobject = new JSONObject();
//            btypeobject.put("阀门状态", stb1[0]);
//            btypeobject.put("表具被强制命令关阀", stb1[1]);
//            btypeobject.put("主电电量不足", stb1[2]);
//            btypeobject.put("备电电量不足", stb1[3]);
//            btypeobject.put("无备电，系统不能正常工作", stb1[4]);
//            btypeobject.put("过流", stb1[5]);
//            btypeobject.put("阀门直通", stb1[6]);
//            btypeobject.put("外部报警触发", stb1[7]);
//
//            btypeobject.put("计量模块异常", stb2[0]);
//            btypeobject.put("多少天不用气导致阀门关闭", stb2[1]);
//            btypeobject.put("曾出现多天没有远传数据上发成功而导致阀门关闭", stb2[2]);
//            btypeobject.put("电磁干扰", stb2[3]);
//            btypeobject.put("面罩拆卸", stb2[4]);
//            btypeobject.put("未定义5", stb2[5]);
//            btypeobject.put("未定义6", stb2[6]);
//            btypeobject.put("未定义7", stb2[7]);
//
//            object.put("表状态", btypeobject);
//        } else if ("000e".equals(mid) || "000E".equals(mid)) {
//        if ("000e".equals(mid) || "000E".equals(mid)) {
//            //开户状态RW
//            if (hexstr.equals("04")) {
//                object.put("开户状态", binaryData[0]);
//            } else if (hexstr.equals("05")) {
//                //错误码
//                String errornum = errorcode(binaryData[0] + binaryData[1]);
//                object.put("错误码", errornum);
//            }
//        } else if ("0010".equals(mid)) {
//        if ("0010".equals(mid)) {
//            //余量状态RW
//            if (hexstr.equals("04")) {
//                object.put("余量状态", binaryData[0]);
//            } else if (hexstr.equals("05")) {
//                //错误码
//                String errornum = errorcode(binaryData[0] + binaryData[1]);
//                object.put("错误码", errornum);
//            }
//        } else if (mid.equals("0011")) {
//        if (mid.equals("0011")) {
//            //NB网络信号强度R
//            object.put("RSRP", binaryData[0] + binaryData[1]);
//            object.put("SNR", binaryData[2] + binaryData[3]);
//        } else if (mid.equals("0012")) {
//        if (mid.equals("0012")) {
//            //通信失败计数R
//            object.put("通信失败计数", binaryData[0] + binaryData[1]);
//        } else if (mid.equals("0013")) {
//        if (mid.equals("0013")) {
//            //ECL覆盖等级R
//            object.put("ECL覆盖等级", binaryData[0]);
            //object.put("ECL覆盖等级", binaryData[0] + binaryData[1]);
//        } else if (mid.equals("0014")) {
//        if (mid.equals("0014")) {
//            String data = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3] + binaryData[4] + binaryData[5];
//            if (data.length() < 12) {
//                String body = "";
//                for (int i = 0; i < 12 - data.length(); i++) {
//                    body += "0";
//                }
//                data = body + data;
//                object.put("CellID", data);
//            } else {
//                object.put("CellID", data);
//            }
//        } else if (mid.equals("0015")) {
//        if (mid.equals("0015")) {
//            object.put("REAL_NEARFCN", binaryData[0] + binaryData[1]);
//        } else if (mid.equals("0016")) {
//        if (mid.equals("0016")) {
//            // imei转换成字符串
//            String imei = "";
//            for (int i = 0; i < 15; i++) {
//                imei = imei + binaryData[i];
//            }
//            imei = dataFomat.hexStr2Str(imei);
//            object.put("IMEI", imei);
//        } else if (mid.equals("0017")) {
//        if (mid.equals("0017")) {
//            object.put("模组固件版本", binaryData[0]);
//        } else if (mid.equals("1000")) {
//        if (mid.equals("1000")) {
//            if (hexstr.equals("07")) {
//                //事件记录条数 转成10进制
//                int n = Integer.parseInt(binaryData[0], 16);
//                object.put("读时间段事件记录条数", binaryData[0]);
//                //事件记录数据
//                String count = "";
//                for (int i = 1; i < binaryData.length; i++) {
//                    count += binaryData[i];
//                }
//                if (n > 0) {
//                    for (int i = 0; i < n; i++) {
//                        //根据事件代码，获取事件名称  eventType
//                        String[] s1 = count.substring(i * 18, (i + 1) * 18).split("");
//                        object.put("读时间段事件类型" + i, eventType((s1[0] + s1[1] + s1[2] + s1[3])));
//                        object.put("读时间段事件时间" + i, (s1[4] + s1[5] + s1[6] + s1[7] + s1[8]
//                                + s1[9] + s1[10] + s1[11] + s1[12] + s1[13] + s1[14] + s1[15]));
//                        object.put("读时间段事件详情" + i, s1[16] + s1[17]);
//                    }
//                } else {
//                    //根据事件代码，获取事件名称
//                    object.put("读时间段事件记录数据", count);
//                }
//            }
//        } else if ("1001".equals(mid)) {
        if ("1001".equals(mid)) {
            //读最新事件记录条数 转成10进制
            if ("07".equals(hexstr)) {
                //事件记录条数 转成10进制
                int n = Integer.parseInt(binaryData[0], 16);
                object.put("读最新事件记录条数", binaryData[0]);
                //事件记录数据
                String count = "";
                for (int i = 1; i < binaryData.length; i++) {
                    count += binaryData[i];
                }
                if (n > 0) {
                    for (int i = 0; i < n; i++) {
                        //根据事件代码，获取事件名称  eventType
                        String[] s1 = count.substring(i * 18, (i + 1) * 18).split("");
                        object.put("读最新事件类型" + i, eventType((s1[0] + s1[1] + s1[2] + s1[3])));
                        object.put("读最新事件时间" + i, (s1[4] + s1[5] + s1[6] + s1[7] + s1[8]
                                + s1[9] + s1[10] + s1[11] + s1[12] + s1[13] + s1[14] + s1[15]));
                        object.put("读最新事件详情" + i, s1[16] + s1[17]);
                    }
                } else {
                    //根据事件代码，获取事件名称
                    object.put("读最新事件记录数据", count);
                }
            }
        } else if (mid.equals("1004")) {
            if (hexstr.equals("07")) {
                //解密  获取明文
                String body = "";
                int length = b.length;
                int cd;
                if (length % 16 == 0) {
                    cd = length / 16;
                } else {
                    cd = length / 16 + 1;
                }
                for (int i = 0; i < cd; i++) {
                    // 创建一个长度是16的数组,用于存放每一段数组
                    byte[] newTxet = new byte[16];
                    for (int j = 0; j < 16; j++) {
                        if ((i * 16 + j) <= length - 1) {
                            newTxet[j] = b[i * 16 + j];
                        } else {
                            newTxet[j] = 0;
                        }
                    }
                    String originalString = dataFomat.bytes2HexString(aesUtil.decryptAES(newTxet, keyvalue));
                    body = body + originalString;
                }
                // 去掉body中的空格
                body = body.replaceAll(" ", "");

                // 将获取的数据域转换成byte[]
                byte[] b2 = dataFomat.toBytes(body);

                // 将byte转换成string
                String[] binaryData3 = new String[b2.length];
                for (int i = 0; i < binaryData3.length; i++) {
                    binaryData3[i] = dataFomat.toHex(b2[i]);
                }

                int daynum = Integer.parseInt(binaryData3[3], 16);

                String bindata = "";
                for (int i = 4; i < binaryData3.length; i++) {
                    bindata += binaryData3[i];
                }
                object.put("读日用气记录起始日期", binaryData3[0] + binaryData3[1] + binaryData3[2]);
                object.put("读日用气记录天数", binaryData3[3]);
                JSONArray array = new JSONArray();
                for (int i = 0; i < daynum; i++) {
                    JSONObject dataobject = new JSONObject();
                    String s1 = bindata.substring(i * 8, (i + 1) * 8);
                    long n1 = Long.parseLong(s1, 16);
                    double d1 = Double.valueOf(n1 * 1.0) / 1000;
                    dataobject.put("读日用气累计量", String.format("%.3f", d1));
                    array.add(dataobject);
                }
                object.put("数据域", array);
            }
        } else if (mid.equals("2001")) {
            object.put("嵌软版本", binaryData[0] + binaryData[1]);
        } else if (mid.equals("2002")) {
            object.put("表型号", binaryData[0] + binaryData[1]);
        } else if (mid.equals("2003")) {
            object.put("表号长度", Integer.parseInt(binaryData[0], 16));
            String devSerial = "";
            for (int i = 1; i < 33; i++) {
                devSerial = devSerial + binaryData[i];
            }
            devSerial = dataFomat.hexStr2Str(devSerial);
            // 第10位表示表号参数的长度
            if (devSerial.length() < 32) {
                for (int j = 0; j < 32; j++) {
                    devSerial += "0";
                }
            }
            object.put("表号内容", devSerial.replaceAll("\\u0000", "0"));
        } else if (mid.equals("2004")) {
            object.put("带阀门", binaryData[0]);
        } else if (mid.equals("2005")) {
            object.put("通信模式", binaryData[0]);
        } else if (mid.equals("2006")) {
            if (hexstr.equals("04")) {
                object.put("定时上传天数/月份区分位", binaryData[0]);
                object.put("定时上传周期值", binaryData[1]);
                object.put("定时上传时间/时", binaryData[2]);
                object.put("定时上传时间/分", binaryData[3]);
            } else if (hexstr.equals("05")) {
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("2007")) {
            //java.lang.NumberFormatException: For input string: "3138332e3233302e34302e3339202020"
            if (hexstr.equals("04")) {
                String ip = "";
                for (int i = 0; i < 16; i++) {
                    ip += binaryData[i];
                }
                byte[] ipbytes = dataFomat.toBytes(ip);
                String IP = new String(ipbytes);
                IP = IP.replaceAll(" ", "");
                IP = IP.replaceAll("\\u0000", "");
                object.put("采集服务参数", IP);
//                    String port = dataFomat.hexStr2Str(binaryData[16] + binaryData[17]);
                int port2 = Integer.valueOf(binaryData[16] + binaryData[17], 16);
                object.put("采集服务器端口", port2);
            } else if (hexstr.equals("05")) {
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("2009")) {
            String errornum = errorcode(binaryData[0] + binaryData[1]);
            object.put("错误码", errornum);

            //根据IMEI 查询出设备编号
            DevRegInfo devRegInfo = devRegInfoService.selectByImei(imeikey);
            String devserial = devRegInfo.getDevserial();

            //根据设备编号 devserial 查询出 最新的3001 上报信息，获取里面的密钥版本号
            DevDataLog devDataLog = devDataLogService.selectDataByDevserialAndDid(devserial, "3001");
            String j = JSONObject.fromObject(devDataLog.getData()).getString("数据域");
            String keyname = JSONObject.fromObject(j).getString("密钥版本号");

            String errorcode = binaryData[0] + binaryData[1];
            //判断返回的2009 错误码是否是00，00表示下发的2009修改密钥已成功
            if (!"0000".equals(errorcode)) {
                //修改密钥失败，删除非默认的最新修改的版本
                int delnewSecretKey = devSecretKeyService.delNewSecretKey(imeikey, keyname);
                //System.out.println(delnewSecretKey);
            }
        } else if (mid.equals("200a") || mid.equals("200A")) {
            String SIM = "";
            for (int i = 0; i < 20; i++) {
                SIM += binaryData[i];
            }
            String SIMascii = dataFomat.convertHexToString(SIM);
            object.put("SIM卡信息", SIMascii);
        } else if (mid.equals("200e") || mid.equals("200E")) {
            if (hexstr.equals("04")) {
                object.put("错峰间隔时间", binaryData[0] + binaryData[1]);
            } else if (hexstr.equals("05")) {
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("200f") || mid.equals("200F")) {
            if (hexstr.equals("04")) {
                object.put("多天不用气关阀控制", binaryData[0]);
            } else if (hexstr.equals("05")) {
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("2010")) {
            if (hexstr.equals("04")) {
                object.put("多天不上传关阀控制", binaryData[0]);
            } else if (hexstr.equals("05")) {
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("2011")) {
            if (hexstr.equals("04")) {
                object.put("过流报警使能", binaryData[0]);
            } else if (hexstr.equals("05")) {
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("2012")) {
            if (hexstr.equals("04")) {
                //APN
                String APN = "";
                for (int i = 0; i < 32; i++) {
                    if ("00".equals(binaryData[i])) {
                        break;
                    } else {
                        APN += binaryData[i];
                    }
                }
                String APNascii = dataFomat.convertHexToString(APN);
                object.put("APN", APNascii.toUpperCase());
            } else if (hexstr.equals("05")) {
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("2020")) {
            if (hexstr.equals("04")) {
                object.put("液晶显示", binaryData[0]);
            } else if (hexstr.equals("05")) {
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("0007")) {
            //备电电量百分比
            object.put("备电电量百分比", binaryData[0]);
        } else if (mid.equals("0008")) {
            //预留量
            //判断是读数据--->上行(04)
            if (hexstr.equals("04")) {
                if (content.equals("00000000")) {
                    object.put("预留量", content);
                } else {
                    String bindata = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3];
                    long ct = Long.parseLong(bindata, 16);
                    Double evs = Double.valueOf(ct * 1.0 / 1000);
                    object.put("预留量", String.format("%.3f", evs));
                }
                //判断是写数据--->上行(05)
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("0009")) {
            //剩余气量
            //判断是读数据--->上行(04)
            if (hexstr.equals("04")) {
                if (content.equals("00000000")) {
                    object.put("剩余气量", content);
                } else {
                    String bindata = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3];
                    long ct = Long.parseLong(bindata, 16);
                    Double evs = Double.valueOf(ct * 1.0 / 1000);
                    object.put("剩余气量", String.format("%.3f", evs));
                }
                //判断是写数据--->上行(05)
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("000a") || mid.equals("000A")) {
            //透支状态
            if (hexstr.equals("04")) {
                object.put("透支状态", binaryData[0]);
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if ("000c".equals(mid) || "000C".equals(mid)) {
            String suijima = "";
            for (int i = 0; i < 16; i++) {
                suijima += binaryData[i];
            }
            object.put("通信随机码", suijima);
        } else if ("000d".equals(mid) || "000D".equals(mid)) {
            //单价
            if (hexstr.equals("04")) {
                if (content.equals("00000000")) {
                    object.put("单价", content);
                } else {
                    String bindata = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3];
                    long ct = Long.parseLong(bindata, 16);
                    Double evs = Double.valueOf(ct * 1.0 / 10000);
                    object.put("单价", String.format("%.4f", evs));
                }
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("000f") || mid.equals("000F")) {
            if (hexstr.equals("04")) {
                if (content.equals("00000000")) {
                    object.put("剩余金额", content);
                } else {
                    String RA = binaryData[0] + binaryData[1] + binaryData[2] + binaryData[3];
                    int ra = Integer.valueOf(RA, 16);
                    Double evs = Double.valueOf(ra * 1.0 / 100);
                    object.put("剩余金额", String.format("%.2f", evs));
                }
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("0100")) {
            object.put("厂商自定义表状态", content);
        } else if (mid.equals("0101")) {
            if (hexstr.equals("04")) {
                String[] binarydata3 = decryptAESstr(b, keyvalue);
                String bindata = binarydata3[0] + binarydata3[1] + binarydata3[2] + binarydata3[3];
                long n = Long.parseLong(bindata, 16);
                Double evs = Double.valueOf(n * 1.0 / 1000);
                object.put("当前工况累积量", String.format("%.3f", evs));
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("0102")) {
            if (hexstr.equals("04")) {
                String[] binarydata3 = decryptAESstr(b, keyvalue);
                String bindata = binarydata3[0] + binarydata3[1] + binarydata3[2] + binarydata3[3];
                long n = Long.parseLong(bindata, 16);
                Double evs = Double.valueOf(n * 1.0 / 1000);
                object.put("当前标况累积量", String.format("%.3f", evs));
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("0103")) {
            if (hexstr.equals("04")) {
                String[] binarydata3 = decryptAESstr(b, keyvalue);
                String bindata = binarydata3[0] + binarydata3[1];
                long n = Long.parseLong(bindata, 16);
                Double evs = Double.valueOf(n * 1.0 / 10);
                object.put("当前声速", String.format("%.1f", evs));
            }
        } else if (mid.equals("0104")) {
            if (hexstr.equals("04")) {
                String[] binarydata3 = decryptAESstr(b, keyvalue);
                String bindata = binarydata3[0] + binarydata3[1];
                long n = Long.parseLong(bindata, 16);
                Double evs = Double.valueOf(n * 1.0 / 100);
                object.put("当前温度", String.format("%.2f", evs));
            }
        } else if (mid.equals("0105")) {
            if (hexstr.equals("04")) {
                String[] binarydata3 = decryptAESstr(b, keyvalue);
                String bindata = binarydata3[0] + binarydata3[1] + binarydata3[2] + binarydata3[3];
                long n = Long.parseLong(bindata, 16);
                Double evs = Double.valueOf(n * 1.0 / 1000);
                object.put("当前压力", String.format("%.3f", evs));
            }
        } else if ("1002".equals(mid)) {
            if ("07".equals(hexstr)) {
                //解密  获取明文
                String body = "";
                int length = b.length;
                int cd;
                if (length % 16 == 0) {
                    cd = length / 16;
                } else {
                    cd = length / 16 + 1;
                }
                for (int i = 0; i < cd; i++) {
                    // 创建一个长度是16的数组,用于存放每一段数组
                    byte[] newTxet = new byte[16];
                    for (int j = 0; j < 16; j++) {
                        if ((i * 16 + j) <= length - 1) {
                            newTxet[j] = b[i * 16 + j];
                        } else {
                            newTxet[j] = 0;
                        }
                    }
                    String originalString = dataFomat.bytes2HexString(aesUtil.decryptAES(newTxet, keyvalue));
                    body = body + originalString;
                }
                // 去掉body中的空格
                body = body.replaceAll(" ", "");

                // 将获取的数据域转换成byte[]
                byte[] b2 = dataFomat.toBytes(body);

                // 将byte转换成string
                String[] binaryData3 = new String[b2.length];
                for (int i = 0; i < binaryData3.length; i++) {
                    binaryData3[i] = dataFomat.toHex(b2[i]);
                }

                int daynum = Integer.parseInt(binaryData3[3], 16);

                String bindata = "";
                for (int i = 4; i < binaryData3.length; i++) {
                    bindata += binaryData3[i];
                }
                JSONArray array = new JSONArray();
                for (int i = 0; i < daynum; i++) {
                    JSONObject dataobject = new JSONObject();
                    dataobject.put("读每小时用气记录日期", binaryData3[0] + binaryData3[1] + binaryData3[2]);
                    dataobject.put("读每小时用气记录天数", binaryData3[3]);
                    array.add(dataobject);
                    String s1 = bindata.substring(i * 192, (i + 1) * 192);
                    String[] s2 = s1.split("");
                    int m = 1;
                    for (int j = 0; j < 192; j = j + 8) {
                        JSONObject db = new JSONObject();
                        long n1 = Long.parseLong(s2[j] + s2[j + 1] + s2[j + 2] + s2[j + 3] + s2[j + 4] + s2[j + 5] + s2[j + 6] + s2[j + 7], 16);
                        double d1 = Double.valueOf(n1 * 1.0) / 1000;
//                            double d1 = Double.valueOf(n1 * 1.0 /1000);
                        db.put("第" + m + "小时用气量", String.format("%.3f", d1));
                        m++;
                        array.add(db);
                    }
                }
                object.put("数据域", array);
            }
        } else if (mid.equals("1006")) {
            if (hexstr.equals("07")) {
                //解密  获取明文
                String body = "";
                int length = b.length;
                int cd;
                if (length % 16 == 0) {
                    cd = length / 16;
                } else {
                    cd = length / 16 + 1;
                }
                for (int i = 0; i < cd; i++) {
                    // 创建一个长度是16的数组,用于存放每一段数组
                    byte[] newTxet = new byte[16];
                    for (int j = 0; j < 16; j++) {
                        if ((i * 16 + j) <= length - 1) {
                            newTxet[j] = b[i * 16 + j];
                        } else {
                            newTxet[j] = 0;
                        }
                    }
                    String originalString = dataFomat.bytes2HexString(aesUtil.decryptAES(newTxet, keyvalue));
                    body = body + originalString;
                }
                // 去掉body中的空格
                body = body.replaceAll(" ", "");

                // 将获取的数据域转换成byte[]
                byte[] b2 = dataFomat.toBytes(body);

                // 将byte转换成string
                String[] binaryData3 = new String[b2.length];
                for (int i = 0; i < binaryData3.length; i++) {
                    binaryData3[i] = dataFomat.toHex(b2[i]);
                }

                int year = Integer.valueOf(binaryData3[0]);
                object.put("读月用气记录年份", year);
                String bindata = "";
                for (int i = 1; i < binaryData3.length; i++) {
                    bindata += binaryData3[i];
                }
                JSONArray array = new JSONArray();
                int m = 1;
                for (int i = 0; i < 12; i++) {
                    JSONObject dataobject = new JSONObject();
                    String[] s1 = bindata.substring(i * 8, (i + 1) * 8).split("");
                    long n1 = Long.parseLong(s1[0] + s1[1] + s1[2] + s1[3] + s1[4] + s1[5] + s1[6] + s1[7], 16);
                    double d1 = Double.valueOf(n1 * 1.0) / 1000;
                    dataobject.put("第" + m + "个月用气累计量", String.format("%.3f", d1));
                    array.add(dataobject);
                    m++;
                }
                object.put("数据域", array);
            }
        } else if (mid.equals("2000")) {
            object.put("厂商ID", binaryData[0] + binaryData[1]);
        } else if (mid.equals("2008")) {
            if (binaryData[0].equals("00")) {
                object.put("结算方式", binaryData[0]);
            } else {
                object.put("结算方式", binaryData[0]);
            }
        } else if (mid.equals("200b") || mid.equals("200B")) {
            if (binaryData[0].equals("00")) {
                object.put("运营商信息", "电信");
            } else if (binaryData[0].equals("01")) {
                object.put("运营商信息", "移动");
            } else if (binaryData[0].equals("02")) {
                object.put("运营商信息", "联通");
            }
        } else if (mid.equals("200c") || mid.equals("200C")) {
            object.put("应用版本协议", binaryData[0] + binaryData[1]);
        } else if (mid.equals("200d") || mid.equals("200D")) {
            if (binaryData[0].equals("00")) {
                object.put("供电类型", "碱电");
            } else {
                object.put("供电类型", "锂电");
            }
        } else if (mid.equals("2021")) {
            if (hexstr.equals("04")) {
                if (binaryData[0].equals("00")) {
                    object.put("面罩防拆", "禁止");
                } else {
                    object.put("面罩防拆", "使能");
                }
            }
        } else if (mid.equals("2022")) {
            if (hexstr.equals("04")) {
                if (binaryData[0].equals("00")) {
                    object.put("外部泄露报警使能", "禁止");
                } else {
                    object.put("部泄露报警使能", "使能");
                }
            }
        } else if (mid.equals("2023")) {
            if (hexstr.equals("04")) {
                if (binaryData[0].equals("00")) {
                    object.put("过流报警使能", "禁止");
                } else {
                    object.put("过流报警使能", "使能");
                }
                int bindata = Integer.valueOf(binaryData[0] + binaryData[1], 16);
                double bd = bindata / 100 * 1.0;
                object.put("过流门限", bd);
            }
        } else if (mid.equals("2100")) {
            if (hexstr.equals("04")) {
                //获取明文
                int daynum = Integer.parseInt(binaryData[0], 16);
                object.put("每日上传时间次数", daynum);

                String bindata = "";
                for (int i = 1; i < binaryData.length; i++) {
                    bindata += binaryData[i];
                }
                int m = 1;
                JSONArray array = new JSONArray();
                for (int i = 0; i < daynum; i++) {
                    JSONObject dataobject = new JSONObject();
                    String[] s1 = bindata.substring(i * 4, (i + 1) * 4).split("");
                    dataobject.put("上传时" + m, s1[0] + s1[1]);
                    dataobject.put("上传分" + m, s1[2] + s1[3]);
                    array.add(dataobject);
                    m++;
                }
                object.put("数据域", array);
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("2101")) {
            if (hexstr.equals("04")) {
                object.put("启动表端预结算", binaryData[0]);
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("2102")) {
            if (hexstr.equals("04")) {
                object.put("启动单价隐藏", binaryData[0]);
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        } else if (mid.equals("2103")) {
            if (hexstr.equals("04")) {
                object.put("启动余量/余额隐藏", binaryData[0]);
            } else if (hexstr.equals("05")) {
                //错误码
                String errornum = errorcode(binaryData[0] + binaryData[1]);
                object.put("错误码", errornum);
            }
        }
        return resultObject.toString();
    }


    /**
     * 事件代码
     * 传入事件代码 返回事件名称
     *
     * @param num
     * @return
     */
    public String eventType(String num) {
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

    /**
     * 错误码
     *
     * @param code
     * @return
     */
    public String errorcode(String code) {
        JSONObject jsonobject = new JSONObject();
        //数据对象ID不正确
        if (code.equals("0001")) {
            //验证数据对象ID 空着
            jsonobject.put("数据对象ID不正确", code);
        } else if (code.equals("0002")) {
            //验证日期非法 空着
            jsonobject.put("日期非法", code);
        } else if (code.equals("0003")) {
            //验证协议类型 空着
            jsonobject.put("协议类型不支持", code);
        } else if (code.equals("0004")) {
            //
            jsonobject.put("协议框架版本不支持", code);
        } else if (code.equals("0005")) {
            //
            jsonobject.put("MAC认证错误", code);
        } else if (code.equals("0006")) {
            //
            jsonobject.put("应用协议版本不支持", code);
        } else if (code.equals("0007")) {
            //
            jsonobject.put("写参数值非法", code);
        } else if (code.equals("0008")) {
            //
            jsonobject.put("标号非法", code);
        } else if (code.equals("0000")) {
            jsonobject.put("无错误", code);
        }

        return jsonobject.toString();
    }

    /**
     * 缓存命令2009
     */
    public String writedata2009(String did, String data, String reRead, String random, String keyvalue, String devserial, String IMEI) throws Exception {
        AESUtil aesutil = new AESUtil();
        DataFomat dataFomat = new DataFomat();
        String D = DataBody2009(did, data, random, keyvalue, devserial, IMEI);
        // 对于下行写数据指令，没有数据域，帧长度是12
        // 对于下行写数据指令，
        String head = "68";
        String T = "00";
        String V = "01";
        String L = String.format("%04x", dataFomat.toBytes(D).length + 12).toUpperCase();
        String MID = "BB";

        // 控制域，对于下行数据，默认无后续帧,不回读，指令帧执行结果是0，读数据功能码：0100，拼接成10000101，转换成十六进制 85
        String C = "";
        if (reRead.equals("0")) {
            C = "85";
        } else {
            //读数据功能码：1000，拼接成10001000，转换成十六进制 85
            C = "88";
        }
        String DID = did;

        // 计算校验码
        String CRC = AESUtil.crc(MID + C + DID + D);
        // 帧尾
        String tall = "16";
        String cmd = head + T + V + L + MID + C + DID + D + CRC + tall;
        return cmd;
    }

    /**
     * 2009data参数加密
     */
    public String DataBody2009(String did, String data, String random, String keyvalue, String devserial, String IMEI) throws Exception {
        // 定义数据域字符串
        String databody = "";
        String result = "";
        String newresult = "";
        AESUtil aesUtil = new AESUtil();
        // 2009H 写秘钥参数
        JSONObject resultObj = JSONObject.fromObject(data);

        // 密钥长度 2位
        String keylength = resultObj.getString("keylength");
        int n = Integer.valueOf(keylength);
        String newkey = "";
        if (n < 10) {
            newkey = "0" + String.valueOf(n);
        } else {
            newkey = Integer.toHexString(Integer.valueOf(keylength));
            if (newkey.length() < 2) {
                newkey = "0" + newkey;
            }
        }
        // 密钥版本 2位
        String keyVER = resultObj.getString("keyVER");
        int n2 = Integer.valueOf(keyVER);
        String newkeyVER = "";
        if (n2 < 10) {
            newkeyVER = "0" + String.valueOf(n2);
        } else {
            newkeyVER = Integer.toHexString(Integer.valueOf(keyVER));
            if (newkeyVER.length() < 2) {
                newkeyVER = "0" + newkeyVER;
            }
        }

        //获取密钥参数
        String hex = resultObj.getString("HEX");

        DevSecretKey ds = new DevSecretKey();
        ds.setImei(IMEI);
        ds.setKeyname(newkeyVER);
        ds.setKeylength(newkey);
        ds.setKeyvalue(hex);
        ds.setDefaultversion("1");
        //ds.setDevtype("GC");
        ds.setUsekeyname("1");
        ds.setDevserial(devserial);
        //将新密钥插入数据库中，默认为不启用状态
        int insertsecret = devSecretKeyService.insertnewsecret(ds);
        //同时启用初始版本密钥
//        int enableOldSecretkey = devSecretKeyService.updateoldusekeynameopen(IMEI,devserial);
        //最后将 非初始版本密钥 停用   (keyname!=00 && keyname!=#{newkeyVER})
//        int disableUndefaultSecretKey = devSecretKeyService.disableUndefaultSecretKey(IMEI);

//        System.out.println(insertsecret);
//        System.out.println(enableOldSecretkey);
//        System.out.println(disableUndefaultSecretKey);

        //新密钥 32位 不足32位的后面用0补齐
        if (hex.length() == 64) {
            result = newkey + newkeyVER + hex;
        } else {
            for (int i = 0; i < 64; i++) {
                if (i >= hex.length()) {
                    databody += "0";
                }
            }
            result = newkey + newkeyVER + hex + databody;
        }

        //使用初始化密钥进行加密
        newresult = aesUtil.encryptAESAndMAC(result, random, keyvalue);
        return newresult;
    }


    /**
     * 对3001的回复
     *
     * @param @param  data
     * @param @param  suijima
     * @param @return
     * @param @throws Exception    参数
     * @return String    返回类型
     * @throws
     * @Title: response3001
     * @author yuyan
     * @date 2019年11月18日
     */
    public String response3001(String data, String suijima, String mid, String keyvalue) throws Exception {
        Date date = new Date();
        DataFomat dataFomat = new DataFomat();
//        AESUtil aesutil = new AESUtil();
        //data 数据域    对于3001的回复数据，数据域是2字节错误码加上6字节时钟 ，错误码暂时默认0000无错误
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        //错误码 加 日期  数据对象内容
        String data2 = data + simpleDateFormat.format(date);

        // 对数据域进行MAC认证
        MAC mac2 = new MAC();
        String da = suijima + data2;
        byte[] b1 = dataFomat.toBytes(da);
        String maca = mac2.HMACSHA256(b1, suijima, keyvalue);
        String D = data2 + maca;
        String head = "68";
        String T = "00";
        String V = "01";
        String L = String.format("%04x", dataFomat.toBytes(D).length + 12).toUpperCase();

        //取设备上报上来的消息序号
        String MID = mid;

        // 控制域，对于下行数据，默认无后续帧，指令帧执行结果是0，读数据功能码：0100，拼接成10000100，转换成十六进制 81
        String C = "81";
        String DID = "3001";

        String CRC = AESUtil.crc(MID + C + DID + D);
        String tall = "16";
        String cmd = head + T + V + L + MID + C + DID + D + CRC + tall;
//        System.out.println("cmd" + cmd);
        return cmd;
    }

    /**
     * 解密 转成 16 进制字符串
     */
    public String[] decryptAESstr(byte[] b, String keyvalue) {
        DataFomat dataFomat = new DataFomat();
        AESUtil aesUtil = new AESUtil();
        String body = "";
        int length = b.length;
        int cd;
        if (length % 16 == 0) {
            cd = length / 16;
        } else {
            cd = length / 16 + 1;
        }
        for (int i = 0; i < cd; i++) {
            // 创建一个长度是16的数组,用于存放每一段数组
            byte[] newTxet = new byte[16];
            for (int j = 0; j < 16; j++) {
                if ((i * 16 + j) <= length - 1) {
                    newTxet[j] = b[i * 16 + j];
                } else {
                    newTxet[j] = 0;
                }
            }
            String originalString = dataFomat.bytes2HexString(aesUtil.decryptAES(newTxet, keyvalue));
            body = body + originalString;
        }
        // 去掉body中的空格
        body = body.replaceAll(" ", "");
        // 将获取的数据域转换成byte[]
        byte[] b2 = dataFomat.toBytes(body);

        // 将byte转换成string
        String[] binaryData3 = new String[b2.length];
        for (int i = 0; i < b2.length; i++) {
            binaryData3[i] = dataFomat.toHex(b2[i]);
        }
        return binaryData3;
    }

}
