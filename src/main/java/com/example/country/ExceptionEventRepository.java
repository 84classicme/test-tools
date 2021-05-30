package com.example.country;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ExceptionEventRepository extends ReactiveCrudRepository<ExceptionEvent, Long> {
}
