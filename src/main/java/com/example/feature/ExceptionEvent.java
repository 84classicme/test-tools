package com.example.feature;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("exceptions")
@Data
@Builder
public class ExceptionEvent {
    @Id
    private Long id;

    @Column
    private String service;

    @Column
    private String exception;

    @Column
    private String message;

    @Column
    private String timestamp;

    @Column
    private String payload;
}
