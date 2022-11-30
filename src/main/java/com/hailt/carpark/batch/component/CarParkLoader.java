package com.hailt.carpark.batch.component;

import com.hailt.carpark.batch.dto.CarParkDTO;
import com.hailt.carpark.batch.listener.CarParkLoadCompleteListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CarParkLoader {

    private final JobLauncher jobLauncher;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ItemReader<CarParkDTO> carParkItemReader;
    private final ItemWriter<CarParkDTO> carParkItemWriter;
    private final CarParkLoadCompleteListener carParkLoadCompleteListener;

    public CarParkLoader(JobLauncher jobLauncher, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ItemReader<CarParkDTO> carParkItemReader, ItemWriter<CarParkDTO> carParkItemWriter, CarParkLoadCompleteListener carParkLoadCompleteListener) {
        this.jobLauncher = jobLauncher;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.carParkItemReader = carParkItemReader;
        this.carParkItemWriter = carParkItemWriter;
        this.carParkLoadCompleteListener = carParkLoadCompleteListener;
    }

    public JobExecution loadData() {
        log.info("Start loading car park data.");
        try {
            Map<String, JobParameter> parameters = new HashMap<>();
            parameters.put("launching-time", new JobParameter(new Date()));
            parameters.put("name", new JobParameter(this.getClass().getName()));
            return jobLauncher.run(buildJob(), new JobParameters(parameters));
        } catch (Exception e) {
            log.error("Failed to load car park data. {}", e.getMessage(), e);
        }
        return null;
    }

    private Job buildJob() {
        return jobBuilderFactory.get("loadCarParkData")
                .incrementer(new RunIdIncrementer())
                .listener(carParkLoadCompleteListener)
                .flow(fromCsvToDatabaseStep())
                .end()
                .build();
    }

    private Step fromCsvToDatabaseStep() {
        return stepBuilderFactory.get("loadCarParkStep")
                .<CarParkDTO, CarParkDTO>chunk(100)
                .reader(carParkItemReader)
                .writer(carParkItemWriter)
                .build();
    }
}
