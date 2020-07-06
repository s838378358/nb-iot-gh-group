package com.weeg.controller;

import com.weeg.bean.DevDataInfo;
import com.weeg.bean.IotPushRecvReponse;
import com.weeg.service.DevDataInfoService;
import com.weeg.util.Post;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 获取实时数据
 *
 * @author lym  
 *
 * @date 2019年11月4日
 */
@Controller
@RequestMapping("weeg")
public class WeegInterfaceController {
	static Post post = new Post();
	Logger logger = Logger.getLogger(WeegInterfaceController.class);
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	@Autowired
	private DevDataInfoService devdatainfoService;
	@Autowired
	private com.weeg.service.IotPushRecvReponseService IotPushRecvReponseService;

	/**
	 * 根据设备序列号，获取实时数据
	 * 
	 * @author yuyan
	 * @date 2019年3月1日
	 */
	@RequestMapping(value = "/getActualData")
	public void getActualData(String devSerial, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 记录日志，操作人员在当前时间登录
		logger.debug(devSerial + "," + new Date() + "," + "获取实时数据！");

		JSONObject returnObj = new JSONObject();
		try {
			// 根据设备序列号，去数据库查询得到这个设备对应的平台信息和NBId
			DevDataInfo devdatainfo = devdatainfoService.selectActualData(
					"3003", devSerial);
			if (devdatainfo == null) {
				returnObj.put("result", "false");
				returnObj.put("code", "");
				returnObj.put("data", "");
				returnObj.put("msg", "根据表具序列号：" + devSerial + "没有找到对应实时数据！");
			} else {

				returnObj.put("result", "true");
				returnObj.put("code", "");
				returnObj.put("data", JSONObject.fromObject(devdatainfo)
						.getString("data"));
				returnObj.put("msg", "实时数据查询成功！");
			}
		} catch (Exception e) {
			// 平台注册成功但数据库没有操作成功，认为注册失败
			returnObj.put("result", "false");
			returnObj.put("code", "");
			returnObj.put("data", "");
			returnObj.put("msg", e.getMessage());
		}
		response.setHeader("content-type", "text/html;charset=UTF-8");// 设置响应头部,设置主体的编码格式是UTF-8
		response.setCharacterEncoding("UTF-8");// 设置传输的编码格式
		Writer writer = response.getWriter();
		writer.write(returnObj.toString());// 将 字符串内容写入缓存
		writer.flush();// 将缓存输出
		writer.close();
	}

	/**
	 * 根据设备序列号，获取实时数据
	 * 
	 * @author yuyan
	 * @date 2019年3月1日
	 */
	@RequestMapping(value = "/getDataListByTime")
	public void getDataListByTime(String devSerial, String startTime,
			String endTime, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 记录日志，操作人员在当前时间登录
		logger.debug(devSerial + "获取时间" + startTime + "-" + endTime
				+ "之间的数据列表！");

		JSONObject returnObj = new JSONObject();
		try {
			// 根据设备序列号，去数据库查询得到这个设备对应的平台信息和NBId
			// DevDataInfo devdatainfo = devdatainfoService.selectActualData(
			// "3003", devSerial);
			List<IotPushRecvReponse> iotPushRecvReponses = IotPushRecvReponseService
					.selectBySerialandTime(devSerial, startTime, endTime);

			if (iotPushRecvReponses == null) {
				returnObj.put("result", "false");
				returnObj.put("code", "");
				returnObj.put("data", "");
				returnObj.put("msg", "根据表具序列号：" + devSerial + "没有找到对应实时数据！");
			} else {
				List<IotPushRecvReponse> iotPushRecvReponses2=new ArrayList<IotPushRecvReponse>(iotPushRecvReponses.size());
				for (int i = 0; i < iotPushRecvReponses.size(); i++) {
					IotPushRecvReponse iotPushRecvReponse = iotPushRecvReponses
							.get(i);
					if(iotPushRecvReponse.getConfirmtime()!=null){
						iotPushRecvReponse.setConfirmtime(iotPushRecvReponse.getConfirmtime());
					}
					
					if(iotPushRecvReponse.getIotreponsetime()!=null){
						iotPushRecvReponse.setIotreponsetime(iotPushRecvReponse.getIotreponsetime());
					}
					
					if(iotPushRecvReponse.getUploadtime()!=null){
						iotPushRecvReponse.setUploadtime(iotPushRecvReponse.getUploadtime());
					}
					iotPushRecvReponses2.add(iotPushRecvReponse);
				}
				
				returnObj.put("result", "true");
				returnObj.put("code", "");
				returnObj.put("data", iotPushRecvReponses2);
				returnObj.put("msg", "实时数据查询成功！");
			}
		} catch (Exception e) {
			// 平台注册成功但数据库没有操作成功，认为注册失败
			returnObj.put("result", "false");
			returnObj.put("code", "");
			returnObj.put("data", "");
			returnObj.put("msg", e.getMessage());
		}
		response.setHeader("content-type", "text/html;charset=UTF-8");// 设置响应头部,设置主体的编码格式是UTF-8
		response.setCharacterEncoding("UTF-8");// 设置传输的编码格式
		Writer writer = response.getWriter();
		writer.write(returnObj.toString());// 将 字符串内容写入缓存
		writer.flush();// 将缓存输出
		writer.close();
	}

}
