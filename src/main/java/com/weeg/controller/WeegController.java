package com.weeg.controller;

import com.weeg.bean.DevRegInfo;
import com.weeg.bean.DevSecretKey;
import com.weeg.bean.IotImeiStatus;
import com.weeg.model.ResponseData;
import com.weeg.service.DevRegInfoService;
import com.weeg.service.DevSecretKeyService;
import com.weeg.service.IotImeiStatusService;
import com.weeg.util.Post;
import com.weeg.util.ReadPropertise;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;

/**
 * NB相关操作(设备注册，设备注销，命令注销入口)
 * 
 * @ClassName: WeegController.java
 *
 * @author: yuyan
 * @date: 2019年11月5日 下午12:50:39
 */
@RestController
@RequestMapping("/NBWeegServer/weeg")
public class WeegController {
	static Post post = new Post();
	Logger logger = Logger.getLogger(WeegController.class);
	@Autowired
    DevRegInfoService devRegInfoService;
	@Autowired
    DevSecretKeyService devSecretKeyService;
	@Autowired
	IotImeiStatusService iotImeiStatusService;

	/**
	 * 页面上填写相关信息向后台提交，实现设备注册
	 * @throws Exception
	 */
	@RequestMapping(value = "/createDevice", method = RequestMethod.POST)
	public ResponseData registerDevice(@RequestBody String body,HttpServletRequest request) {
		// 读取配置文件，这样才可以得到当前设备对应的平台信息
		String path = request.getSession().getServletContext().getRealPath("/");
		String path1 = (new File(path)).getParent();
		ResponseData responseData = new ResponseData();
		JSONObject getBody = JSONObject.fromObject(body);
		String operatorInfo = getBody.getString("OperatorInfo");
		String imei = getBody.getString("imei");
		String imsi = getBody.getString("imsi");
		String serial = getBody.getString("serial");
		String deviceType = getBody.getString("deviceType");
		//String clientId = getBody.getString("clientId");
		//String phone = getBody.getString("phone");

		// 记录日志，操作人员在当前时间登录
		logger.debug(serial + "," + new Date() + "," + "根据imei:" + imei + ",imsi:" + imsi + "进行设备注册");

		// 根据设备序列号对设备进行查询有没有被注册
		DevRegInfo devRegInfo = devRegInfoService.selectByDevSerial(serial);

		// 查询得到的对象是空的
		if (devRegInfo == null) {
			ReadPropertise readPropertise = new ReadPropertise();
			// 根据传入的OperatorInfo，获得对应的value
			String value = readPropertise.readpro(path1, operatorInfo);

			// 拼接请求参数对象
			JSONObject params = new JSONObject();
			params.put("imei", imei);
			params.put("imsi", imsi);
			params.put("name", serial);
			params.put("serial", serial);
			params.put("operator", value);

			// 拼接平台注册地址
			String registUrl = readPropertise.readpro(path1, operatorInfo.substring(0, 1)) + "registDevice";

			// 向平台进行注册，获取注册结果
			String RegistResult = post.post(registUrl, params.toString());

			// 网络请求失败，得到fail提示
			if (RegistResult.equals("fail")) {
				// 记录日志
				logger.debug(serial + "," + new Date() + "," + "注册设备平台请求失败");

				//// 平台注册请求失败
				responseData.setResult(false);
				responseData.setErrorCode2("00002");
				responseData.setMessage("平台注册失败");
			} else {
				// 平台请求成功，获取平台返回的信息，判断平台返回信息，如果是true，进行写入数据库操作
				if (JSONObject.fromObject(RegistResult).getBoolean("result")) {
					JSONObject resultObj = JSONObject.fromObject(RegistResult);

					// 将信息添加到数据库
					// 获取NBID
					String iotserial = resultObj.getString("data");

					DevRegInfo deRegInfoNew = new DevRegInfo();
					deRegInfoNew.setPlatformcode(operatorInfo);
					//设备序列号
					deRegInfoNew.setDevserial(serial);
					//NBID
					deRegInfoNew.setIotserial(iotserial);
					deRegInfoNew.setDevtype(deviceType);
					deRegInfoNew.setImei(imei);
					deRegInfoNew.setImsi(imsi);
					deRegInfoNew.setRegstatus("0");
					deRegInfoNew.setRegtime(new Date());

					//插入设备信息
					int insertResult = devRegInfoService.insert(deRegInfoNew);

					//插入设备默认密钥
					DevSecretKey devSecretKey = new DevSecretKey();
					devSecretKey.setImei(imei);
					devSecretKey.setKeyname("00");
					devSecretKey.setKeylength("10");
					devSecretKey.setKeyvalue("21213141516171811222324252627282");
					devSecretKey.setDefaultversion("0");
					devSecretKey.setDevtype(deviceType);
					devSecretKey.setUsekeyname("0");
					devSecretKey.setDevserial(serial);
					int insertDefaultSecretKey = devSecretKeyService.insertnewsecret(devSecretKey);

					//添加设备状态，默认status为0 ，设备离线状态
					IotImeiStatus iotImeiStatus = new IotImeiStatus();
					iotImeiStatus.setStatus("0");
					iotImeiStatus.setIotserial(iotserial);
					iotImeiStatus.setDevserial(serial);
					iotImeiStatus.setImei(imei);
					int n = iotImeiStatusService.insert(iotImeiStatus);


					// 判断数据库操作结果
					if (insertResult == 1 && insertDefaultSecretKey==1 && n==1) {
						// 平台注册成功并且数据库操作成功，表示设备完成注册成功
						logger.debug(serial + "," + new Date() + "," + "设备向NB平台进行注册，设备平台注册成功，数据库操作成功");
						responseData.setResult(true);
						responseData.setErrorCode1("0000");
						responseData.setErrorCode2("00000");
						responseData.setData(resultObj.getString("data"));
						responseData.setMessage("平台注册成功，数据库设备注册成功，默认密钥写入成功");
					} else {
						responseData.setResult(false);
						responseData.setErrorCode1("0001");
						responseData.setErrorCode2("00001");
						responseData.setMessage("平台注册成功，数据库注册失败");
					}
				} else {
					// 平台发送请求成功，但是注册失败！
					responseData.setResult(false);
					responseData.setErrorCode1("0002");
					responseData.setErrorCode2("00002");
					responseData.setMessage("平台注册失败，数据库注册失败");
				}
			}
		} else {
			// 数据库查询该设备有对应的信息，不允许重复注册
			responseData.setResult(false);
			responseData.setErrorCode1("0001");
			responseData.setErrorCode2("00001");
			responseData.setMessage("该设备已注册");
		}
		return responseData;

	}

