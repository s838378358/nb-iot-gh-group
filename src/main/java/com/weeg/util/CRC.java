package com.weeg.util;

/**
* @ClassName: CRC
* @Description: TODO(这里用一句话描述这个类的作用)
* @author yuyan
* @date 2019年11月13日
*/
public class CRC {
	/**
     * 计算产生校验码
     * @param data 需要校验的数据
     * @return 校验码
     */
    public static String Make_CRC(byte[] data) {
        byte[] buf = new byte[data.length];// 存储需要产生校验码的数据
        for (int i = 0; i < data.length; i++) {
            buf[i] = data[i];
        }
        int len = buf.length;
        int crc = 0xFFFF;//16位
        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256; // XOR byte into least sig. byte of
                                                // crc
            } else {
                crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
            }
        }
        String c = Integer.toHexString(crc);
        if (c.length() == 4) {
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 3) {
            c = "0" + c;
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 2) {
            c = "0" + c.substring(1, 2) + "0" + c.substring(0, 1);
        }
        return c;
    }
    
    	
    	
    	 /**
         * 计算CRC16校验码
         *
         * @param data 需要校验的字符串
         * @return 校验码
         */
        public static String getCRC(String data) {
            data = data.replace(" ", "");
            int len = data.length();
            if (!(len % 2 == 0)) {
                return "0000";
            }
            int num = len / 2;
            byte[] para = new byte[num];
            for (int i = 0; i < num; i++) {
                int value = Integer.valueOf(data.substring(i * 2, 2 * (i + 1)), 16);
                para[i] = (byte) value;
            }
            return getCRC(para);
        }
     
     
        /**
         * 计算CRC16校验码
         *
         * @param bytes 字节数组
         * @return {@link String} 校验码
         * @since 1.0
         */
        public static String getCRC(byte[] bytes) {
            //CRC寄存器全为1
            int CRC = 0x0000ffff;
            //多项式校验值
            int POLYNOMIAL = 0x0000a001;
            int i, j;
            for (i = 0; i < bytes.length; i++) {
                CRC ^= ((int) bytes[i] & 0x000000ff);
                for (j = 0; j < 8; j++) {
                    if ((CRC & 0x00000001) != 0) {
                        CRC >>= 1;
                        CRC ^= POLYNOMIAL;
                    } else {
                        CRC >>= 1;
                    }
                }
            }
            //结果转换为16进制
            String result = Integer.toHexString(CRC).toUpperCase();
            if (result.length() != 4) {
                StringBuffer sb = new StringBuffer("0000");
                result = sb.replace(4 - result.length(), 4, result).toString();
            }
            //交换高低位
            return result.substring(2, 4) + result.substring(0, 2);
        }
     
     
        public static void main(String[] args) {
            //01 03 20 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 7F FF 8C 45
            //01 03 00 00 00 08 44 0C
            //01 03 10 00 8F 02 4E 00 91 02 44 00 92 02 5A 00 8B 02 47 40 D8
            System.out.println(getCRC("BB0130011911130849360002000110303030303131313130303030303033340000000000000000000000000000000000000000000000000000000101000150000016DC202424DC6F5C68BE4D171B2D78EAFFB40002010001791698620E9A3836353832303033303036353130310500D182A8CAA4E781979AA537DC5A74A870F52B72DB299DED9F398D9386F318CB5B"));
//            System.out.println(getCRC("01 03 00 00 00 08"));
//            System.out.println(getCRC("01 03 10 00 8F 02 4E 00 91 02 44 00 92 02 5A 00 8B 02 47"));
        }
    
//    public static void main(String[] args){
//		String str="BB0130011911130849360002000110303030303131313130303030303033340000000000000000000000000000000000000000000000000000000101000150000016DC202424DC6F5C68BE4D171B2D78EAFFB40002010001791698620E9A3836353832303033303036353130310500D182A8CAA4E781979AA537DC5A74A870F52B72DB299DED9F398D9386F318CB5B";
////		byte[] data=str.getBytes();
//		String a=getCRC(str);
//		System.out.println(a);
//	}
}
