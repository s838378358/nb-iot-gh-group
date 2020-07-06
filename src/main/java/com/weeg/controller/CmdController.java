package com.weeg.controller;
import com.weeg.bean.*;
import com.weeg.service.*;
import com.weeg.util.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName: CmdController
 * @author yuyan
 * @date 2019年11月13日
 */
@RestController
@RequestMapping("weeg")
public class CmdController {
	static Post post = new Post();
//	String path1 = FileUtil.file("properties/data.properties").toString();
//	String path1 = "/properties/data.properties";
	@Autowired
    DevRegInfoService devRegInfoService;
	@Autowired
    DevControlCmdService devControlCmdService;
	@Autowired
    IotImeiStatusService iotimeistatusService;
	@Autowired
    IotPushRecvReponseService iotPushRecvReponseService;
	@Autowired
    DevDataLogService devDataLogService;
	@Autowired
    DevSecretKeyService devSecretKeyService;

	@RequestMapping(value = "/cmd")
	public void cmd(@RequestBody String body,HttpServletResponse response,HttpServletRequest request) throws Exception {
		Date startTime = new Date();
		JSONObject returnObj=new JSONObject();
		int cmdno;
		JSONObject getBody = JSONObject.fromObject(body);
		String serial = getBody.getString("serial");
		String cmdType = getBody.getString("cmdType");
		String did = getBody.getString("did");
		String data = getBody.getString("data");
		String reRead = getBody.getString("reRead");
		
		//根据设备序列号，获取设备对应信息
		DevRegInfo devRegInfo = devRegInfoService.selectByDevSerial(serial);
		String IMEI = devRegInfo.getImei();
		if(devRegInfo==null) {
			returnObj.put("result", false);
			returnObj.put("code", "");
			returnObj.put("data", "");
			returnObj.put("msg", serial+"号未查到对应的信息");
		}else {
			// 读取配置文件，这样才可以得到当前设备对应的平台信息
			String path = request.getSession().getServletContext().getRealPath("/");
			String path1 = (new File(path)).getParent();
			//获取data中platformcode对应的平台信息
			ReadPropertise readPropertise = new ReadPropertise();
			String value = readPropertise.readpro(path1, devRegInfo.getPlatformcode());

			//获取设备序列号 查询设备是否在线
			String devserial = devRegInfo.getDevserial();
			//根据设备序列号，查询设备状态是否在线
			IotImeiStatus iotimeistatus = iotimeistatusService.selectBySerial(devserial);
			String status = iotimeistatus.getStatus();
			
			if("1".equals(status)) {
                //如果设备在线 ，直接下发命令

                String random = "";
                String keyvalue = "";


                //查询出随机码 和 密钥信息
				IotPushRecvReponse iprr = iotPushRecvReponseService.selectClassid(devRegInfo.getDevserial(), "3001");
				String resclassid = iprr.getClassid();
				DevDataLog resdataLog = devDataLogService.selectByChildclassId(resclassid);
				String datas = resdataLog.getData();
				String datastr = JSONObject.fromObject(datas).getString("数据域");
				//获取通讯随机码
				random = JSONObject.fromObject(datastr).getString("通信随机码");
				//获取密钥版本号
				String keyname = JSONObject.fromObject(datastr).getString("密钥版本号");
				//根据密钥版本号、设备编号、IMEI号获取对应的密钥keyvalue
				DevSecretKey devSecretKey = devSecretKeyService.selectkeyvalue(keyname, devserial, IMEI);
				keyvalue = devSecretKey.getKeyvalue();


				//对下行的命令进行拼接
				String cmd="";
				if(cmdType.equals("RData")) {
					//读状态数据
					cmd = cmdReadBody(did);
				}else if(cmdType.equals("WData")){
					//写状态数据
					cmd = cmdWriteBody(did,data,reRead,random,keyvalue,devserial,IMEI);
				}else if(cmdType.equals("Record")){
					//读记录数据
					cmd = cmdRecodeBody(did,data,random,keyvalue,devserial,IMEI);
				}

				//拼接下发平台命令需要的信息
				JSONObject params = new JSONObject();
				params.put("NBId", devRegInfo.getIotserial());
				params.put("imei", devRegInfo.getImei());
				params.put("cmds", cmd);
				params.put("operator", value);
				
				// 向平台下发命令
				String postUrl = readPropertise.readpro(path1, devRegInfo.getPlatformcode().substring(0, 1))+ "postDeviceCmdTou";
				String parasmstr = params.toString();
				String result = post.post(postUrl, params.toString());
				
				if (JSONObject.fromObject(result).getString("errno").equals("0")) {
					//将下发成功的命令存入数据库 cmdflag = 1  devControlCmd.setcmdFlag(1)  1表示命令下发， 存入数据库
					DevControlCmd devControlCmd = new DevControlCmd();
					//生成唯一识别码
					String classid=UUID.randomUUID().toString(); 
					devControlCmd.setClassid(classid);
					devControlCmd.setDevserial(serial);
					devControlCmd.setIotserial(devRegInfo.getIotserial());
					devControlCmd.setDevtype(devRegInfo.getDevtype());
					devControlCmd.setOpttype(devRegInfo.getPlatformcode());
					devControlCmd.setDid(did);
					devControlCmd.setCtrlvalue(cmd);
					devControlCmd.setCtrltime(startTime);
					devControlCmd.setCtrltype(random);
					//命令下发 cmdflag = 1
					devControlCmd.setCmdFlag("1");
					//未下发命令条数 默认0
					cmdno = 0;
					devControlCmd.setCmdNo(cmdno);
					devControlCmd.setCtrltime1(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
					devControlCmdService.insert(devControlCmd);
					// 命令下发成功
					returnObj.put("result", true);
					returnObj.put("code", "");
					returnObj.put("data", "");
					returnObj.put("msg", "命令下发成功");
				} else {
					returnObj.put("result", false);
					returnObj.put("code", "");
					returnObj.put("data", "");
					returnObj.put("msg", JSONObject.fromObject(result).getString("error"));
				}
				
			}else {
				//如果设备不在线，将命令存入数据库（缓存命令） devControlCmd.setcmdFlag(0)   0表示命令未下发，存入数据库      cmdno 表示未下发的命令条数
				//获取该设备未下发命令的条数
				List<DevControlCmd> dctc = devControlCmdService.selectBySerialandcmdFlag(devserial,"0");
				if(dctc==null || dctc.isEmpty()) {
					cmdno = 1;
				}else {
					cmdno = Integer.valueOf(dctc.size()) + 1;
				}
				//将未下发命令数据原文添加进数据库  devControlCmd.setcmdFlag(1)  1表示命令下发， 存入数据库
				DevControlCmd devControlCmd = new DevControlCmd();
				//生成唯一识别码
				String classid=UUID.randomUUID().toString(); 
				devControlCmd.setClassid(classid);
				devControlCmd.setDevserial(serial);
				devControlCmd.setIotserial(devRegInfo.getIotserial());
				devControlCmd.setDevtype(devRegInfo.getDevtype());
				devControlCmd.setOpttype(devRegInfo.getPlatformcode());
				devControlCmd.setDid(did);
				devControlCmd.setCtrltime(startTime);
				//命令下发 cmdflag = 0   0表示未下发
				devControlCmd.setCmdFlag("0");
				//未下发命令条数
				devControlCmd.setCmdNo(cmdno);
				devControlCmd.setCtrltime1(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
				//将缓存命令存入数据库
				devControlCmd.setCmdCache(getBody.toString());
				devControlCmd.setCmdType(cmdType);
				devControlCmd.setCmdData(data);
				devControlCmd.setReRead(reRead);
				devControlCmdService.insert(devControlCmd);
				
				//拼接下发命令 将未下发的命令存入数据库
				returnObj.put("result", false);
				returnObj.put("code", "");
				returnObj.put("data", "");
				returnObj.put("msg", "设备不在线，命令已存入数据库");
			}
		}
		
		response.setHeader("content-type", "text/html;charset=UTF-8");// 设置响应头部,设置主体的编码格式是UTF-8
		response.setCharacterEncoding("UTF-8");// 设置传输的编码格式
		Writer writer = response.getWriter();
		writer.write(returnObj.toString());// 将 字符串内容写入缓存
		writer.flush();// 将缓存输出
		writer.close();
	}

	/**
	 * 2009写密钥命令，单独一个cmd
	 */
	@RequestMapping(value = "cmd2009")
	public void cmd2009(@RequestBody String body, HttpServletResponse response, HttpServletRequest request) throws Exception {
		JSONObject getBody = JSONObject.fromObject(body);
		Date startTime = new Date();
		String serial = getBody.getString("serial");
		String cmdType = getBody.getString("cmdType");
		String did = getBody.getString("did");
		String data = getBody.getString("data");
		String reRead = getBody.getString("reRead");
		int cmdno;

		JSONObject returnObj=new JSONObject();
		//根据设备序列号，获取设备对应信息
		DevRegInfo devRegInfo = devRegInfoService.selectByDevSerial(serial);
		String IMEI = devRegInfo.getImei();
		if(devRegInfo==null) {
			returnObj.put("result", false);
			returnObj.put("code", "");
			returnObj.put("data", "");
			returnObj.put("msg", serial+"号未查到对应的信息");
		}else {
			// 读取配置文件，这样才可以得到当前设备对应的平台信息
			String path = request.getSession().getServletContext().getRealPath("/");
			String path1 = (new File(path)).getParent();
			//获取data中platformcode对应的平台信息
			ReadPropertise readPropertise = new ReadPropertise();
			String value = readPropertise.readpro(path1, devRegInfo.getPlatformcode());

			//获取设备序列号 查询设备是否在线
			String devserial = devRegInfo.getDevserial();
			//根据设备序列号，查询设备状态是否在线
			IotImeiStatus iotimeistatus = iotimeistatusService.selectBySerial(devserial);
			String status = iotimeistatus.getStatus();

			//判断设备是否在线
			if("1".equals(status)) {
				//如果设备在线 ，直接下发命令
				//先查出最新的3001请求的classID，再根据classID查出3001请求的数据域
				IotPushRecvReponse iprr = iotPushRecvReponseService.selectClassid(devRegInfo.getDevserial(),"3001");
				String resclassid = iprr.getClassid();
				DevDataLog resdataLog = devDataLogService.selectByChildclassId(resclassid);
				String datas = resdataLog.getData();
				String datastr = JSONObject.fromObject(datas).getString("数据域");
				//获取通讯随机码
				String random = JSONObject.fromObject(datastr).getString("通信随机码");
				//获取密钥版本号
				String keyname = JSONObject.fromObject(datastr).getString("密钥版本号");
				//根据密钥版本号、设备编号、IMEI号获取对应的密钥keyvalue
				DevSecretKey devSecretKey = devSecretKeyService.selectkeyvalue(keyname,devserial,IMEI);
				String keyvalue = devSecretKey.getKeyvalue();


				//判断是否是写 2009 修改密钥命令 如果是，按顺序下发 000E：00 关户，  2009修改密钥， 000E：01 开户 三条命令
				if(did.equals("2009")){
					//先下发一条 开户状态 000E 00 关户命令
					String cmd000E = cmdWriteBody("000E","0","0",random,keyvalue,devserial,IMEI);
					JSONObject params000Eclose = new JSONObject();
					params000Eclose.put("NBId", devRegInfo.getIotserial());
					params000Eclose.put("imei", devRegInfo.getImei());
					params000Eclose.put("cmds", cmd000E);
					params000Eclose.put("operator", value);
					// 向平台下发命令
					String postUrl2 = readPropertise.readpro(path1, devRegInfo.getPlatformcode().substring(0, 1))+ "postDeviceCmdTou";
					String result2 = post.post(postUrl2, params000Eclose.toString());
					if (JSONObject.fromObject(result2).getString("errno").equals("0")) {
						//将下发成功的命令存入数据库 cmdflag = 1  devControlCmd.setcmdFlag(1)  1表示命令下发， 存入数据库
						DevControlCmd devControlCmd2 = new DevControlCmd();
						//生成唯一识别码
						String classid2=UUID.randomUUID().toString();
						devControlCmd2.setClassid(classid2);
						devControlCmd2.setDevserial(serial);
						devControlCmd2.setIotserial(devRegInfo.getIotserial());
						devControlCmd2.setDevtype(devRegInfo.getDevtype());
						devControlCmd2.setOpttype(devRegInfo.getPlatformcode());
						devControlCmd2.setDid("000E");
						devControlCmd2.setCtrlvalue(cmd000E);
						devControlCmd2.setCtrltime(startTime);
						devControlCmd2.setCtrltype(random);
						//命令下发 cmdflag = 1
						devControlCmd2.setCmdFlag("1");
						//未下发命令条数 默认0
						cmdno = 0;
						devControlCmd2.setCmdNo(cmdno);
						devControlCmd2.setCtrltime1(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
						int cmd2 = devControlCmdService.insert(devControlCmd2);
						System.out.println("cmd2:"+cmd2);

						//当下发 000E命令成功后，继续下发 2009 命令。
						String cmd2009 = cmdWriteBody(did,data,reRead,random,keyvalue,devserial,IMEI);
						JSONObject params2009 = new JSONObject();
						params2009.put("NBId", devRegInfo.getIotserial());
						params2009.put("imei", devRegInfo.getImei());
						params2009.put("cmds", cmd2009);
						params2009.put("operator", value);
						// 向平台下发命令
						String postUrl3 = readPropertise.readpro(path1, devRegInfo.getPlatformcode().substring(0, 1))+ "postDeviceCmdTou";
						String result3 = post.post(postUrl3, params2009.toString());
						if (JSONObject.fromObject(result3).getString("errno").equals("0")) {
							//将下发成功的命令存入数据库 cmdflag = 1  devControlCmd.setcmdFlag(1)  1表示命令下发， 存入数据库
							DevControlCmd devControlCmd3 = new DevControlCmd();
							//生成唯一识别码
							String classid3=UUID.randomUUID().toString();
							devControlCmd3.setClassid(classid3);
							devControlCmd3.setDevserial(serial);
							devControlCmd3.setIotserial(devRegInfo.getIotserial());
							devControlCmd3.setDevtype(devRegInfo.getDevtype());
							devControlCmd3.setOpttype(devRegInfo.getPlatformcode());
							devControlCmd3.setDid(did);
							devControlCmd3.setCtrlvalue(cmd2009);
							devControlCmd3.setCtrltime(startTime);
							devControlCmd3.setCtrltype(random);
							//命令下发 cmdflag = 1
							devControlCmd3.setCmdFlag("1");
							//未下发命令条数 默认0
							cmdno = 0;
							devControlCmd3.setCmdNo(cmdno);
							devControlCmd3.setCtrltime1(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
							int cmd3 = devControlCmdService.insert(devControlCmd3);
							System.out.println("cmd3:" + cmd3);

							//当下发2009成功以后，最后下发 000E 01 开户
							String cmd000Eopen = cmdWriteBody("000E","1","0",random,keyvalue,devserial,IMEI);
							JSONObject params000E2 = new JSONObject();
							params000E2.put("NBId", devRegInfo.getIotserial());
							params000E2.put("imei", devRegInfo.getImei());
							params000E2.put("cmds", cmd000Eopen);
							params000E2.put("operator", value);
							// 向平台下发命令
							String postUrl4 = readPropertise.readpro(path1, devRegInfo.getPlatformcode().substring(0, 1))+ "postDeviceCmdTou";
							String result4 = post.post(postUrl4, params000E2.toString());

							if(JSONObject.fromObject(result4).getString("errno").equals("0")){
								//开户01 命令下发成功 存入数据库
								DevControlCmd devControlCmd4 = new DevControlCmd();
								//生成唯一识别码
								String classid4=UUID.randomUUID().toString();
								devControlCmd4.setClassid(classid4);
								devControlCmd4.setDevserial(serial);
								devControlCmd4.setIotserial(devRegInfo.getIotserial());
								devControlCmd4.setDevtype(devRegInfo.getDevtype());
								devControlCmd4.setOpttype(devRegInfo.getPlatformcode());
								devControlCmd4.setDid(did);
								devControlCmd4.setCtrlvalue(cmd2009);
								devControlCmd4.setCtrltime(startTime);
								devControlCmd4.setCtrltype(random);
								//命令下发 cmdflag = 1
								devControlCmd4.setCmdFlag("1");
								//未下发命令条数 默认0
								cmdno = 0;
								devControlCmd4.setCmdNo(cmdno);
								devControlCmd4.setCtrltime1(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
								int cmd4 = devControlCmdService.insert(devControlCmd4);
								System.out.println("cmd3:" + cmd4);

								//所有命令下发成功，return
								returnObj.put("result", true);
								returnObj.put("code", "");
								returnObj.put("data", "");
								returnObj.put("msg", "命令下发成功");
							}else{
								returnObj.put("result", false);
								returnObj.put("code", "");
								returnObj.put("data", "");
								returnObj.put("msg", JSONObject.fromObject(result2).getString("error"));
							}
						}else{
							returnObj.put("result", false);
							returnObj.put("code", "");
							returnObj.put("data", "");
							returnObj.put("msg", JSONObject.fromObject(result2).getString("error"));
						}
					} else {
						returnObj.put("result", false);
						returnObj.put("code", "");
						returnObj.put("data", "");
						returnObj.put("msg", JSONObject.fromObject(result2).getString("error"));
					}

				}
			}else {
				//如果设备不在线，将命令存入数据库（缓存命令） devControlCmd.setcmdFlag(0)   0表示命令未下发，存入数据库      cmdno 表示未下发的命令条数
				//获取该设备未下发命令的条数
				List<DevControlCmd> dctc = devControlCmdService.selectBySerialandcmdFlag(devserial,"0");
				if(dctc==null || dctc.isEmpty()) {
					cmdno = 1;
				}else {
					cmdno = Integer.valueOf(dctc.size()) + 1;
				}
				//将未下发命令数据原文添加进数据库  devControlCmd.setcmdFlag(1)  1表示命令下发， 存入数据库
				DevControlCmd devControlCmd = new DevControlCmd();
				//生成唯一识别码
				String classid=UUID.randomUUID().toString();
				devControlCmd.setClassid(classid);
				devControlCmd.setDevserial(serial);
				devControlCmd.setIotserial(devRegInfo.getIotserial());
				devControlCmd.setDevtype(devRegInfo.getDevtype());
				devControlCmd.setOpttype(devRegInfo.getPlatformcode());
				devControlCmd.setDid(did);
				devControlCmd.setCtrltime(startTime);
				//命令下发 cmdflag = 0   0表示未下发
				devControlCmd.setCmdFlag("0");
				//未下发命令条数
				devControlCmd.setCmdNo(cmdno);
				devControlCmd.setCtrltime1(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
				//将缓存命令存入数据库
				devControlCmd.setCmdCache(getBody.toString());
				devControlCmd.setCmdType(cmdType);
				devControlCmd.setCmdData(data);
				devControlCmd.setReRead(reRead);
				devControlCmdService.insert(devControlCmd);

				//拼接下发命令 将未下发的命令存入数据库
				returnObj.put("result", false);
				returnObj.put("code", "");
				returnObj.put("data", "");
				returnObj.put("msg", "设备不在线，命令已存入数据库");
			}
		}
		response.setHeader("content-type", "text/html;charset=UTF-8");// 设置响应头部,设置主体的编码格式是UTF-8
		response.setCharacterEncoding("UTF-8");// 设置传输的编码格式
		Writer writer = response.getWriter();
		writer.write(returnObj.toString());// 将 字符串内容写入缓存
		writer.flush();// 将缓存输出
		writer.close();
	}

	/**
	 *  下发3002
	 * @param data     数据域
	 * @throws Exception
	 */
	@RequestMapping(value = "response3002")
	public void response3002(@RequestBody String data, HttpServletResponse response,HttpServletRequest request) throws Exception {
		JSONObject returnObj=new JSONObject();
		DataFomat dataFomat = new DataFomat();
		AESUtil aesutil = new AESUtil();
		//设备号
		String devserial = JSONObject.fromObject(data).getString("serial");
		//下发命令
		String did = JSONObject.fromObject(data).getString("did");

		JSONObject EndTestDatas = JSONObject.fromObject(data);
		JSONObject etDatas = EndTestDatas.getJSONObject("data");


		//剩余气量
		String ResVol = etDatas.getString("ResVol");
		String ResVoldatabody = "";
		//有符号整数，转成10进制字符串，扩大1000倍
		double m1 = Double.parseDouble(ResVol)*1000;
		long k1 = (long) m1;
		String n1 = Long.toHexString(k1);
		String bo = "";
		if(n1.length() == 8){
			ResVoldatabody = n1;
		}else {
			for(int i=0; i<8; i++){
				if(i >= n1.length()){
					bo += "0";
				}
			}
			ResVoldatabody = bo + n1;
		}

		//透支状态
		String OverStatus = etDatas.getString("OverStatus");
		String OverS = "";
		if ("非透支".equals(OverStatus)){
			OverS = "00";
		}else if ("透支".equals(OverStatus)){
			OverS = "01";
		}

		//余量状态
		String MarStatus = etDatas.getString("MarStatus");
		String MarS = "";
		if("余量正常".equals(MarStatus)){
			MarS = "00";
		}else if("余量不足".equals(MarStatus)){
			MarS = "01";
		}

		//单价
		String UnitPrice = etDatas.getString("UnitPrice");
		String UnitPricedatabody = "";
		//有符号整数，转成10进制字符串，扩大10000倍
		double m2 = Double.parseDouble(UnitPrice)*10000;
		long k2 = (long)m2;
		String n2 = Long.toHexString(k2);
		String bo2 = "";
		if(n2.length() == 8){
			UnitPricedatabody = n2;
		}else {
			for(int i=0; i<8; i++){
				if(i >= n2.length()){
					bo2 += "0";
				}
			}
			UnitPricedatabody = bo2 + n2;
		}

		//剩余金额
		String balance = etDatas.getString("balance");
		String balancedatabody = "";
		//有符号整数，转成10进制字符串，扩大1000倍
		double m3 = Double.parseDouble(balance)*100;
		int k3 = (int) m3;
		String n3= Integer.toHexString(k3);
		String bo3 = "";
		if(n3.length() == 8){
			balancedatabody = n3;
		}else {
			for(int i=0; i<8; i++){
				if(i >= n3.length()){
					bo3 += "0";
				}
			}
			balancedatabody = bo3 + n3;
		}


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
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
			String date = simpleDateFormat.format(new Date());
			String code = "0000";

			String Data = code + date + ResVoldatabody + OverS + MarS + UnitPricedatabody + balancedatabody;
			//对原始数据域加密加MAC result
			/**
			 * 加密
			 */
			MAC mac = new MAC();
			String body = "";
			// 16进制数字转成byte[]数组
//		byte[] dtbytes = dt.getBytes();
			byte[] bytes = dataFomat.toBytes(Data);
			// 对数据进行加密 先判断bytes[]数组长度大于16吗
			int length = bytes.length;
			int cd;
			if(length % 16 ==0){
				cd = length /16;
			}else{
				cd = length / 16 + 1;
			}
			for (int i = 0; i < cd; i++) {
				// 创建一个长度是16的数组,用于存放每一段数组
				byte[] newTxet = new byte[16];
				for (int j = 0; j < 16; j++) {
					if((i * 16 + j) <= length-1){
						newTxet[j] = bytes[i * 16 + j];
					}else{
						newTxet[j] = 0;
					}
				}
				String originalString = dataFomat.bytes2HexString(aesutil.encryptAES(newTxet,keyvalue));
				body = body + originalString;
			}
			// 去掉body中的空格
			body = body.replaceAll(" ", "");
			String encrystr = random + body;
			// 16进制数字转成byte[]数组
			byte[] b2 = dataFomat.toBytes(encrystr);
			// Mac认证
			String mactype = mac.HMACSHA256(b2,random,keyvalue);
			// 拼接成数据域 先将加密数据转成字符串， 再拼上MAC字符串
//			String jmysj = dataFomat.bytesToHexFun1(b2);
			String newresult = body + mactype;

//			String result = encryptAESAndMAC(Data, random);

			String head = "68";
			String T = "00";
			String V = "01";
			String L = String.format("%04x", dataFomat.toBytes(newresult).length + 12).toUpperCase();

			//取设备上报上来的消息序号
			String MID = mid;

			// 控制域
			String C = "82";
			String DID = "3002";

			String CRC = aesutil.crc(MID + C + DID + newresult);
			String tall = "16";
			String cmd = head + T + V + L + MID + C + DID + newresult + CRC + tall;
			System.out.println("cmd"+cmd);

			// 读取配置文件，这样才可以得到当前设备对应的平台信息
			String path = request.getSession().getServletContext().getRealPath("/");
			String path1 = (new File(path)).getParent();
			//获取data中platformcode对应的平台信息
			ReadPropertise readPropertise = new ReadPropertise();
			String value = readPropertise.readpro(path1, devRegInfo.getPlatformcode());
			//拼接下发平台命令需要的信息
			JSONObject params = new JSONObject();
			params.put("NBId", devRegInfo.getIotserial());
			params.put("imei", devRegInfo.getImei());
			params.put("cmds", cmd);
			params.put("operator", value);
			// 向平台下发命令
			String postUrl = readPropertise.readpro(path1, devRegInfo.getPlatformcode().substring(0, 1))+ "postDeviceCmdTou";
			String resultobj = post.post(postUrl, params.toString());

			if (JSONObject.fromObject(resultobj).getString("errno").equals("0")) {
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

				returnObj.put("result", true);
				returnObj.put("code", "");
				returnObj.put("data", "");
				returnObj.put("msg", "命令下发成功");
			} else {
				returnObj.put("result", false);
				returnObj.put("code", "");
				returnObj.put("data", "");
				returnObj.put("msg", JSONObject.fromObject(resultobj).getString("error"));
			}
		}else{
			returnObj.put("result", false);
			returnObj.put("code", "");
			returnObj.put("data", "");
			returnObj.put("msg","设备不在线，命令下发失败");
		}
		// 设置响应头部,设置主体的编码格式是UTF-8
		response.setHeader("content-type", "text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");// 设置传输的编码格式
		Writer writer = response.getWriter();
		System.out.println("结束调试:"+returnObj.toString());
		writer.write(returnObj.toString());// 将 字符串内容写入缓存
		writer.flush();// 将缓存输出
		writer.close();
	}

	/**
	 * 
	 * @Title: cmdBody
	 * @Description: 读数据命令
	 * @param @param request
	 * @param @param response 参数
	 * @return void 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	public String cmdReadBody(String did) {
		AESUtil aesutil = new AESUtil();
		// 对于下行读数据指令，没有数据域，帧长度是12
		String head = "68";
		String T = "00";
		String V = "01";
		String L = String.format("%04x", 12).toUpperCase();
		String MID = "BB";
		// 控制域，对于下行数据，默认无后续帧，指令帧执行结果是0，读数据功能码：0100，拼接成10000100，转换成十六进制 84
		String C = "84";
		String DID = did;
		String CRC = aesutil.crc(MID + C + DID);
		String tall = "16";
		String cmd = head + T + V + L + MID + C + DID + CRC + tall;
		return cmd;
	}

	/**
	 * 
	 * @Title: cmdBody
	 * @Description: 拼接写数据下行语句
	 * @param @param request
	 * @param @param response 参数
	 * @return void 返回类型
	 * @throws Exception
	 * @throws @author   yuyan
	 * @date 2019年11月13日
	 */
	public String cmdWriteBody(String did, String data,String reRead,String random,String keyvalue,String devserial,String IMEI) throws Exception {
		AESUtil aesutil = new AESUtil();
		DataFomat dataFomat = new DataFomat();
		String D = DataBody(did, data,random,keyvalue,devserial,IMEI);
		// 对于下行写数据指令，没有数据域，帧长度是12
		// 对于下行写数据指令，
		String head = "68";
		String T = "00";
		String V = "01";
		String L = String.format("%04x", dataFomat.toBytes(D).length + 12).toUpperCase();
		String MID = "BB";

		// 控制域，对于下行数据，默认无后续帧,不回读，指令帧执行结果是0，读数据功能码：0100，拼接成10000101，转换成十六进制 85
		String C="";
		if(reRead.equals("0")) {
			C = "85";
		}else {
			//读数据功能码：1000，拼接成10001000，转换成十六进制 85
			C = "88";
		}
		String DID = did;

		// 计算校验码
		String CRC = aesutil.crc(MID + C + DID + D);
		// 帧尾
		String tall = "16";
		String cmd = head + T + V + L + MID + C + DID + D + CRC + tall;
		return cmd;
	}
	/**
	 * 
	 * @Title: cmdBody
	 * @Description: 拼接读数据下行语句
	 * @param @param request
	 * @param @param response 参数
	 * @return void 返回类型
	 * @throws Exception
	 * @throws @author   yuyan
	 * @date 2019年11月13日
	 */
	public String cmdRecodeBody(String did, String data,String random,String keyvalue,String devserial,String IMEI) throws Exception {
		DataFomat dataFomat = new DataFomat();
		AESUtil aesutil = new AESUtil();
		String D = DataBody(did, data,random,keyvalue,devserial,IMEI);
		// 对于下行写数据指令，没有数据域，帧长度是12
		
		// 对于下行写数据指令，
		String head = "68";
		String T = "00";
		String V = "01";
		String L = String.format("%04x", dataFomat.toBytes(D).length + 12).toUpperCase();
		String MID = "BB";
		
		// 控制域，对于下行数据，默认无后续帧，指令帧执行结果是0，读数据功能码：0111，拼接成10000111，转换成十六进制 87
		String C = "87";
		String DID = did;
		
		// 计算校验码
		String CRC = aesutil.crc(MID + C + DID + D);
		// 帧尾
		String tall = "16";
		String cmd = head + T + V + L + MID + C + DID + D + CRC + tall;
		return cmd;
	}

	/**
	 * 
	 * @Title: dataBody
	 * @Description: 拼接数据域
	 * @param @param request
	 * @param @param response 参数
	 * @return void 返回类型
	 * @throws Exception
	 * @throws @author   yuyan
	 * @date 2019年11月13日
	 */
	public String DataBody(String did, String data,String random,String keyvalue,String devserial,String IMEI) throws Exception {
		DataFomat dataFomat = new DataFomat();
		// 定义数据域字符串
		String databody = "";
		String result = "";
		if (did.equals("0001")) {
			// 0001H写阀门状态，值为0/1/2
			// 开阀门
			if (data.equals("0")) {
				databody = "00";
			// 关阀门
			} else if (data.equals("1")) {
				databody = "01";
			// 关阀门并锁定
			} else if (data.equals("2")) {
				databody = "02";
			}
			result = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return result;
		} else if (did.equals("0002")) {
			// 0002H写 时钟 12位
			String datetime = data;
			String newresult = MAC.encryptAESAndMAC(datetime, random,keyvalue);
			return newresult;
		} else if ("0003".equals(did)){
			//无符号整数，转成10进制字符串，扩大1000倍
			double m = Double.parseDouble(data)*1000;
			int k = (int)m;
			String datastr = Integer.toHexString(k);
			//字符串少于8位补齐，补0
			databody = operationUtil.strFillzero(datastr,8);
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if("0008".equals(did)){
			// 0008H写 预留量 4位
			//有符号整数，转成10进制字符串，扩大1000倍
			double m = Double.parseDouble(data)*1000;
			int k = (int)m;
			String datastr = Integer.toHexString(k);
			//字符串少于8位补齐，补0
			databody = operationUtil.strFillzero(datastr,8);
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		}else if("0009".equals(did)) {
			//0009 剩余气量
			//有符号整数，转成10进制字符串，扩大1000倍
			double m = Double.parseDouble(data)*1000;
			long k = (long) m;
			String n = Long.toHexString(k);
			//补0
			databody = operationUtil.strFillzero(n,8);
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if("000A".equals(did)) {
			//000A 写透支状态
			if ("0".equals(data)) {
				databody = "00";
			} else if ("1".equals(data)) {
				databody = "01";
			}
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if("000D".equals(did)) {
			//000D 写单价
			//有符号整数，转成10进制字符串，扩大10000倍
			double m = Double.parseDouble(data)*10000;
			long k = (long)m;
			String n = Long.toHexString(k);
			//补0
			databody = operationUtil.strFillzero(n,8);
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if ("000E".equals(did)) {
			// 000E 写开户状态
			if (data.equals("0")) {
				databody = "00";
			} else if (data.equals("1")) {
				databody = "01";
			}
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if("000F".equals(did)) {
			//000D 写单价
			//有符号整数，转成10进制字符串，扩大1000倍
			double m = Double.parseDouble(data)*100;
			int k = (int)m;
			String n = Integer.toHexString(k);
			//补0
			databody = operationUtil.strFillzero(n,8);
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if ("0010".equals(did)) {
			// 0010H 写余量状态 传递1位数字
			if ("0".equals(data)) {
				//余量正常
				databody = "00";
			} else if ("1".equals(data)) {
				//余量不足
				databody = "01";
			}
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if (did.equals("1000")) {
			// 1000H 读时间段时间记录
			JSONObject resultObj = JSONObject.fromObject(data);
			// 事件代码 前4位事件时间代码
			String event = resultObj.getString("event");
			// 起始时间 12位
			String starttime = resultObj.getString("starttime");
			// 结束时间 12位
			String endtime = resultObj.getString("endtime");
			// 记录条数 2位
			String count = resultObj.getString("count");
			int n = Integer.valueOf(count);
            String newcount = "";
			if(n<10){
                count = String.valueOf(n);
                newcount = "0" + count;
			}else {
				newcount = Integer.toHexString(Integer.parseInt(count));
				if (newcount.length()<2){
					newcount = "0" + newcount;
				}
			}
			// 拼接事件记录
			result = event + starttime + endtime + newcount;
			// 无需加密直接return
			return result;
		} else if (did.equals("1001")) {
			// 1001H 读最新事件记录
			JSONObject resultObj = JSONObject.fromObject(data);
			// 事件代码
			String event = resultObj.getString("event");
			// 记录条数
			String count = resultObj.getString("count");
            int n = Integer.valueOf(count);
            String newcount = "";
            if(n<10){
                count = String.valueOf(n);
                newcount = "0" + count;
            }else {
//				newcount = count;
				newcount = Integer.toHexString(Integer.parseInt(count));
				if (newcount.length()<2){
					newcount = "0" + newcount;
				}
			}
			result = event + newcount;
			return result;
		} else if(did.equals("1002")) {
			// 1002H 读每小时用气日志
			JSONObject resultObj = JSONObject.fromObject(data);
			//起始日期
			String date = resultObj.getString("date");
			//天数
			String day = resultObj.getString("day");
            int n = Integer.valueOf(day);
            String newcount = "";
            if(n<10){
				day = String.valueOf(n);
                newcount = "0" + day;
            }else {
//				newcount = day;
				newcount = Integer.toHexString(Integer.parseInt(day));
				if (newcount.length()<2){
					newcount = "0" + newcount;
				}
			}
			result = date + newcount;
			return result;
		} else if (did.equals("1004")) {
			// 1004H 读日用气记录 传递8位数字
			JSONObject resultObj = JSONObject.fromObject(data);
			// 0-5位 起始年月日
			String date = resultObj.getString("date");
			// 6、7位 天数
			String day = resultObj.getString("day");
            int n = Integer.valueOf(day);
            String newcount = "";
            if(n<10){
                day = String.valueOf(n);
                newcount = "0" + day;
            }else {
//				newcount = day;
				newcount = Integer.toHexString(Integer.parseInt(day));
				if (newcount.length()<2){
					newcount = "0" + newcount;
				}
			}
			result = date + newcount;
			return result;
		} else if(did.equals("1006")) {
			//1006 读月用气记录   1位
			result = data;
			return result;
		} else if (did.equals("2006")) {
			// 2006H 写定时上传参数 8位数 天数00、 周期01、 时00、 分00
			JSONObject resultObj = JSONObject.fromObject(data);
			// 天数/月份 区分
			String days = resultObj.getString("days");
			// 周期值
			String cyc = resultObj.getString("cyc");
			int n = Integer.parseInt(cyc);
			String newcount;
			if(n<10){
				newcount = "0" + n;
			}else{
				newcount = Integer.toHexString(Integer.valueOf(cyc));
				if(newcount.length()<2){
					newcount = "0" + newcount;
				}
			}
			// 0表示按天发送，1表示按月发送
			// 上传 时 分
			String upload = resultObj.getString("upload");
			// 上传 分
			databody = days + newcount + upload;
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if (did.equals("2007")) {
			String newresult;
			// 2007H 写采集服务参数 36位
			JSONObject resultObj = JSONObject.fromObject(data);
			// 采集服务器地址
			String ip = resultObj.getString("ip");
			String[] ips = ip.split("");
			String ascIP = "";
			for(int i=0; i<ips.length; i++){
				//字符串转成ASCII码
				String n1 = dataFomat.stringToAscii(ips[i]);
				//ASCII码转成 16 进制字符串
				ascIP += Integer.toHexString(Integer.valueOf(n1));
			}
			// 端口号
			String port = resultObj.getString("port");
			// 端口号转成16进制
			port = Long.toHexString(Long.parseLong(port));
			//端口号补0
			port = operationUtil.strFillzero(port,4);
			//ip补0
			result = operationUtil.strFillzero(ascIP,32);
			newresult = MAC.encryptAESAndMAC2(result,port,random,keyvalue);
			return newresult;
		} else if (did.equals("2009")) {

			String newresult;
			// 2009H 写秘钥参数
			JSONObject resultObj = JSONObject.fromObject(data);

			// 密钥长度 2位
			String keylength = resultObj.getString("keylength");
			int n = Integer.parseInt(keylength);

			String newkey;
			if(n<10){
				newkey = "0" + n;
			}else{
				newkey = Integer.toHexString(Integer.valueOf(keylength));
				if(newkey.length()<2){
					newkey = "0" + newkey;
				}
			}
			// 密钥版本 2位
			String keyVER = resultObj.getString("keyVER");
			int n2 = Integer.parseInt(keyVER);
			String newkeyVER;
			if(n2<10){
				newkeyVER = "0" + n2;
			}else{
				newkeyVER = Integer.toHexString(Integer.valueOf(keyVER));
				if(newkeyVER.length()<2){
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
			ds.setUsekeyname("1");
			ds.setDevserial(devserial);
			//将新密钥插入数据库中，默认为不启用状态
			int insertsecret = devSecretKeyService.insertnewsecret(ds);
//			System.out.println(insertsecret);
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
			newresult = MAC.encryptAESAndMAC(result,random,keyvalue);
			return newresult;
		} else if (did.equals("200E")) {
			// 200EH 写错峰间隔时间 data范围15-43 4位
			int n2 = Integer.valueOf(data);
			String newkeyVER;
			//转成16进制 小于10 直接前面补0，大于10，用toHexString
			if(n2<10){
				newkeyVER = "000" + data;
			}else{
				newkeyVER = Integer.toHexString(Integer.valueOf(data));
			}
			//补0
			databody = operationUtil.strFillzero(newkeyVER,4);
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if (did.equals("200F")) {
			// 200FH 写多天不用气关阀控制参数 2位
			int n2 = Integer.valueOf(data);
			String newkeyVER;
			if(n2<10){
				newkeyVER = "0" + n2;
			}else{
				newkeyVER = Integer.toHexString(Integer.valueOf(data));
			}
			String bo = operationUtil.strFillzero(newkeyVER,2);
			String newresult = MAC.encryptAESAndMAC(bo, random,keyvalue);
			return newresult;
		} else if (did.equals("2010")) {
			// 2010H 写多天不上传关阀控制参数
			int n2 = Integer.valueOf(data);
			String newkeyVER;
			if(n2<10){
				newkeyVER = "0" + String.valueOf(n2);
			}else{
				newkeyVER = Integer.toHexString(Integer.valueOf(data));
			}
			String bo = operationUtil.strFillzero(newkeyVER,2);
			String newresult = MAC.encryptAESAndMAC(bo,random,keyvalue);
			return newresult;
		} else if(did.equals("2011")){
			//2011 过流报警使能
			int n2 = Integer.parseInt(data);
			String newkeyVER;
			if(n2<10){
				newkeyVER = "0" + n2;
			}else{
				newkeyVER = Integer.toHexString(Integer.parseInt(data));
			}
			String bo = operationUtil.strFillzero(newkeyVER,2);
			String newresult = MAC.encryptAESAndMAC(bo, random,keyvalue);
			return newresult;
		} else if(did.equals("2012")) {
			//2012H 写APN  32位
			String[] apn = data.split("");
			String ascIP = "";
			for(int i=0; i<apn.length; i++){
				//字符串转成ASCII码
				String n1 = dataFomat.stringToAscii(apn[i]);
				//ASCII码转成 16 进制字符串
				ascIP += Integer.toHexString(Integer.parseInt(n1));
			}
			result = operationUtil.strFillzero(ascIP,64);
			String newresult = MAC.encryptAESAndMAC(result, random,keyvalue);
			return newresult;
		}else if ("2020".equals(did)){
			// 2020 液晶显示 2位
			if ("0".equals(data)) {
				//余量正常
				databody = "00";
			} else if ("1".equals(data)) {
				//余量不足
				databody = "01";
			}
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		}else if ("2021".equals(did)){
			// 2021 面罩防拆使能 2位
			if ("0".equals(data)) {
				//余量正常
				databody = "00";
			} else if ("1".equals(data)) {
				//余量不足
				databody = "01";
			}
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		}else if ("2022".equals(did)){
			// 2022 外部泄露报警使能 2位
			if ("0".equals(data)) {
				//余量正常
				databody = "00";
			} else if ("1".equals(data)) {
				//余量不足
				databody = "01";
			}
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		}else if ("2100".equals(did)){
			//定时上传参数
			String newresult;
			JSONObject resultObj = JSONObject.fromObject(data);
			//前端做好限制，长度是几，time的长度就是 2(时)*2(分) * Num(次数)
			String num = resultObj.getString("num");
			if(num.equals("00")){
			    databody = num;
				newresult = MAC.encryptAESAndMAC(databody,random,keyvalue);
			}else{
				String time = resultObj.getString("time");
				databody = num + time;
				//加密
				newresult = MAC.encryptAESAndMAC(databody,random,keyvalue);
			}
			return newresult;
		}else if (did.equals("2101")) {
			// 2101H 启用表端预结算
			if (data.equals("0")) {
				databody = "00";
			} else if (data.equals("1")) {
				databody = "01";
			}
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if(did.equals("2102")) {
			//2102H 启用单价隐藏
			// 0：进制
			if (data.equals("0")) { 
				databody = "00";
			// 0： 使能
			} else if (data.equals("1")) { 
				databody = "01";
			}
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if(did.equals("2103")) {
			//2103H 启用余量/余额隐藏
			// 0：进制
			if (data.equals("0")) { 
				databody = "00";
			// 0： 使能
			} else if (data.equals("1")) { 
				databody = "01";
			}
			String newresult = MAC.encryptAESAndMAC(databody, random,keyvalue);
			return newresult;
		} else if(did.equals("2023")) {

			//2023H 写过流参数
			JSONObject resultObj = JSONObject.fromObject(data);
			String res = resultObj.getString("res");
			String par = resultObj.getString("par");
			if(res.equals("0")) {
				databody = "00";
			}else if(res.equals("1")) {
				databody = "01";
			}
			//无符号整数，转成10进制字符串，扩大100倍
			int data1 = Integer.valueOf(par) * 100;
			//10进制整数转成16进制字符串
			String data2 = dataFomat.stringToHex(String.valueOf(data1));
			String dt = databody + data2;
			String newresult = MAC.encryptAESAndMAC(dt, random,keyvalue);
			return newresult;
		}
		return result;
	}


}
