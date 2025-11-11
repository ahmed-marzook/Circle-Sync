package com.circlesync.circlesync.circlemodule.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Database configuration for Circle Module
 * This is the PRIMARY database configuration
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.circlesync.circlesync.circlemodule.repository",
        entityManagerFactoryRef = "circleEntityManagerFactory",
        transactionManagerRef = "circleTransactionManager"
)
public class CircleDatabaseConfig {

    /**
     * Primary DataSource for Circle Module
     * Reads configuration from spring.datasource.circle.*
     */
    @Primary
    @Bean(name = "circleDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.circle")
    public DataSource circleDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Primary EntityManagerFactory for Circle Module
     * Scans entities in com.circlesync.circlesync.circlemodule.entity package
     */
    @Primary
    @Bean(name = "circleEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean circleEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("circleDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.jdbc.batch_size", 20);
        properties.put("hibernate.order_inserts", true);
        properties.put("hibernate.order_updates", true);
        properties.put("hibernate.jdbc.time_zone", "UTC");

        return builder
                .dataSource(dataSource)
                .packages("com.circlesync.circlesync.circlemodule.entity")
                .persistenceUnit("circle")
                .properties(properties)
                .build();
    }

    /**
     * Primary TransactionManager for Circle Module
     */
    @Primary
    @Bean(name = "circleTransactionManager")
    public PlatformTransactionManager circleTransactionManager(
            @Qualifier("circleEntityManagerFactory") LocalContainerEntityManagerFactoryBean circleEntityManagerFactory) {
        return new JpaTransactionManager(circleEntityManagerFactory.getObject());
    }
}