package com.ljtao.springbootmiaosha.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import org.apache.catalina.manager.StatusManagerServlet;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
//扫描 Mapper 接口并容器管理
@MapperScan(basePackages = DruidConfig.PACKAGE, sqlSessionFactoryRef = "houseSqlSessionFactory")
public class DruidConfig {

    // 精确到 master 目录，以便跟其他数据源隔离
    static final String PACKAGE = "com.ljtao.springbootmiaosha.dao";
    static final String MAPPER_LOCATION = "classpath:mapper/*.xml";

    @Value("${miaosha.datasource.url}")
    private String url;

    @Value("${miaosha.datasource.username}")
    private String user;

    @Value("${miaosha.datasource.password}")
    private String password;

    @Value("${miaosha.datasource.driverClassName}")
    private String driverClass;

    @Bean(name = "houseDataSource")
    @Primary
    public DataSource houseDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setProxyFilters(Lists.newArrayList(statFilter()));
        return dataSource;
    }
    @Bean
    public Filter statFilter(){
        StatFilter statFilter=new StatFilter();
        statFilter.setSlowSqlMillis(1);
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(true);
        return statFilter;
    }

    @Bean(name = "houseTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(houseDataSource());
    }

    @Bean(name = "houseSqlSessionFactory")
    @Primary
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("houseDataSource") DataSource houseDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(houseDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(DruidConfig.MAPPER_LOCATION));
        //开启驼峰命名转换
        sessionFactory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return sessionFactory.getObject();
    }
    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        //http://localhost:8081/druid
        return new ServletRegistrationBean(new StatusManagerServlet(),"/druid/*");
    }
}