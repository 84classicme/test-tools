package com.example.feature;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CountryController {

    @Autowired
    CountryRepository countryRepository;

    @GetMapping(
        value = "/country/{name}",
        produces = "application/json")
    @ApiOperation(
        value = "getCountry",
        response = Country.class,
        consumes = "application/json")
    @ResponseBody
    public Mono<Country> getCountry(
        @ApiParam(
            name = "name",
            type = "String",
            value = "Name of the country to fetch data about",
            example = "Utopia")
        @PathVariable String name){
        return countryRepository.findByName(name);
    }
}
