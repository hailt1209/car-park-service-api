package com.hailt.carpark.batch.configuration;

import com.hailt.carpark.batch.component.QueryBuilder;
import com.hailt.carpark.batch.dto.CarParkAvailabilityDTO;
import com.hailt.carpark.batch.dto.CarParkDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Scanner;

@Configuration
@Slf4j
public class BatchJobConfig {

    private static final String CSV_DELIMITER = ",";

    private final DataSource dataSource;
    private final QueryBuilder queryBuilder;

    public BatchJobConfig(DataSource dataSource, QueryBuilder queryBuilder) {
        this.dataSource = dataSource;
        this.queryBuilder = queryBuilder;
    }

    @Value("classpath:static/hdb-carpark-information.csv")
    Resource carParkResource;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ItemReader<CarParkDTO> carParkItemReader() throws IOException {

        String header = getHeader(carParkResource);

        FlatFileItemReader<CarParkDTO> reader = new FlatFileItemReader<>();
        reader.setResource(carParkResource);
        reader.setLineMapper(getLineMapper(header));
        reader.setLinesToSkip(1);
        return reader;
    }

    @Bean
    public ItemWriter<CarParkDTO> carParkItemWriter() {
        JdbcBatchItemWriter<CarParkDTO> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql(queryBuilder.getCarParkInsertQuery());
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public ItemWriter<CarParkAvailabilityDTO> carParkAvailabilityItemWriter() {
        JdbcBatchItemWriter<CarParkAvailabilityDTO> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql(queryBuilder.getCarParkAvailabilityUpsertQuery());
        writer.setDataSource(dataSource);
        writer.setAssertUpdates(false);
        return writer;
    }

    private String getHeader(Resource resource) throws IOException {
        Scanner scanner = new Scanner(resource.getInputStream());
        String line = scanner.nextLine();
        scanner.close();
        return line;
    }

    private DefaultLineMapper<CarParkDTO> getLineMapper(String header) {
        return new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(header.split(CSV_DELIMITER));
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(CarParkDTO.class);
            }});
        }};
    }
}
