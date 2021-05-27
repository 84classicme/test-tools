package com.example.feature;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="exceptions")
@Data
@Builder
public class ExceptionEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String service;
    private String exception;
    private String message;
    private String datetime;
    private String payload;
}
