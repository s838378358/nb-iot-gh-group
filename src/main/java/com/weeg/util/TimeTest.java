package com.weeg.util;

/**
 * 
* @ClassName: TimeTest
* @Description: 定时器demo
* @author yuyan
* @date 2019年11月12日
 */
public class TimeTest {
	public static void test(int index) {
		System.out.println("test:" + index);
	}
	
	public static void run() throws InterruptedException {
		
		for(int i=0;i<3;i++){
			test(i);
			Thread.sleep(3*1000);
		}
		
	}
	
	public static void main(String[] args) {
		try{
			run();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
