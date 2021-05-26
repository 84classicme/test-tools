package com.example.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ExceptionService {

    private ExceptionEventRepository exceptionEventRepository;

    @Autowired
    public ExceptionService(ExceptionEventRepository exceptionEventRepository){
        this.exceptionEventRepository = exceptionEventRepository;
    }

    public Mono<Void>  recordExceptionEvent(ExceptionEvent event){
        return exceptionEventRepository.save(event).then();
    }
}
