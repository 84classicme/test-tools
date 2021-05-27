package com.example.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class CountryController {

    @Autowired
    private CountryService countryService;

//    @Autowired
//    public CountryController(CountryService countryService){
//        this.countryService = countryService;
//    }

    @GetMapping(value = "/country/{name}", produces = "application/json")
    public @ResponseBody Country getCountry(
        @PathVariable String name){
        return countryService.getCountryFromLocal(name);
    }

    @PostMapping(value = "/country",consumes = "application/json",produces = "application/json")
    public @ResponseBody Country newCountry(@RequestBody CountryRequest countryRequest){
        Country c = (Country)countryRequest;
        return countryService.saveCountryToLocal(c);
    }

    @PutMapping(
        value = "/country/{id}",
        consumes = "application/json",
        produces = "application/json")
    public @ResponseBody Country editCountry(@RequestBody Country country, @PathVariable Long id){
        country.setId(id);
        return countryService.saveCountryToLocal(country);
    }

    @DeleteMapping(
        value = "/country/{id}")
    public void deleteCountry(@PathVariable Long id){
            //return countryService.deleteCountryFromLocal(id);
    }
}
