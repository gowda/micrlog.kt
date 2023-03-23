package org.kanur.microlog.configs

import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.boot.context.properties.ConfigurationProperties
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.orm.jpa.JpaTransactionManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
  entityManagerFactoryRef = "primaryEntityManagerFactory",
  transactionManagerRef = "primaryTransactionManager",
  basePackages = ["org.kanur.microlog"]
)
class DataSourceConfiguration {
  @Primary
  @Bean(name = ["primaryAppDataSourceProperties"])
  @ConfigurationProperties("spring.datasource")
  fun primaryAppDataSourceProperties(): DataSourceProperties {
    return DataSourceProperties()
  }

  @Primary
  @Bean(name = ["primaryAppDataSource"])
  @ConfigurationProperties("spring.datasource.configuration")
  fun primaryAppDataSource(@Qualifier("primaryAppDataSourceProperties") primaryAppDataSourceProperties: DataSourceProperties): DataSource {
    return primaryAppDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
  }

  @Primary
  @Bean(name = ["primaryEntityManagerFactory"])
  fun primaryEntityManagerFactory(
    primaryEntityManagerFactoryBuilder: EntityManagerFactoryBuilder,
    @Qualifier("primaryAppDataSource") primaryAppDataSource: DataSource?
  ): LocalContainerEntityManagerFactoryBean {
    return primaryEntityManagerFactoryBuilder
      .dataSource(primaryAppDataSource)
      .packages("org.kanur.microlog.components")
      .persistenceUnit("primaryAppDataSource")
      .build()
  }

  @Primary
  @Bean(name = ["primaryTransactionManager"])
  fun primaryTransactionManager(
    @Qualifier("primaryEntityManagerFactory") primaryEntityManagerFactory: EntityManagerFactory?
  ): PlatformTransactionManager {
    return JpaTransactionManager(primaryEntityManagerFactory!!)
  }
}
