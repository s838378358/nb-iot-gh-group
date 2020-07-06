package com.weeg.util;

import java.io.*;
import java.util.Properties;

/**
 * 
 * @ClassName: ReadPropertise
 * @Description: 读取配置文件
 * @author yuyan
 * @date 2019年11月12日
 */
public class ReadPropertise {

//
//	private static Properties props = new Properties();
//
//	static {
//		try {
//			props.load(new FileInputStream(profilepath));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			System.exit(-1);
//		} catch (IOException e) {
//			System.exit(-1);
//		}
//	}


	/**
	 * 更新（或插入）一对properties信息(主键及其键值)
	 * 如果该主键已经存在，更新该主键的值；
	 * 如果该主键不存在，则插件一对键值。
	 * @param keyname 键名
	 * @param keyvalue 键值
	 */
//	public Boolean writeProperties(String keyname,String keyvalue) {
//		try {
//			// 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
//			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
//			OutputStream fos = new FileOutputStream(profilepath);
//			props.setProperty(keyname, keyvalue);
//			// 以适合使用 load 方法加载到 Properties 表中的格式，
//			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
//			props.store(fos, "Update '" + keyname + "' value");
//			return true;
//		} catch (IOException e) {
//			System.err.println("属性文件更新错误");
//			return false;
//		}
//	}

	/**
	 * 更新properties文件的键值对
	 * 如果该主键已经存在，更新该主键的值；
	 * 如果该主键不存在，则插件一对键值。
	 * @param keyname 键名
	 * @param keyvalue 键值
	 */
//	public void updateProperties(String keyname,String keyvalue) {
//		try {
//			props.load(new FileInputStream(profilepath));
//			// 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
//			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
//			OutputStream fos = new FileOutputStream(profilepath);
//			props.setProperty(keyname, keyvalue);
//			// 以适合使用 load 方法加载到 Properties 表中的格式，
//			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
//			props.store(fos, "Update '" + keyname + "' value");
//		} catch (IOException e) {
//			System.err.println("属性文件更新错误");
//		}
//	}


	/**
	 * 根据主键key读取主键的值value
	 * @param filePath 属性文件路径
	 * @param key 键名
	 */
	public String readValue(String filePath, String key) {
		Properties props = new Properties();

//		File fileB = new File(this.getClass().getResource("").getPath());
//		System.out.println("fileB path: " + fileB);
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					filePath));
			props.load(in);
			String value = props.getProperty(key);
			System.out.println(key +"键的值是："+ value);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	// 读取配置文件中内容，根据Key，查找到对应的value
	public String readpro(String path, String key) {
		Properties prop = new Properties();
		String value = "";
		try {

			// 读取属性文件a.properties
//			InputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
			InputStream in = new BufferedInputStream(new FileInputStream(path + "/data.properties"));
			prop.load(in); /// 加载属性列表

//	            Iterator<String> it=prop.stringPropertyNames().iterator();

			value = prop.getProperty(key);

			// 获取整个文件中的属性
//	            while(it.hasNext()){
////	                String key=it.next();
//	                System.out.println(key+":"+prop.getProperty(key));
//	            }
			in.close();

//	            ///保存属性到b.properties文件
//	            FileOutputStream oFile = new FileOutputStream("b.properties", true);//true表示追加打开
//	            prop.setProperty("phone", "10086");
//	            prop.store(oFile, "The New properties file");
//	            oFile.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return value;
	}


	/**
	 * 10
	 * 01
	 * 00112233445566778899AABBCCDDEEFF
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

//		ReadPropertise rp = new ReadPropertise();
//		JSONObject jb = new JSONObject();
//		JSONObject jobj = new JSONObject();
//		String keylength = "10";
//		String keyname = "01";
//		String keyvalue = "00112233445566778899AABBCCDDEEFF";
//		jb.put("keyname",keyname);
//		jb.put("keylength",keylength);
//		jb.put("keyvalue",keyvalue);
//
//		jobj.put("SecretKey",jb.toString());
//
//		boolean b = rp.writeProperties("865820030065101", jobj.toString());
//
//		System.out.println("操作完成");
//
//		Properties properties = new Properties();
//		properties.load(in);
//		//获取.properties文件key对应的值
//		String value = properties.getProperty("865820030065101");
//		System.out.println(value);
//
////		String value = rp.readValue(profilepath,"01");
////		System.out.println(value);
//
//		String SecretKey = JSONObject.fromObject(value).getString("SecretKey");
//		String keyname2 = JSONObject.fromObject(SecretKey).getString("keyname");
//		String keylength2 = JSONObject.fromObject(SecretKey).getString("keylength");
//		String keyvalue2 = JSONObject.fromObject(SecretKey).getString("keyvalue");
//		System.out.println("keyname:"+keyname2);
//		System.out.println("keylength2:"+keylength2);
//		System.out.println("keyvalue2:"+keyvalue2);


//		Properties prop = new Properties();
//		try {
//
//			// 读取属性文件a.properties
//			InputStream in = new BufferedInputStream(new FileInputStream("data.properties"));
//			prop.load(in); /// 加载属性列表
//			Iterator<String> it = prop.stringPropertyNames().iterator();
//			while (it.hasNext()) {
//				String key = it.next();
//				System.out.println(key + ":" + prop.getProperty(key));
//			}
//			in.close();

//	            ///保存属性到b.properties文件
//	            FileOutputStream oFile = new FileOutputStream("b.properties", true);//true表示追加打开
//	            prop.setProperty("phone", "10086");
//	            prop.store(oFile, "The New properties file");
//	            oFile.close();
//		} catch (Exception e) {
//			System.out.println(e);
//		}
	}
}
