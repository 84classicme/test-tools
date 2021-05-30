package com.example.country;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CountryRepository extends ReactiveCrudRepository<CountryDto, Long> {

    @Query("SELECT * FROM country WHERE name = :name")
    Mono<CountryDto> findByName(String name);
}
