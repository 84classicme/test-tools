package com.example.country;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("exceptions")
@Data
@Builder
public class ExceptionEvent {
    @Id
    private Long id;
    private String service;
    private String exception;
    private String message;
    private String datetime;
    private String payload;
}
