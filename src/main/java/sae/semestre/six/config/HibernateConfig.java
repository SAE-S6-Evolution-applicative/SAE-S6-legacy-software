package sae.semestre.six.config;

        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.jdbc.datasource.DriverManagerDataSource;
        import org.springframework.orm.jpa.JpaTransactionManager;
        import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
        import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
        import org.springframework.transaction.PlatformTransactionManager;
        import org.springframework.transaction.annotation.EnableTransactionManagement;

        import javax.sql.DataSource;
        import java.util.Properties;

        @Configuration
        @EnableTransactionManagement
        public class HibernateConfig {

            @Bean
            public DataSource dataSource() {
                DriverManagerDataSource dataSource = new DriverManagerDataSource();
                dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                dataSource.setUrl("jdbc:mysql://localhost:7878/hospital_db?useSSL=false");
                dataSource.setUsername("root");
                dataSource.setPassword("root");
                return dataSource;
            }

            @Bean
            public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
                LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
                factoryBean.setDataSource(dataSource());
                factoryBean.setPackagesToScan("sae.semestre.six.entity");
                factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

                Properties properties = new Properties();
                properties.setProperty("hibernate.hbm2ddl.auto", "update");
                properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");

                factoryBean.setJpaProperties(properties);
                return factoryBean;
            }

            @Bean
            public PlatformTransactionManager transactionManager() {
                JpaTransactionManager transactionManager = new JpaTransactionManager();
                transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
                return transactionManager;
            }
        }