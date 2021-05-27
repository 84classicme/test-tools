package com.example.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExceptionService {

    private ExceptionEventRepository exceptionEventRepository;

    @Autowired
    public ExceptionService(ExceptionEventRepository exceptionEventRepository){
        this.exceptionEventRepository = exceptionEventRepository;
    }

    public void recordExceptionEvent(ExceptionEvent event){
       // return exceptionEventRepository.save(event);
    }
}
