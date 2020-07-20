package com.weeg.controller;

import com.weeg.bean.DevControlCmd;
import com.weeg.bean.DevDataLog;
import com.weeg.bean.IotImeiStatus;
import com.weeg.bean.IotPushRecvReponse;
import com.weeg.service.DevControlCmdService;
import com.weeg.service.DevDataLogService;
import com.weeg.service.IotImeiStatusService;
import com.weeg.service.IotPushRecvReponseService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 页面数据接口
 *
 * @ClassName: WeegDateController.java
 * @Description: 该类的功能描述
 * @author: yuyan
 * @date: 2019年11月5日 上午8:58:03
 */
@RestController
@RequestMapping("/NBWeegServer/weeg")
public class WeegDateController {

    Logger logger = Logger.getLogger(WeegDateController.class);
    @Autowired
    IotPushRecvReponseService iotPushRecvReponseService;
    @Autowired
    DevControlCmdService devControlCmdService;
    @Autowired
    DevDataLogService devDataLogService;
    @Autowired
    IotImeiStatusService iotImeiStatusService;

    /**
     * 根据设备序列号，查询某一个时间段之间，该设备获取的数据列表
     * <p>
     * 开始时间是开始调试按钮按下的时间，停止时间是当前时间
     * <p>
     * Title: getDataList
     * </p>
     *
     * @author yuyan
     * @date 2019年4月4日
     */
    @RequestMapping(value = "/getDataList")
    public void getDataList(@RequestBody String body, HttpServletResponse response)
//	public void getDataList(String serial, String startTime, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        JSONObject getBody = JSONObject.fromObject(body);
        String serial = getBody.getString("serial");
        String startTime = getBody.getString("startTime");

        // 记录日志，操作人员在当前时间登录
        logger.debug(serial + "获取设备数据历史列表，时间间隔：" + startTime + "至今");

        // 获取当前时间，作为查询截止的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间的字符串
        Date endTime = new Date();
        String time = sdf.format(endTime);

        // 创建返回信息的对象
        JSONObject object = new JSONObject();

        try {
            // 获取推送数据对象列表
            List<IotPushRecvReponse> iotPushRecvReponses = iotPushRecvReponseService.selectBySerialandTime(serial,
                    startTime, time);

            //获取命令列表
            List<DevControlCmd> devControlCmds = devControlCmdService.selectBySerialandTime(serial,
                    startTime, time);

            //获取设备在线状态,status是设备状态，0标识离线，1标识在线
            IotImeiStatus imeiStatus = iotImeiStatusService.selectBySerial(serial);


            JSONObject dataObject = new JSONObject();
            dataObject.put("infoList", iotPushRecvReponses);
            dataObject.put("cmdList", devControlCmds);
            dataObject.put("devStatus", imeiStatus.getStatus());


            object.put("result", true);
            object.put("data", dataObject);
        } catch (Exception e) {
            object.put("result", false);
            object.put("data", "查询失败");
        }
        response.setHeader("content-type", "text/html;charset=UTF-8");// 设置响应头部,设置主体的编码格式是UTF-8
        response.setCharacterEncoding("UTF-8");// 设置传输的编码格式
        Writer writer = response.getWriter();
        System.out.println("开始调试" + object.toString());
        writer.write(object.toString());// 将 字符串内容写入缓存
        writer.flush();// 将缓存输出
        writer.close();
    }

    /**
     * 根据设备序列号，查询某一个时间段之间，该设备获取的数据列表
     * <p>
     * 开始时间是开始调试按钮按下的时间，停止时间是当前时间
     * <p>
     * Title: getDataList
     * </p>
     *
     * @author yuyan
     * @date 2019年4月4日
     */
    @RequestMapping(value = "/getDataDetail")
    public void getDataDetail(@RequestBody String body, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        JSONObject getBody = JSONObject.fromObject(body);
        String classid = getBody.getString("classid");
//		classid="a309d01d-09a5-4c7a-be84-918a8cca4b31";
        JSONObject object = new JSONObject();

        try {
            // 获取对象列表
            DevDataLog dataLog = devDataLogService.selectByChildclassId(classid);
            IotPushRecvReponse iotpushrecvreponse = iotPushRecvReponseService.selectReponse(classid);
            object.put("result", true);
            object.put("data", dataLog);
            object.put("dataresponse", iotpushrecvreponse);
        } catch (Exception e) {
            object.put("result", false);
            object.put("data", "查询失败");
        }
        response.setHeader("content-type", "text/html;charset=UTF-8");// 设置响应头部,设置主体的编码格式是UTF-8
        response.setCharacterEncoding("UTF-8");// 设置传输的编码格式
        Writer writer = response.getWriter();
        writer.write(object.toString());// 将 字符串内容写入缓存
        writer.flush();// 将缓存输出
        writer.close();
    }


//	/**
//	 * 查询设备在线状态
//	 * <p>
//	 * Title: registerDevice
//	 * </p>
//	 * 
//	 * @author yuyan
//	 * @date 2019年4月3日
//	 */
//	@RequestMapping(value = "/getOnlineStatus")
//	public void getOnlineStatus(String serial, HttpServletRequest request,
//			HttpServletResponse response) throws Exception {
//		// 记录日志，操作人员在当前时间登录
//		logger.debug(serial + new Date() + "获取设备状态");
//
//		// 创建返回信息的对象
//		JSONObject returnObj = new JSONObject();
//
//		// 首先根据设备序列号对设备进行查询
//		NBCommonInfo nbCommonInfo1 = NBCommonInfoService.selectByDevID(serial);
//		//取得结果对象中的平台信息数据
//		String OperatorInfo = nbCommonInfo1.getOperatorInfo();
//
//		// 读取配置文件，这样才可以得到当前设备对应的平台信息
//		String path = request.getSession().getServletContext().getRealPath("/");
//		String path1 = (new File(path)).getParent();
//		ReadPropertise readPropertise = new ReadPropertise();
//
//		// 根据传入的key，获得对应的value
//		String value = readPropertise.readpro(path1, OperatorInfo);
//
//		// 拼接请求参数对象
//		JSONObject params = new JSONObject();
//		params.put("NBId", nbCommonInfo1.getNBId());
//		params.put("operator", value);
//
//		Post post = new Post();
//		// 拼接注册地址
//		String registUrl = readPropertise.readpro(path1,
//				OperatorInfo.substring(0, 1))
//				+ "getDeviceStatus";
//
//		String RegistResult = post.post(registUrl, params.toString());
//
//		if (JSONObject.fromObject(RegistResult).getBoolean("result")) {
//			returnObj.put("result", true);
//			returnObj.put("code", "");
//			returnObj.put("data", JSONObject.fromObject(RegistResult)
//					.getBoolean("data"));
//			returnObj.put("msg", "查询成功");
//		} else {
//			returnObj.put("result", false);
//			returnObj.put("code", "");
//			returnObj.put("data", "");
//			returnObj.put("msg", "查询失败");
//		}
//
//		response.setHeader("content-type", "text/html;charset=UTF-8");// 设置响应头部,设置主体的编码格式是UTF-8
//		response.setCharacterEncoding("UTF-8");// 设置传输的编码格式
//		Writer writer = response.getWriter();
//		writer.write(returnObj.toString());// 将 字符串内容写入缓存
//		writer.flush();// 将缓存输出
//		writer.close();
//	}

}
