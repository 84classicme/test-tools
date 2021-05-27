package com.example.feature;


import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<CountryDto, Long> {

    CountryDto findByName(String name);
}
