package cn.muzisheng.lebo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

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
    /**
     * 配置 EntityManagerFactory
     * 显式设置 Hibernate 方言
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("cn.muzisheng.lebo.entity"); // 根据您的实际包名调整
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties properties = new Properties();
        // 显式设置 MySQL 方言，避免元数据查询失败
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.show_sql", "true");

        em.setJpaProperties(properties);

        return em;
    }
}
