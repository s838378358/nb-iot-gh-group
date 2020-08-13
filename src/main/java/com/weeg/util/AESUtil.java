package com.weeg.util;

import com.weeg.controller.SendCmdControllerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class AESUtil {
	//配置日志
	private static final Logger LOG = LoggerFactory.getLogger(AESUtil.class);

	// 16进制char集
	static byte[] iv = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
			0x0F };

	public byte[] pkcs7Padding(byte[] b) {
		int length = 16 - b.length;
		byte[] pading = new byte[16];

		System.arraycopy(b, 0, pading, 0, b.length);

		for (int i = b.length; i < 16; i++) {
			pading[i] = iv[length];
		}
		return pading;
	}

	/**
	 * 解密
	 * 
	 * @Title: decryptAES @Description: 由于C语言在加密时采用了模式，所以JAVA在解析时需要采用模式来解密 @param
	 *         b @return @return: byte[] @throws
	 */
	public byte[] decryptAES(byte[] b,String keyvalue) {
//		LOG.info("解密参数byte[]:"+ b);
//		LOG.info("解密参数密钥:"+ keyvalue);

		try {

			DataFomat dataFomat = new DataFomat();
			byte[] raw = dataFomat.toBytes(keyvalue);
			SecretKeySpec skp = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, skp);
			byte[] original = cipher.doFinal(b);
			return original;

		} catch (Exception e) {
//			LOG.info("解密发生的异常:"+ e);
			System.out.println("数据解密时发生异常...");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密
	 * 
	 * @Title: encryptAES @param b @param aesKey @return @return: byte[] @throws
	 */
	public byte[] encryptAES(byte[] decodeContent,String keyvalue) {
//		LOG.info("加密参数byte[]:"+ Arrays.toString(decodeContent));
//		LOG.info("加密参数密钥:"+ keyvalue);
		try {
			DataFomat dataFomat = new DataFomat();
			byte[] raw = dataFomat.toBytes(keyvalue);

			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			byte[] encrypted;
			if(decodeContent.length==16) {
				encrypted = cipher.doFinal(decodeContent);
			}else {
				//不足16位的，将数据补齐
				encrypted = cipher.doFinal(pkcs7Padding(decodeContent));
			}
			return encrypted;
		} catch (Exception e) {
			LOG.info("加密发生的异常:"+ e);
//			System.out.println("数据加密时发生异常...");
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 	加密加MAC
	 * @param dt
	 * @param random
	 * @return
	 * @throws Exception
	 */
	public String encryptAESAndMAC(String dt,String random,String keyvalue) throws Exception {
		AESUtil aesutil = new AESUtil();
		DataFomat dataFomat = new DataFomat();
		MAC mac = new MAC();
		String body = "";
		// 16进制数字转成byte[]数组
//		byte[] dtbytes = dt.getBytes();
		byte[] bytes = dataFomat.toBytes(dt);
		// 对数据进行加密 先判断bytes[]数组长度大于16吗
		if (bytes.length > 16) {
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
			String mactype = mac.HMACSHA256(b2, random,keyvalue);
			// 拼接成数据域 先将加密数据转成字符串， 再拼上MAC字符串
//			String jmysj = dataFomat.bytesToHexFun1(b2);
			String newresult = body + mactype;
			return newresult;
		} else {
			byte[] bytes2 = aesutil.encryptAES(bytes,keyvalue);
			//随机数 + 加密数据域
			String encrystr = dataFomat.bytes2HexString(bytes2);
			encrystr = encrystr.replaceAll(" ", "");
			String randomencry = random + encrystr;
			byte[] b2 = dataFomat.toBytes(randomencry);
			// Mac认证
			String mactype = mac.HMACSHA256(b2, random,keyvalue);
			// 拼接成数据域 先将加密数据转成字符串， 再拼上MAC字符串
			String jmysj = dataFomat.bytesToHexFun1(bytes2);
			String newresult = jmysj + mactype;
			return newresult;
		}
	}

	public static void main(String[] args) {
//		AESUtil aesUtil = new AESUtil();
//		String test = "8090";
//		DataFomat dataFomat = new DataFomat();
//		byte[] raw = dataFomat.toBytes(test);
//
//		byte[] aaaa = aesUtil.pkcs7Padding(raw);
//
//		System.out.println(aaaa);
	}
	
	
	/**
	 * 
	 * @Title: crc
	 * @Description: 计算crc校验码
	 * @param @param  request
	 * @param @param  response
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	public static String crc(String src) {
		DataFomat dataFomat = new DataFomat();
		byte[] b = dataFomat.hex2byte(src);

		short test = dataFomat.crcencodetowngas(b);

		// 分配两个字节的空间
		byte[] packagebuf = new byte[2];
		// 将int型打包成byte
		ByteBuffer buf;
		buf = ByteBuffer.allocateDirect(2);
		// 大端
		buf.order(ByteOrder.BIG_ENDIAN);
		// 放入数据
		buf.putShort(test);
		// 字节对齐
		buf.rewind();
		// 获取bytes
		buf.get(packagebuf, 0, 2);
		String crcData = dataFomat.byteToHex(packagebuf, packagebuf.length).replace(" ", "").toUpperCase();
		return crcData;
	}
	
	
	/**
	 * 传入设备序列号，获取设备最新的随机码
	 * @param devserial
	 * @return
	 */
//	public String getrandom(String devserial) {
//		//获取随机码
//		//先查出最新的3001请求的classID，再根据classID查出3001表的通讯随机码
//		IotPushRecvReponse iprr = iotPushRecvReponseService.selectClassid(devserial,"3001");
//		String resclassid = iprr.getClassid();
//		DevDataLog resdataLog = devDataLogService.selectByChildclassId(resclassid);
//		String data = resdataLog.getData();
//		String datastr = JSONObject.fromObject(data).getString("数据域");
//		String random = JSONObject.fromObject(datastr).getString("通信随机码");
//		return random;
//	}

	/**
	 * 	加密加MAC
	 * @param dt
	 * @param devRegInfo
	 * @return
	 * @throws Exception
	 */
//	public String encryptAESAndMAC(String dt,DevRegInfo devRegInfo) throws Exception {
//		String body = "";
//		DataFomat dataFomat = new DataFomat();
//		CmdController cc = new CmdController();
//		MAC mac = new MAC();
//		// 16进制数字转成byte[]数组
//		byte[] bytes = dataFomat.toBytes(dt);
//		// 对数据进行加密 先判断bytes[]数组长度大于16吗
//		if (bytes.length > 16) {
//			int length = bytes.length / 16;
//			for (int i = 0; i < length; i++) {
//				// 创建一个长度是16的数组,用于存放每一段数组
//				byte[] newTxet = new byte[16];
//				for (int j = 0; j < 16; j++) {
//					newTxet[j] = bytes[i * 16 + j];
//				}
//				String originalString = dataFomat.bytes2HexString(encryptAES(newTxet));
//				body = body + originalString;
//			}
//			// 去掉body中的空格
//			body = body.replace(" ", "");
//			// 16进制数字转成byte[]数组
//			byte[] b2 = dataFomat.toBytes(body);
//			// 随机码
//			String devserial = devRegInfo.getDevserial();
//			String suijima = cc.getrandom(devserial);
//			// Mac认证
//			String mactype = mac.HMACSHA256(b2, suijima);
//			// 拼接成数据域 先将加密数据转成字符串， 再拼上MAC字符串
//			String jmysj = dataFomat.bytesToHexFun1(b2);
//			String newresult = jmysj + mactype;
//			return newresult;
//		} else {
//			byte[] bytes2 = encryptAES(bytes);
//			// 随机码
//			String devserial = devRegInfo.getDevserial();
//			String suijima = cc.getrandom(devserial);
//			// Mac认证
//			String mactype = mac.HMACSHA256(bytes2, suijima);
//			// 拼接成数据域 先将加密数据转成字符串， 再拼上MAC字符串
//			String jmysj = dataFomat.bytesToHexFun1(bytes2);
//			String newresult = jmysj + mactype;
//			return newresult;
//		}
//	}

}