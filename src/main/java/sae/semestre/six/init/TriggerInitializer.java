/*
 * TriggerInitializer.java                                 22 May 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class TriggerInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public TriggerInitializer(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String triggerName = "prevent_bill_deletion";
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TRIGGERS WHERE TRIGGER_NAME = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, triggerName);

        if (count != null && count == 0) {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.setSeparator("@@");
            populator.addScript(new ClassPathResource("triggers.sql"));
            populator.execute(dataSource);
        }
    }
}

