//package com.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.support.http.StatViewServlet;
//import com.alibaba.druid.support.http.WebStatFilter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.servlet.ServletRegistration;
//import javax.sql.DataSource;
//import java.util.Base64;
//import java.util.HashMap;
//
//@Configuration
//public class DruidConfig {
//
//    @ConfigurationProperties(prefix = "spring.datasource")
//    @Bean
//    public DataSource druidDataSource(){
//        return new DruidDataSource();
//    }
//
//
//    /**
//     * 后台监控
//     * @return
//     */
//    @Bean
//    public ServletRegistrationBean statViewServlet(){
//        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
//
//        //后台需要有人登陆，账号密码配置
//        HashMap<String, String> initParameters = new HashMap<>();
//
//        //增加配置
//        initParameters.put("loginUsername","admin");
//        initParameters.put("loginPassword","123456");
//
//        //允许谁能访问
//        initParameters.put("allow","");
//
//        bean.setInitParameters(initParameters);
//        return bean;
//
//    }
//
//    /**
//     * filter
//     * @return
//     */
//    @Bean
//    public FilterRegistrationBean webStatFilter(){
//        FilterRegistrationBean bean = new FilterRegistrationBean();
//
//        bean.setFilter(new WebStatFilter());
//
//        //设置 过滤请求
//        HashMap<String, String> map = new HashMap<>();
//        //这些不进行统计
//        map.put("exclusions","*.js,*.css,/durid/*");
//
//        bean.setInitParameters(map);
//        return bean;
//    }
//
//}
