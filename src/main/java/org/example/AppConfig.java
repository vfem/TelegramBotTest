package org.example;

import org.h2.Driver;
import org.h2.tools.Server;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.sql.SQLException;
import java.util.Properties;

@Configuration
@ComponentScan
@EnableJpaRepositories
public class AppConfig {

	@Bean
	public DriverManagerDataSource botDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(Driver.class.getName());
		dataSource.setUrl("jdbc:h2:mem:testdb;create=true;DB_CLOSE_DELAY=-1");
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emFactory = new LocalContainerEntityManagerFactoryBean();
		emFactory.setDataSource(botDataSource());
		emFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		emFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		jpaProperties.setProperty("hibernate.hbm2ddl.auto", "create");
		emFactory.setJpaProperties(jpaProperties);
		emFactory.setPackagesToScan("org.example");
		return emFactory;
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}

}
