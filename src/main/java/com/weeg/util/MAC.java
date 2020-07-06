package com.weeg.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
* @ClassName: MAC
* @Description: MAC值计算
* @author yuyan
* @date 2019年11月12日
 */
public class MAC {

	/**
	 * 
	 * @Title: HMACSHA256
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param  data 需要计算的数据
	 * @param @param  key mac密钥
	 * @param @param  macData 随机码
	 * @param @return
	 * @param @throws Exception 参数
	 * @return String 返回类型
	 * @throws @author yy
	 * @date 2019年11月12日
	 */
	public String HMACSHA256(byte[] data, String macData,String IMEI) throws Exception {
//		public static String HMACSHA256(String data, String key) throws Exception {
		// 将通信随机码转换成byte[]，用于计算加密值
		DataFomat dataFomat = new DataFomat();
		byte[] macDataByte = dataFomat.toBytes(macData);
		// 创建AES对象，对通信随机码进行加密
		AESUtil aesUtil = new AESUtil();
		byte[] encryp = aesUtil.encryptAES(macDataByte,IMEI);

		// 取加密后数值的前16字节
		byte[] macKey = new byte[16];
		for (int i = 0; i < 16; i++) {
			macKey[i] = encryp[i];
		}

		//计算MAC值
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(macKey, "HmacSHA256");
		sha256_HMAC.init(secret_key);
		byte[] array = sha256_HMAC.doFinal(data);
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}

		//将MAC值全部转成大写
		return sb.toString().toUpperCase();
	}


	/**
	 * 	加密加MAC认证
	 * @param dt
	 * @param random
	 * @return
	 * @throws Exception
	 */
	public static String encryptAESAndMAC(String dt,String random,String keyvalue) throws Exception {
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

	/**
	 * 加密加MAC  端口号+IP 分开转byte
	 */
	public static String encryptAESAndMAC2(String ip,String port,String random,String keyvalue) throws Exception {
		AESUtil aesutil = new AESUtil();
		DataFomat dataFomat = new DataFomat();
		MAC mac = new MAC();
		String body = "";
		// 16进制数字转成byte[]数组
//		byte[] dtbytes = dt.getBytes();
		byte[] bytesip = dataFomat.toBytes(ip);
		byte[] bytesport = dataFomat.toBytes(port);
		byte[] bytesport2 = aesutil.pkcs7Padding(bytesport);


		//将两个数组合并
		byte[] bytes3 = DataFomat.addBytes(bytesip,bytesport2);

		// 对数据进行加密 先判断bytes[]数组长度大于16吗
		if (bytes3.length > 16) {
			int length = bytes3.length;
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
						newTxet[j] = bytes3[i * 16 + j];
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
			String newresult = body + mactype;
			return newresult;
		}
		return  null;
	}
}
