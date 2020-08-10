package indi.ipan.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;


@Configuration
public class DruidConfig implements TransactionManagementConfigurer {
    // 多个事务管理器的情况下，用名字注入
    @Resource(name="txManager1")
    private PlatformTransactionManager txManager1;
    @Bean(name = "druiDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druiDataSource() {
        return new DruidDataSource();
    }
    // 自己用@Bean写的会覆盖系统默认，从jpa改成jdbc了
    @Bean(name = "txManager1")
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    // 设置默认的事务管理器
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager1;
    }
    // 配置 Druid 监控管理后台的Servlet；
    // 内置 Servler 容器时没有web.xml文件，所以使用 SpringBoot的注册 Servlet 方式
    @Bean
    public ServletRegistrationBean statViewServlet() {
        // 创建Servlet注册实体
        // /druid/*：后台访问的路径
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "admin"); //后台管理界面的登录账号
        initParams.put("loginPassword", "123456"); //后台管理界面的登录密码

        //后台允许谁可以访问
        initParams.put("allow", "127.0.0.1");//表示只有本机可以访问
        //initParams.put("allow", "")：为空或者为null时，表示允许所有访问
        //deny：Druid 后台拒绝谁访问
        //initParams.put("deny", "192.168.1.20");表示禁止此ip访问

        //设置初始化参数
        bean.setInitParameters(initParams);
        return bean;
    }
}
