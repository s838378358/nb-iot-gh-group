package com.weeg.util;

import java.util.UUID;

/**
* @ClassName: GUID
* @Description: TODO(这里用一句话描述这个类的作用)
* @author yuyan
* @date 2019年11月15日
*/
public class GUID {
	public String guid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString(); 
	}
//	
//	public static void main(String[] args) {
//		System.out.println(guid());
//	}

}
