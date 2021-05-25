package com.example.feature;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ExceptionEventRepository extends ReactiveCrudRepository<ExceptionEvent, Long> {
}
