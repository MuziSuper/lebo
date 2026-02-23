package cn.muzisheng.lebo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * 数据源配置类
 * 从环境变量中读取数据库配置
 */
@Configuration
public class DataSourceConfig {

    private final Environment environment;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * 配置数据源
     * 只配置数据库地址、用户名、密码
     */
    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        
        // 从环境变量读取配置
        String url = environment.getProperty("DATASOURCE_URL");
        String username = environment.getProperty("DATASOURCE_USERNAME");
        String password = environment.getProperty("DATASOURCE_PASSWORD");
        
        // 验证必需的配置项
        if (url == null || username == null || password == null) {
            throw new IllegalArgumentException("数据库配置不完整，请设置 DATASOURCE_URL、DATASOURCE_USERNAME、DATASOURCE_PASSWORD 环境变量");
        }
        
        // 设置数据库连接配置
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        return dataSource;
    }
}
