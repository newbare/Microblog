package me.molchanoff.microblog.config;

import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * This class is used to configure ContextLoaderListener's application context
 */
@Configuration
@ComponentScan(basePackages = {"me.molchanoff.microblog"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class)})
@EnableJpaRepositories(basePackages = "me.molchanoff.microblog.repositories")
@EnableTransactionManagement
public class RootConfig {

    @Bean
    @Profile("production")
    public DataSource dataSourceProd() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/microblog");
        ds.setUsername("microblog");
        ds.setPassword("microblog");
        return ds;
    }

    @Bean
    @Profile("production")
    public JpaVendorAdapter jpaVendorAdapterProd() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.POSTGRESQL);
        adapter.setShowSql(false);
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
        return adapter;
    }

    @Bean
    @Profile("test")
    public DataSource dataSourceTest() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:testdata.sql")
                .build();
    }

    @Bean
    @Profile("test")
    public JpaVendorAdapter jpaVendorAdapterTest() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.H2);
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        return adapter;
    }

    @Bean
    @Profile("default")
    public DataSource dataSourceEmbedded() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
    }

    @Bean
    @Profile("default")
    public JpaVendorAdapter jpaVendorAdapterEmbedded() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.H2);
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        return adapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setJpaProperties(getHibernateProperties());
        emf.setDataSource(dataSource);
        emf.setPersistenceUnitName("microblog");
        emf.setJpaVendorAdapter(jpaVendorAdapter);
        emf.setPackagesToScan("me.molchanoff.microblog.model");
        return emf;
    }

    @Bean
    public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.connection.characterEncoding", "utf-8");
        properties.put("hibernate.connection.useUnicode", "true");
        properties.put("hibernate.connection.charSet", "UTF-8");
        return properties;
    }
}
