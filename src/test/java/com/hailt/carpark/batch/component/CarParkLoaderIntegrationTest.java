package com.hailt.carpark.batch.component;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class CarParkLoaderIntegrationTest {

    @Autowired
    private CarParkLoader carParkLoader;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After
    public void teardown() {
        jdbcTemplate.update("DELETE FROM car_park_availability");
        jdbcTemplate.update("DELETE FROM car_park");
    }

    @Test
    public void loadData_whenJobExecuted_thenSuccess() {
        JobExecution jobExecution = carParkLoader.loadData();

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }
}
