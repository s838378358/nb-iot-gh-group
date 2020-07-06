package com.weeg.util;//package com.weeg.util;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//
///**
//* @ClassName: rtest
//* @Description: TODO(这里用一句话描述这个类的作用)
//* @author yuyan
//* @date 2019年11月13日
//*/
//public class rtest {
//	//计算crc检验值
//	   public static short crcencodetowngas(byte[] b){   
//			int ret = 0;
//			int CRC = 0x00000000;
//			int POLYNOMIAL = 0x00001021;
//			int i, j;
//			int temp=0;
//
//			int buflen= b.length;
//
//			if (buflen == 0)
//			{
//			return (short) ret;
//			}
//			for (i = 0; i < buflen; i++)
//			{
//			temp = ((int)b[i] & 0x000000ff);
//			temp =temp<<8;
//			CRC ^= temp;
//			for (j = 0; j < 8; j++)
//			{
//			if ((CRC & 0x00008000) != 0)
//			{
//			CRC <<= 1;
//			CRC ^= POLYNOMIAL;
//			}
//			else
//			{
//			CRC <<= 1;
//			}
//			}
//			//System.out.println(Integer.toHexString(CRC));
//			}
//			return (short) CRC;
//		}
//	   
//	   
//	   public static void main(String[] args) {
//				System.out.println("Hello world \n");
//				String srt = "BB0130011911130849360002000110303030303131313130303030303033340000000000000000000000000000000000000000000000000000000101000150000016DC202424DC6F5C68BE4D171B2D78EAFFB40002010001791698620E9A3836353832303033303036353130310500D182A8CAA4E781979AA537DC5A74A870F52B72DB299DED9F398D9386F318CB5B";
//		        DataFomat dataFomat=new DataFomat();
//		        byte[] b = hex2byte(srt);
//		        
//		        short test = dataFomat.crcencodetowngas(b);
////				 int number = 123456;
//			        byte[] packagebuf =  new byte[2];
//			        //将int型打包成byte
//			        ByteBuffer buf;
//			        buf = ByteBuffer.allocateDirect(2);
//			        //小端
////			        buf.order(ByteOrder.LITTLE_ENDIAN);
//			        // 大端
//			        buf.order(ByteOrder.BIG_ENDIAN);
//
//			        //放入数据
//			        buf.putShort(test);
//			        //字节对齐
//			        buf.rewind();
//			        //获取bytes
//			        buf.get(packagebuf,0,2);
//
//			        System.out.println(dataFomat.byteToHex(packagebuf, packagebuf.length).replace(" ", "").toUpperCase());
//
//			        
//			        
//			        
//		}
//
//}
