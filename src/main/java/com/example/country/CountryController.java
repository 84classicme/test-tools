package com.example.country;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class CountryController {

    private CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService){
        this.countryService = countryService;
    }

    @GetMapping(
        value = "/country/{name}",
        produces = "application/json")
    @ApiOperation(
        value = "getCountry",
        response = CountryRequest.class,
        produces = "application/json")
    @ApiResponses(
        @ApiResponse(code = 200, message = "OK",
            examples = @Example(@ExampleProperty(mediaType = "application/json",
                value = "{\"id\": 12345," +
                    "\"name\": \"Utopia\"," +
                    "\"capital\": \"Ritehere\"," +
                    "\"population\": 1," +
                    "\"currency\": \"MGB\"}"))))
    public @ResponseBody Mono<Country> getCountry(
        @ApiParam(
            name = "name",
            type = "String",
            value = "Name of the country to fetch data about",
            example = "Utopia")
        @PathVariable String name){
        return countryService.getCountryFromLocal(name);
    }

    @PostMapping(
        value = "/country",
        consumes = "application/json",
        produces = "application/json")
    @ApiOperation(
        value = "newCountry",
        response = CountryRequest.class,
        consumes = "application/json",
        produces = "application/json")
    @ApiResponses(
        @ApiResponse(code = 201, message = "Created.",
            examples = @Example(@ExampleProperty(mediaType = "application/json",
                value = "{\"id\": 12345," +
                    "\"name\": \"Utopia\"," +
                    "\"capital\": \"Ritehere\"," +
                    "\"population\": 1," +
                    "\"currency\": \"MGB\"}"))))
    @ApiImplicitParams({ @ApiImplicitParam(name = "country",
        value = "Country to add", paramType = "body", dataType = "com.example.feature.Country") })
    public @ResponseBody Mono<Country> newCountry(@RequestBody CountryRequest countryRequest){
        Country c = (Country)countryRequest;
        return countryService.saveCountryToLocal(c);
    }

    @PutMapping(
        value = "/country/{id}",
        consumes = "application/json",
        produces = "application/json")
    @ApiOperation(
        value = "editCountry",
        response = CountryRequest.class,
        consumes = "application/json",
        produces = "application/json")
    @ApiResponses(
        @ApiResponse(code = 200, message = "Updated.",
            examples = @Example(@ExampleProperty(mediaType = "application/json",
                value = "{\"id\": 12345," +
                    "\"name\": \"Utopia\"," +
                    "\"capital\": \"Ritehere\"," +
                    "\"population\": 1," +
                    "\"currency\": \"MGB\"}"))))
    @ApiImplicitParams({ @ApiImplicitParam(name = "country",
        value = "Country to update", paramType = "body", dataType = "com.example.feature.Country") })
    public @ResponseBody Mono<Country> editCountry(
        @RequestBody Country country,
        @ApiParam(
            name = "id",
            type = "Long",
            value = "Id of the country to update.",
            example = "1234")
        @PathVariable Long id){
        country.setId(id);
        return countryService.saveCountryToLocal(country);
    }

    @DeleteMapping(
        value = "/country/{id}")
    @ApiOperation(
        value = "deleteCountry")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @ApiResponses(
        @ApiResponse(code = 204, message = "Success. No content."))
    public Mono<Void> deleteCountry(
        @ApiParam(
            name = "id",
            type = "Long",
            value = "Id of the country to delete.",
            example = "1234")
        @PathVariable Long id){
            return countryService.deleteCountryFromLocal(id);
    }
}
