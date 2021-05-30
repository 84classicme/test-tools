package com.example.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("country")
public class CountryDto {
    @Id
    private long id;
    private String name;
    private String capital;
    private int population;
    private String currency;
}
