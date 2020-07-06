
package com.weeg.util;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 
 * @ClassName: DataFomat
 * @Description: 数据转换
 * @author yuyan
 * @date 2019年11月12日
 */
public class DataFomat {
	// 16进制char集
	private final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	// 16进制数字字符集
	private String hexString = "0123456789ABCDEF";

	/**
	 * 
	 * @Title: bytes2HexString
	 * @Description: 将byte数组转换成十六进制字符串
	 * @param @param  b
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月12日
	 */
	public String bytes2HexString(byte[] b) {
		String r = " ";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			r += hex.toUpperCase();
		}
		return r;
	}

	/**
	 * 
	 * @Title: bytesToHexFun1
	 * @Description: byte数组转换成String
	 * @param @param  bytes
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月12日
	 */
	public String bytesToHexFun1(byte[] bytes) {
		// 一个byte为8位，可用两个十六进制位标识
		char[] buf = new char[bytes.length * 2];
		int a = 0;
		int index = 0;
		for (byte b : bytes) { // 使用除与取余进行转换
			if (b < 0) {
				a = 256 + b;
			} else {
				a = b;
			}

			buf[index++] = HEX_CHAR[a / 16];
			buf[index++] = HEX_CHAR[a % 16];
		}

		return new String(buf);
	}

	public String bytesToHexFun2(byte[] bytes) {
		char[] buf = new char[bytes.length * 2];
		int index = 0;
		for (byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
			buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
			buf[index++] = HEX_CHAR[b & 0xf];
		}

		return new String(buf);
	}

