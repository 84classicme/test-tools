package com.example.feature;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Country {
    String name;
    int population;
    String capital;
    String currency;
}
