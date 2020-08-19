package com.weeg.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.weeg.bean.*;
import com.weeg.configurer.ErrorEnmus;
import com.weeg.service.*;
import com.weeg.util.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by SJ on 2020/7/15
 * 响应命令
 */
@RestController
public class ResponseController extends CoreController{
//    static Post post = new Post();
    //读取配置文件
    Props props = PropsUtil.get("properties/data.properties");
    //配置日志
    private static final Logger LOG = LoggerFactory.getLogger(ResponseController.class);


    /**
     *  下发3002命令
     * @throws Exception
     */
    public Object response3002(DevSecretKeyService devSecretKeyService,DevDataLogService devDataLogService,
                               IotPushRecvReponseService iotPushRecvReponseService,DevControlCmdService devControlCmdService,
                               DevRegInfoService devRegInfoService,IotImeiStatusService iotimeistatusService,String devserial)
            throws Exception {
        DataFomat dataFomat = new DataFomat();
        AESUtil aesutil = new AESUtil();
        //设备号
//        String devserial = JSONObject.fromObject(data).getString("serial");
        //下发命令
//        String did = JSONObject.fromObject(data).getString("did");

//        JSONObject EndTestDatas = JSONObject.fromObject(data);
//        JSONObject etDatas = EndTestDatas.getJSONObject("data");


        //剩余气量
//        String ResVol = etDatas.getString("ResVol");
//        String ResVoldatabody = "";
//        //有符号整数，转成10进制字符串，扩大1000倍
//        double m1 = Double.parseDouble(ResVol)*1000;
//        long k1 = (long) m1;
//        String n1 = Long.toHexString(k1);
//        String bo = "";
//        if(n1.length() == 8){
//            ResVoldatabody = n1;
//        }else {
//            for(int i=0; i<8; i++){
//                if(i >= n1.length()){
//                    bo += "0";
//                }
//            }
//            ResVoldatabody = bo + n1;
//        }
//
//        //透支状态
//        String OverStatus = etDatas.getString("OverStatus");
//        String OverS = "";
//        if ("非透支".equals(OverStatus)){
//            OverS = "00";
//        }else if ("透支".equals(OverStatus)){
//            OverS = "01";
//        }
//
//        //余量状态
//        String MarStatus = etDatas.getString("MarStatus");
//        String MarS = "";
//        if("余量正常".equals(MarStatus)){
//            MarS = "00";
//        }else if("余量不足".equals(MarStatus)){
//            MarS = "01";
//        }
//
//        //单价
//        String UnitPrice = etDatas.getString("UnitPrice");
//        String UnitPricedatabody = "";
//        //有符号整数，转成10进制字符串，扩大10000倍
//        double m2 = Double.parseDouble(UnitPrice)*10000;
//        long k2 = (long)m2;
//        String n2 = Long.toHexString(k2);
//        String bo2 = "";
//        if(n2.length() == 8){
//            UnitPricedatabody = n2;
//        }else {
//            for(int i=0; i<8; i++){
//                if(i >= n2.length()){
//                    bo2 += "0";
//                }
//            }
//            UnitPricedatabody = bo2 + n2;
//        }
//
//        //剩余金额
//        String balance = etDatas.getString("balance");
//        String balancedatabody = "";
//        //有符号整数，转成10进制字符串，扩大1000倍
//        double m3 = Double.parseDouble(balance)*100;
//        int k3 = (int) m3;
//        String n3= Integer.toHexString(k3);
//        String bo3 = "";
//        if(n3.length() == 8){
//            balancedatabody = n3;
//        }else {
//            for(int i=0; i<8; i++){
//                if(i >= n3.length()){
//                    bo3 += "0";
//                }
//            }
//            balancedatabody = bo3 + n3;
//        }


        //根据设备序列号，查询设备状态是否在线
        IotImeiStatus iotimeistatus = iotimeistatusService.selectBySerial(devserial);
        String status = iotimeistatus.getStatus();

        //如果设备在线，才能下发3002
        if("1".equals(status)){
            //根据设备序列号，获取设备对应信息
            DevRegInfo devRegInfo = devRegInfoService.selectByDevSerial(devserial);
            String IMEI = devRegInfo.getImei();
            //先查出最新的3001请求的classID，再根据classID查出3001请求的数据域
            IotPushRecvReponse iprr = iotPushRecvReponseService.selectClassid(devRegInfo.getDevserial(),"3001");
            String resclassid = iprr.getClassid();
            DevDataLog resdataLog = devDataLogService.selectByChildclassId(resclassid);
            String datas = resdataLog.getData();
            String mid = JSONObject.fromObject(datas).getString("消息序号");
            String datastr = JSONObject.fromObject(datas).getString("数据域");
            String random = JSONObject.fromObject(datastr).getString("通信随机码");
            //获取密钥版本号
            String keyname = JSONObject.fromObject(datastr).getString("密钥版本号");
            //根据密钥版本号、设备编号、IMEI号获取对应的密钥keyvalue
            DevSecretKey devSecretKey = devSecretKeyService.selectkeyvalue(keyname,devserial,IMEI);
            String keyvalue = devSecretKey.getKeyvalue();

            //data 包含: code错误码2位  date时钟6位  ResVol剩余气量4位   OverS透支状态1位  MarS余量状态1位   UnitPrice单价4位   balance剩余金额4位
            // 先转成byte[]数组  判断长度是否大于16

            //默认全写0 start
            //data 包含: code错误码2位  date时钟6位  ResV剩余气量4位   OS透支状态1位  MS余量状态1位   UP单价4位   RA剩余金额4位     先转成byte[]数组  判断长度是否大于16
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
            String date = simpleDateFormat.format(new Date());
            String code = "0000";
            String ResV = "00000000";
            String OS = "00";
            String MS = "00";
            String UP = "00000000";
            String RA = "00000000";
            String Data = code + date + ResV + OS + MS + UP + RA;
            //默认全写0 end

            //数据加密认证
            String newresult = MAC.encryptAESAndMAC(Data, random,keyvalue);

            String head = "68";
            String T = "67";
            String V = "0E";
            String L = String.format("%04x", dataFomat.toBytes(newresult).length + 12).toUpperCase();

            //取设备上报上来的消息序号
            String MID = mid;
            // 控制域
            String C = "82";
            String DID = "3002";
            String CRC = aesutil.crc(MID + C + DID + newresult);
            String tall = "16";
            String cmd = head + T + V + L + MID + C + DID + newresult + CRC + tall;
//            LOG.info("cmd:"+cmd);


            //获取data中platformcode对应的平台信息
            String value = props.getStr(devRegInfo.getPlatformcode());

            //拼接下发平台命令需要的信息
            JSONObject params = new JSONObject();
            params.put("NBId", devRegInfo.getIotserial());
            params.put("imei", devRegInfo.getImei());
            params.put("cmds", cmd);
            params.put("operator", value);
            // 向平台下发命令
//            String postUrl = props.getStr(devRegInfo.getPlatformcode().substring(0, 1))+ "postDeviceCmdTou";
//            String resultobj = HttpUtil.post(postUrl, params.toString());
            String result = SendToOneNetController.postDeviceCmdTou(params.toString());
            if (JSONObject.fromObject(result).getString("errno").equals("0")) {
                //命令下发成功  将数据原文添加进数据库  devControlCmd.setcmdFlag(1)  1表示命令下发， 存入数据库
                DevControlCmd devControlCmd=new DevControlCmd();
                //生成唯一识别码
                String classid=UUID.randomUUID().toString();
                devControlCmd.setClassid(classid);
                devControlCmd.setDevserial(devserial);
                devControlCmd.setIotserial(devRegInfo.getIotserial());
                devControlCmd.setDevtype(devRegInfo.getDevtype());
                devControlCmd.setOpttype(devRegInfo.getPlatformcode());
                devControlCmd.setDid(DID);
                devControlCmd.setCtrlvalue(cmd);
                devControlCmd.setCtrltype(random);
                devControlCmd.setCtrltime(new Date());
                //命令下发 cmdflag = 1
                devControlCmd.setCmdFlag("1");
                //未下发命令条数 默认0
                int cmdno = 0;
                devControlCmd.setCmdNo(cmdno);
                devControlCmd.setCtrltime1(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                devControlCmdService.insert(devControlCmd);

                LOG.info("End>>>3002命令下发成功:"+result);
                // 命令下发成功
                return new Ok("2101","命令下发成功");
            } else {
                //JSONObject.fromObject(result).getString("error") 平台返回的错误
                LOG.info("3002命令下发失败:"+JSONObject.fromObject(result).getString("error"));
                return fail(ErrorEnmus.ERROR_10003.getCode(), JSONObject.fromObject(result).getString("error"));
            }
        }else{
            //拼接下发命令 将未下发的命令存入数据库  设备不在线，命令已存入数据库
            return fail(ErrorEnmus.ERROR_10002.getCode(), ErrorEnmus.ERROR_10002.getMessage());
        }
    }


}
