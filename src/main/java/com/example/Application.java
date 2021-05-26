package com.example;

import com.example.feature.Country;
import com.example.feature.CountryRepository;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.time.Duration;

@SpringBootApplication
@EnableR2dbcRepositories
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    // Initialiaze the db by running schema.sql
    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));

        return initializer;
    }

    @Bean
    public CommandLineRunner demo(CountryRepository repository) {

        return (args) -> {
            // save a few customers
            repository.save(Country.builder().name("Utopia").capital("Ritehere").population(1).currency("MGB").build())
                .block(Duration.ofSeconds(10));

        };
    }
}
