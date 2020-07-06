package com.weeg.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CmdUtils {
	
	/**
	 * 对3001的回复
	* @Title: response3001
	* @param @param data
	* @param @param suijima
	* @param @return
	* @param @throws Exception    参数
	* @return String    返回类型
	* @throws
	* @author yuyan
	* @date 2019年11月18日
	 */
	public String response3001(String data, String suijima,String mid,String imeikey) throws Exception {
		Date date = new Date();
		DataFomat dataFomat = new DataFomat();
		AESUtil aesutil = new AESUtil();
		//data 数据域    对于3001的回复数据，数据域是2字节错误码加上6字节时钟 ，错误码暂时默认0000无错误
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
		//错误码 加 日期  数据对象内容
		String data2 = data + simpleDateFormat.format(date);

		// 对数据域进行MAC认证
		MAC mac2 = new MAC();
		String da = suijima + data2;
		byte[] b1 = dataFomat.toBytes(da);
		String maca = mac2.HMACSHA256(b1, suijima,imeikey);
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
		
		String CRC = aesutil.crc(MID + C + DID + D);
		String tall = "16";
		String cmd = head + T + V + L + MID + C + DID + D + CRC + tall;
		System.out.println("cmd"+cmd);
		return cmd;
	}

	/**
	 *
	 * @Title: ctrlBody
	 * @Description: 拼接控制域内容
	 * @param @param  funNum 功能码，01/02/03/04/05/07/08
	 * @param @param  data7 方向，0表示上行，1标识下行
	 * @param @param  data6 是否有后续帧，0标识无，1标志有
	 * @param @param  data5 指令帧执行结果，0标识成功，1标识失败
	 * @param @param  request
	 * @param @param  response
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	public static String ctrlBody(String funNum, String data7, String data6, String data5, HttpServletRequest request,
								  HttpServletResponse response) {
		String ctrl = data7 + data6 + data5;
		// 功能码，十六进制转换成二进制
		if (funNum.equals("01")) {
			ctrl = ctrl + "00001";
		} else if (funNum.equals("02")) {
			ctrl = ctrl + "00010";
		} else if (funNum.equals("03")) {
			ctrl = ctrl + "00011";
		} else if (funNum.equals("04")) {
			ctrl = ctrl + "00100";
		} else if (funNum.equals("05")) {
			ctrl = ctrl + "00101";
		} else if (funNum.equals("07")) {
			ctrl = ctrl + "00111";
		} else if (funNum.equals("08")) {
			ctrl = ctrl + "01000";
		}
		return "";
	}

}
