package com.weeg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Post {
	static String host = "api.js.cmcconenet.com";
	//配置日志
	private static final Logger LOG = LoggerFactory.getLogger(Post.class);
//	// 江苏key
//	static String apiKey = "yVMWn3tBUaWds2SLrvD6Li2C=KU=";

	// Http发出post请求
	public String post(String strURL, String params) {
		try {
			URL url = new URL(strURL);// 创建连接
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();// 打开链接
			connection.setDoOutput(true);// 隐式设置请求方式为post
			connection.setDoInput(true);// 从httpUrlConnection读入，默认的情况下是true
			connection.setUseCaches(false);// post请求不能使用缓存
			connection.setInstanceFollowRedirects(true);// 是否连接遵循重定向
			connection.setRequestMethod("POST");// 设置请求方式 
			connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
			connection.connect();// 实现连接
			OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream(), "UTF-8");// 字符输出流，确认流的输出文件按照UTF—8的格式
			out.append(params);// 将params中的字符追加

			out.flush();// 在关闭流的时候，将内存中的数据一次性输出
			out.close();// 关闭流
			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			reader.close();
			// 断开连接
			connection.disconnect();
			String response = sb.toString();
//			System.out.println("response:"+response);
			return response;
		} catch (IOException e) {
			LOG.info("post请求异常，重新请求");
//			System.out.println("post请求异常，重新请求");
//			Post.post(strURL,params);
			e.printStackTrace();
			return "fail";
		}
	}

	/**
	 * 向移动云平台推送的请求
	 * @param strURL
	 * @param params
	 * @param host
	 * @param apiKey
	 * @return
	 */
	public String post(String strURL, String params,String host,String apiKey) {
		try {
			URL url = new URL(strURL);// 创建连接
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();// 打开链接
			connection.setDoOutput(true);// 隐式设置请求方式为post
			connection.setDoInput(true);// 从httpUrlConnection读入，默认的情况下是true
			connection.setUseCaches(false);// post请求不能使用缓存
			connection.setInstanceFollowRedirects(true);// 是否连接遵循重定向
			connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
			connection.setRequestProperty("Host", host);
			connection.setRequestProperty("api-key", apiKey);
			//设置超时毫秒数
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.connect();// 实现连接
			OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream(), "UTF-8");// 字符输出流，确认流的输出文件按照UTF—8的格式
			out.append(params);// 将params中的字符追加
			out.flush();// 在关闭流的时候，将内存中的数据一次性输出
			out.close();// 关闭流

			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			reader.close();
			// 断开连接
			connection.disconnect();
			String response = sb.toString();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "fail";
	}

	/**
	 * 发送delete请求
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Title: delete
	 * </p>
	 *
	 * @author yuyan
	 * @date 2018年11月22日
	 */
	public static String delete(String url,String host,String apiKey) {
		// 定义stringbuffer 方便后面读取网页返回字节流信息时的字符串拼接
		StringBuffer buffer = new StringBuffer();
		// 创建url_connection
		java.net.URLConnection http_url_connection = null;
		try {
			http_url_connection = (new java.net.URL(url)).openConnection();
			java.net.HttpURLConnection HttpURLConnection = (java.net.HttpURLConnection) http_url_connection;// 将urlconnection类强转为httpurlconnection类
			HttpURLConnection.setDoInput(true);
			HttpURLConnection.setDoOutput(true);

			HttpURLConnection.setRequestMethod("DELETE");// 设置请求方式。可以是delete put  post get
			HttpURLConnection.setRequestProperty("Content-Type",
					"application/json");// 设置编码格式
			HttpURLConnection.setRequestProperty("Host", host);
			HttpURLConnection.setRequestProperty("api-key", apiKey);

			java.io.BufferedOutputStream output_stream = new java.io.BufferedOutputStream(
					HttpURLConnection.getOutputStream());
			output_stream.flush();
			output_stream.close();
			output_stream = null;
			java.io.InputStreamReader input_stream_reader = new java.io.InputStreamReader(
					HttpURLConnection.getInputStream(), "utf-8");
			java.io.BufferedReader buffered_reader = new java.io.BufferedReader(
					input_stream_reader);
			buffer = new StringBuffer();
			String line;
			while ((line = buffered_reader.readLine()) != null) {
				buffer.append(line);
			}
			line = null;
			input_stream_reader.close();
			buffered_reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	public static String get(String url) {
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Host", host);
			connection.setRequestProperty("user-agent", "Fiddler");
			connection.setRequestProperty("Content-Length", "0");
			connection.setConnectTimeout(100000);
			connection.setReadTimeout(100000);
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}

	public static String postTest(String strURL, String params) {
		try {
			URL url = new URL(strURL);// 创建连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开链接
			connection.setDoOutput(true);// 隐式设置请求方式为post
			connection.setDoInput(true);// 从httpUrlConnection读入，默认的情况下是true
			connection.setUseCaches(false);// post请求不能使用缓存
			connection.setInstanceFollowRedirects(true);// 是否连接遵循重定向
			connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
			connection.connect();// 实现连接
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");// 字符输出流，确认流的输出文件按照UTF—8的格式
			out.append(params);// 将params中的字符追加
			out.flush();// 在关闭流的时候，将内存中的数据一次性输出
			out.close();// 关闭流

			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			reader.close();
			// 断开连接
			connection.disconnect();
			String response = sb.toString();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "fail";

	}

	public String read() {
		// 获取当前想不运行的服务器地址下的webapps下的项目文件夹地址
		String path = this.getClass().getResource("/../../").getPath();
		path = path.replaceAll("%20", " ");
		// 将地址拼接成需要读取的字符串
		path = path.replace("/NBWeegServerNew", "") + "NBWeegServerNewUrl.txt";

		// 读取文件
		BufferedReader br = null;
		StringBuffer sb = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "GBK")); // 这里可以控制编码
			sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String data = new String(sb);

		return data;
	}

}
