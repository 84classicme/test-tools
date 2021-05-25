package com.example.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ExceptionService {

    @Autowired
    ExceptionEventRepository exceptionEventRepository;

    public Mono<Void>  recordExceptionEvent(ExceptionEvent event){
        return exceptionEventRepository.save(event).then();
    }
}
