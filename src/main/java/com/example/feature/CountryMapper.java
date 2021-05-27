package com.example.feature;

public class CountryMapper {
    public static Country mapDtoToCountry(CountryDto dto){
        if(dto == null)dto = new CountryDto();
       return Country.builder()
           .name(dto.getName())
           .capital(dto.getCapital())
           .currency(dto.getCurrency())
           .population(dto.getPopulation())
           .build();
    }

    public static CountryDto mapCountryToDto(Country c){
        return CountryDto.builder()
            .name(c.getName())
            .capital(c.getCapital())
            .currency(c.getCurrency())
            .population(c.getPopulation())
            .build();
    }
}