//	public String bytesToHexFun3(byte[] bytes) {
//		StringBuilder buf = new StringBuilder(bytes.length * 2);
//		for (byte b : bytes) { // 使用String的format方法进行转换
//			buf.append(String.format("%02x", new Integer(b & 0xff)));
//		}
//		return buf.toString();
//	}

	/**
	 * 
	 * @Title: toBytes
	 * @Description: 将16进制字符串转换成byte[]
	 * @param @param  str
	 * @param @return 参数
	 * @return byte[] 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月12日
	 */
	public byte[] toBytes(String str) {
		if (str == null || str.trim().equals("")) {
			return new byte[0];
		}

		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			String subStr = str.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(subStr, 16);
		}
		return bytes;
	}

	/*
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0) + " ");
		}

		return sb.toString();

	}

	/*
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public String decode(String bytes) {
		String temp = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2){
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
		}
		temp = new String(baos.toByteArray());
		return temp;

	}

	public String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * byte数组转换成十六进制字符串
	 * 
	 * @Title: bytesToHexString
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param  src
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	public String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < 20; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
				System.out.println(stringBuilder);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();

	}

	/**
	 * 
	 * @Title: bytesToHexStringTwo
	 * @Description: 把字节数组转换成16进制字符串
	 * @param @param  bArray
	 * @param @param  count
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	public final String bytesToHexStringTwo(byte[] bArray, int count) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < count; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2){
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 分割字符串
	 * 
	 * @Title: Stringspace
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param  str
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	public String Stringspace(String str) {

		String temp = "";
		String temp2 = "";
		for (int i = 0; i < str.length(); i++) {

			if (i % 2 == 0) {
				temp = str.charAt(i) + "";
				temp2 += temp;
				System.out.println(temp);
			} else {
				temp2 += str.charAt(i) + " ";
			}

		}
		return temp2;
	}

	/**
	 * byte数组转换成十六进制字符串
	 * 
	 * @Title: byteToHex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param  bytes
	 * @param @param  count
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	public String byteToHex(byte[] bytes, int count) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex).append(" ");
		}
		return sb.toString();
	}

	/**
	 * ascii码转换成bcd
	 * 
	 * @Title: asc_to_bcd
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param  asc
	 * @param @return 参数
	 * @return byte 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	private byte asc_to_bcd(byte asc) {
		byte bcd;
		if ((asc >= '0') && (asc <= '9')){
			bcd = (byte) (asc - '0');
		}
		else if ((asc >= 'A') && (asc <= 'F')){
			bcd = (byte) (asc - 'A' + 10);
		}
		else if ((asc >= 'a') && (asc <= 'f')){
			bcd = (byte) (asc - 'a' + 10);
		}
		else{
			bcd = (byte) (asc - 48);
		}
		return bcd;
	}

	/**
	 * ascii数组转换成bcd
	 * 
	 * @Title: ASCII_To_BCD
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param  ascii
	 * @param @param  asc_len
	 * @param @return 参数
	 * @return byte[] 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	public byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = (byte) asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}
	
	/**
	 * 十六进制字符串转换成byte数组
	* @Title: hex2byte
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param hex
	* @param @return
	* @param @throws IllegalArgumentException    参数
	* @return byte[]    返回类型
	* @throws
	* @author yuyan
	* @date 2019年11月13日
	 */
	public final byte[] hex2byte(String hex)
            throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }
	
    /**
     * 16进制字符串转为 byte[] 字母需要大写
     */
    private static final String HexStr = "0123456789ABCDEF";
    public static byte[] hexStrToByteArr(String hexStr) {
        char[] charArr = hexStr.toCharArray();
        byte btArr[] = new byte[charArr.length / 2];
        int index = 0;
        for (int i = 0; i < charArr.length; i++) {
            int highBit = HexStr.indexOf(charArr[i]);
            int lowBit = HexStr.indexOf(charArr[++i]);
            btArr[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return btArr;
    }

	// 计算crc检验值
	public short crcencode(byte[] b) {
		int ret = 0;
		int CRC = 0x0000ffff;
		int POLYNOMIAL = 0x0000a001;
		int i, j;

		int buflen = b.length;

		if (buflen == 0) {
			return (short) ret;
		}
		for (i = 0; i < buflen - 2; i++) {
			CRC ^= ((int) b[i] & 0x000000ff);
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00000001) != 0) {
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC >>= 1;
				}
			}
			// System.out.println(Integer.toHexString(CRC));
		}

		// System.out.println(Integer.toHexString(CRC));
		b[buflen - 2] = (byte) (CRC & 0x00ff);
		b[buflen - 1] = (byte) (CRC >> 8);

		return (short) CRC;
	}

	// 计算crc检验值
	public short crcencodetowngas(byte[] b) {
		int ret = 0;
		int CRC = 0x00000000;
		int POLYNOMIAL = 0x00001021;
		int i, j;
		int temp = 0;

		int buflen = b.length;

		if (buflen == 0) {
			return (short) ret;
		}
		for (i = 0; i < buflen; i++) {
			temp = ((int) b[i] & 0x000000ff);
			temp = temp << 8;
			CRC ^= temp;
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00008000) != 0) {
					CRC <<= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC <<= 1;
				}
			}
			// System.out.println(Integer.toHexString(CRC));
		}
		return (short) CRC;
	}

	public byte HEX2BCD(byte tempbyte) {
		byte result = 0;
		result = (byte) ((tempbyte / 10) << 4);
		result |= (byte) (tempbyte % 10);
		return result;
	}

	public byte[] HEX2BCD_BUF(byte[] buf, int lenth) {
		byte[] result = new byte[lenth];
		int i = 0;
		for (i = 0; i < lenth; i++) {
			result[i] = HEX2BCD(buf[i]);
		}
		return result;
	}

	/**
	 * 将byte数据转换成String数据
	 */
	public String toHex(byte b) {
		String result = Integer.toHexString(b & 0xFF);
		if (result.length() == 1) {
			result = '0' + result;
		}
//		String result = Integer.toHexString(b & 0xFF).toUpperCase().replace(' ', '0');
		return result;
	}

	// 大端转换
	public static short BigEndian(byte[] b, int shutOff, int length) {
		ByteBuffer buf = ByteBuffer.allocateDirect(2);
		buf.order(ByteOrder.BIG_ENDIAN);
		// 放入数据,
		buf.put(b, shutOff, length);
		// 字节对齐
//		buf.flip();
		buf.rewind();
		// 获取int
		return buf.getShort();
	}

	// byte[]转换成char
	public char byteToChar(byte[] b) {
		char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
		return c;
	}

	/**
	 * 字符串转成ASCII码
	 */
	public String stringToAscii(String value)
	{
		StringBuffer sbu = new StringBuffer();
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if(i != chars.length - 1)
			{
				sbu.append((int)chars[i]).append(",");
			}
			else {
				sbu.append((int)chars[i]);
			}
		}
		return sbu.toString();
	}

	/**
	 * 16进制字符串转成ASCII码
	 * @param hex
	 * @return
	 */
	public String convertHexToString(String hex){

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		//49204c6f7665204a617661 split into two characters 49, 20, 4c...
		for( int i=0; i<hex.length()-1; i+=2 ){

			//grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			//convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			//convert the decimal to character
			sb.append((char)decimal);

			temp.append(decimal);
		}
		return sb.toString();
	}

	/**
	 * 十六进制字符串转换成字符串
	 * 
	 * @Title: hexStr2Str
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param  hexStr
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	public String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	/**
	 * 字符串转换成十六进制字符串
	 * 
	 * @Title: stringToHex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param  s
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws @author yuyan
	 * @date 2019年11月13日
	 */
	public String stringToHex(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			if (s4.length() == 1) {
				s4 = '0' + s4;
			}
			str = str + s4 + " ";
		}
		return str;
	}

	/**
	 * Byte转Bit
	* @Title: byteToBit
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param b
	* @param @return    参数
	* @return String    返回类型
	* @throws
	* @author yuyan
	* @date 2019年11月13日
	 */
	public String byteToBit(byte b) {
		return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1) + (byte) ((b >> 5) & 0x1)
				+ (byte) ((b >> 4) & 0x1) + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1) + (byte) ((b >> 1) & 0x1)
				+ (byte) ((b >> 0) & 0x1);
	}

	/**
	 * Bit转Byte
	* @Title: BitToByte
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param byteStr
	* @param @return    参数
	* @return byte    返回类型
	* @throws
	* @author yuyan
	* @date 2019年11月13日
	 */
	public byte BitToByte(String byteStr) {
		int re, len;
		if (null == byteStr) {
			return 0;
		}
		len = byteStr.length();
		if (len != 4 && len != 8) {
			return 0;
		}
		if (len == 8) {// 8 bit处理
			if (byteStr.charAt(0) == '0') {// 正数
				re = Integer.parseInt(byteStr, 2);
			} else {// 负数
				re = Integer.parseInt(byteStr, 2) - 256;
			}
		} else {// 4 bit处理
			re = Integer.parseInt(byteStr, 2);
		}
		return (byte) re;
	}

	// 16进制数转二进制
	public byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	/**
	 * char转换成byte
	* @Title: toByte
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param c
	* @param @return    参数
	* @return byte    返回类型
	* @throws
	* @author yuyan
	* @date 2019年11月13日
	 */
	private byte toByte(char c) {
		byte b = (byte) "0123456789abcdef".indexOf(c);
		return b;
	}
	
	/**
	 * String字符串转成String[]数组
	 * @param st
	 */
	public String[] stringSZ(String st) {
		String[] str = new String[st.length()];
		for(int i=0;i<str.length;i++){
            str[i]= String.valueOf(st.charAt(i));
        }
		return str;
	}
	
	/**
	 * 16进制字符串转成 二进制字符串
	 */
	public String  hexString2binaryString(String hexString) {
		if (hexString == null){
			return null;
		}
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
		tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
		bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}
	
	/**
	 * 二进制字符串转成16进制字符串
	 * @param bString
	 */
	public String binaryString2hexString(String bString) {
	    if (bString == null || bString.equals("") || bString.length() % 8 != 0){
			return null;
		}
	    StringBuffer tmp=new StringBuffer();
	    int iTmp = 0;
	    for (int i = 0; i < bString.length(); i += 4) {
	      iTmp = 0;
	      for (int j = 0; j < 4; j++) {
	        iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
	      }
	      tmp.append(Integer.toHexString(iTmp));
	    }
	    return tmp.toString();
	}

	/**
	 * 两个byte[]数组合并
	 */
	public static byte[] addBytes(byte[] data1, byte[] data2) {
		byte[] data3 = new byte[data1.length + data2.length];
		System.arraycopy(data1, 0, data3, 0, data1.length);
		System.arraycopy(data2, 0, data3, data1.length, data2.length);
		return data3;
	}
}
