package com.weeg.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * 
* @ClassName: HttpResponse
* @Description: 统一回复内容
* @author yuyan
* @date 2019年11月12日
 */
public class HttpResponse {
	public void Response(HttpServletResponse response, String str) throws IOException {
		response.setHeader("content-type", "text/html;charset=UTF-8");// 设置响应头部,设置主体的编码格式是UTF-8
		response.setCharacterEncoding("UTF-8");// 设置传输的编码格式
		Writer writer = response.getWriter();
		writer.write(str);// 将 字符串内容写入缓存
		writer.flush();// 将缓存输出
		writer.close();
	}
}
