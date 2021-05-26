package com.example.feature;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CountryRepository extends ReactiveCrudRepository<Country, Long> {

    @Query("SELECT * FROM country WHERE name = :name")
    Mono<Country> findByName(String name);
}