	/**
	 * 删除设备，根据传过来的设备序列号，获取到该设备对应的NBId,去对应的平台将设备删除
	 * 
	 * 在数据库中对该条数据只是进行数据的更新，将该设备的NBId删除
	 * <p>
	 * Title: deleteDevice
	 * </p>
	 * 
	 * @author yuyan
	 * @date 2019年3月1日
	 */
	@RequestMapping(value = "/removeDevice", method = RequestMethod.POST)
	public ResponseData deleteDevice(@RequestBody String body,HttpServletRequest request){
		// 读取配置文件，这样才可以得到当前设备对应的平台信息
		String path = request.getSession().getServletContext().getRealPath("/");
		String path1 = (new File(path)).getParent();
		ResponseData<Object> responseData = new ResponseData<>();
		JSONObject getBody = JSONObject.fromObject(body);
		String deviceSerial = getBody.getString("deviceSerial");

		// 记录日志，操作人员在当前时间登录
		logger.debug(deviceSerial + "," + new Date() + "," + "设备删除");

		// 创建返回对象
//		JSONObject returnObj = new JSONObject();

		// 首先根据相关号码对设备进行查询
		DevRegInfo devRegInfo = devRegInfoService.selectByDevSerial(deviceSerial);
		if (devRegInfo == null) {
			responseData.setResult(false);
			responseData.setErrorCode2("00001");
			responseData.setMessage("根据表具序列号：" + deviceSerial + "没有找到对应设备信息！");
		} else {
			ReadPropertise readPropertise = new ReadPropertise();

			// 根据传入的key，获得对应的value
			String value = readPropertise.readpro(path1, devRegInfo.getPlatformcode());

			String imei = devRegInfo.getImei();
			JSONObject params = new JSONObject();
			//NBID
			params.put("NBId", devRegInfo.getIotserial());
			params.put("imei", devRegInfo.getImei());
			params.put("operator", value);

			// 拼接请求内容，将需要下发的命令提交
			Post post = new Post();
			// 初始化请求地址对象
			String OperatorInfo = devRegInfo.getPlatformcode();
			String postUrl = readPropertise.readpro(path1, OperatorInfo.substring(0, 1)) + "removeDevice";

			// 从平台发送删除设备信息请求
			String result = post.post(postUrl, params.toString());

			if (result.equals("fail")) {
				logger.debug(deviceSerial + "," + new Date() + "," + "删除设备平台请求失败");

				// 请求失败，result 为 false
				responseData.setResult(false);
				responseData.setErrorCode2("00002");
				responseData.setMessage("平台删除失败");
			} else {
				if (JSONObject.fromObject(result).getBoolean("result")) {
					// 平台删除成功之后，将注册信息表中的相关信息删除 包括密钥和设备状态信息
					int deleteResult = devRegInfoService.deleteByPrimaryKey(deviceSerial);

					//删除设备状态信息
					int deleteStatus = iotImeiStatusService.deleteStatusByImei(imei);
					System.out.println(deleteStatus);

					//删除设备默认密钥
					int deleteSecretKye = devSecretKeyService.delSecretKey(deviceSerial,imei);

					if (deleteResult == 1 && deleteSecretKye==1) {
						responseData.setResult(true);
						responseData.setErrorCode2("00000");
						responseData.setMessage("设备删除成功");
					} else {
						responseData.setResult(false);
						responseData.setErrorCode2("00001");
						responseData.setMessage("平台设备成功删除，数据库删除失败，请手动操作！");
					}
				} else {
					responseData.setResult(false);
					responseData.setErrorCode2("00002");
					responseData.setMessage("平台删除失败，" + JSONObject.fromObject(result).getString("message"));
				}
			}
		}
		return responseData;
	}

}
