package com.weeg.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.weeg.bean.*;
import com.weeg.configurer.ThreadConfig;
import com.weeg.callbackHandler.CallbackFactory;
import com.weeg.callbackHandler.CallBackHandler;
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
                        LOG.info("IMEI号："+imei);
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


    // 对数据域进行解析
    public String date(String content, String mid, String ori, String imeikey) {
        JSONObject object = new JSONObject();
        DataFomat dataFomat = new DataFomat();
        String keyvalue = null;

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
        CallBackHandler strategy = CallbackFactory.getInvokeStrategy(mid);
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
        }else if ("1001".equals(mid)){
            resultObject = strategy.upload1001Handler(object,mid,binaryData,hexstr);
        }else if ("1004".equals(mid)){
            resultObject = strategy.upload1004Handler(object,mid,b,binaryData,hexstr,keyvalue);
        }else if ("2001".equals(mid)){
            resultObject = strategy.upload2001Handler(object,mid,binaryData);
        }else if ("2002".equals(mid)){
            resultObject = strategy.upload2002Handler(object,mid,binaryData);
        }else if ("2003".equals(mid)){
            resultObject = strategy.upload2003Handler(object,mid,binaryData);
        }else if ("2004".equals(mid)){
            resultObject = strategy.upload2004Handler(object,mid,binaryData);
        }else if ("2005".equals(mid)){
            resultObject = strategy.upload2005Handler(object,mid,binaryData);
        }else if ("2006".equals(mid)){
            resultObject = strategy.upload2006Handler(object,mid,binaryData,hexstr);
        }else if ("2007".equals(mid)){
            resultObject = strategy.upload2007Handler(object,mid,binaryData,hexstr);
        }else if ("2009".equals(mid)){
            resultObject = strategy.upload2009Handler(object,mid,binaryData,imeikey,devRegInfoService,devDataLogService,devSecretKeyService);
        }else if ("200a".equals(mid)){
            resultObject = strategy.upload200aHandler(object,mid,binaryData);
        }else if ("200e".equals(mid)){
            resultObject = strategy.upload200eHandler(object,mid,binaryData,hexstr);
        }else if ("200f".equals(mid)){
            resultObject = strategy.upload200fHandler(object,mid,binaryData,hexstr);
        }else if ("2010".equals(mid)){
            resultObject = strategy.upload2010Handler(object,mid,binaryData,hexstr);
        }else if ("2011".equals(mid)){
            resultObject = strategy.upload2011Handler(object,mid,binaryData,hexstr);
        }else if ("2012".equals(mid)){
            resultObject = strategy.upload2012Handler(object,mid,binaryData,hexstr);
        }else if ("2020".equals(mid)){
            resultObject = strategy.upload2020Handler(object,mid,binaryData,hexstr);
        }else if ("0007".equals(mid)){
            resultObject = strategy.upload0007Handler(object,mid,binaryData);
        }else if ("0008".equals(mid)){
            resultObject = strategy.upload0008Handler(object,mid,binaryData,hexstr,content);
        }else if ("0009".equals(mid)){
            resultObject = strategy.upload0009Handler(object,mid,binaryData,hexstr,content);
        }else if ("000a".equals(mid)){
            resultObject = strategy.upload000aHandler(object,mid,binaryData,hexstr);
        }else if ("000c".equals(mid)){
            resultObject = strategy.upload000cHandler(object,mid,binaryData);
        }else if ("000d".equals(mid)){
            resultObject = strategy.upload000dHandler(object,mid,binaryData,hexstr,content);
        }else if ("000f".equals(mid)){
            resultObject = strategy.upload000fHandler(object,mid,binaryData,hexstr,content);
        }else if ("0100".equals(mid)) {
            object.put("厂商自定义表状态", content);
        }else if ("0101".equals(mid)){
            resultObject = strategy.upload0101Handler(object,mid,binaryData,hexstr,b,keyvalue);
        }else if ("0102".equals(mid)){
            resultObject = strategy.upload0102Handler(object,mid,binaryData,hexstr,b,keyvalue);
        }else if ("0103".equals(mid)){
            resultObject = strategy.upload0103Handler(object,mid,binaryData,hexstr,b,keyvalue);
        }else if ("0104".equals(mid)){
            resultObject = strategy.upload0104Handler(object,mid,binaryData,hexstr,b,keyvalue);
        }else if ("0105".equals(mid)){
            resultObject = strategy.upload0105Handler(object,mid,binaryData,hexstr,b,keyvalue);
        }else if ("1002".equals(mid)){
            resultObject = strategy.upload1002Handler(object,mid,binaryData,hexstr,b,keyvalue);
        }else if ("1006".equals(mid)){
            resultObject = strategy.upload1006Handler(object,mid,binaryData,hexstr,b,keyvalue);
        }else if ("2000".equals(mid)) {
            object.put("厂商ID", binaryData[0] + binaryData[1]);
        }else if ("2008".equals(mid)){
            resultObject = strategy.upload2008Handler(object,mid,binaryData);
        }else if ("200b".equals(mid)){
            resultObject = strategy.upload200BHandler(object,mid,binaryData);
        }else if ("200c".equals(mid) || "200C".equals(mid)) {
            object.put("应用版本协议", binaryData[0] + binaryData[1]);
        }else if ("200d".equals(mid) || "200D".equals(mid)) {
            if (binaryData[0].equals("00")) {
                object.put("供电类型", "碱电");
            } else {
                object.put("供电类型", "锂电");
            }
        }else if (mid.equals("2021")) {
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
        }else if ("2100".equals(mid)){
            resultObject = strategy.upload2100Handler(object,mid,binaryData,hexstr);
        }else if (mid.equals("2101")) {
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



}
