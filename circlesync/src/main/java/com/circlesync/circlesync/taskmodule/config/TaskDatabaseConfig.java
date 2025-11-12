package com.circlesync.circlesync.taskmodule.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Database configuration for Task Module
 * This is a secondary database configuration
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.circlesync.circlesync.taskmodule.repository",
        entityManagerFactoryRef = "taskEntityManagerFactory",
        transactionManagerRef = "taskTransactionManager"
)
public class TaskDatabaseConfig {

    /**
     * DataSource for Task Module
     * Reads configuration from spring.datasource.task.*
     */
    @Bean(name = "taskDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.task")
    public DataSource taskDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * EntityManagerFactory for Task Module
     * Scans entities in com.circlesync.circlesync.taskmodule.entity package
     */
    @Bean(name = "taskEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean taskEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("taskDataSource") DataSource dataSource) {

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
                .packages("com.circlesync.circlesync.taskmodule.entity")
                .persistenceUnit("task")
                .properties(properties)
                .build();
    }

    /**
     * TransactionManager for Task Module
     */
    @Bean(name = "taskTransactionManager")
    public PlatformTransactionManager taskTransactionManager(
            @Qualifier("taskEntityManagerFactory") LocalContainerEntityManagerFactoryBean taskEntityManagerFactory) {
        return new JpaTransactionManager(taskEntityManagerFactory.getObject());
    }
}
