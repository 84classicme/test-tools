package com.example.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class CountryService {

    @Value( "${soap.country-service.endpoint-url}" )
    private String webserviceEndpointUrl;

    @Value( "${rest.country-service.endpoint-url}" )
    private String restEndpointUrl;

    private CountryRepository countryRepository;

    private ExceptionService exceptionService;


    @Autowired
    public CountryService(CountryRepository countryRepository, ExceptionService exceptionService){
        this.countryRepository = countryRepository;
        this.exceptionService = exceptionService;
    }

    public Country getCountryFromLocal(String name){
        CountryDto dto = countryRepository.findByName(name);
        return CountryMapper.mapDtoToCountry(dto);
        //return null;
    }

    public Country saveCountryToLocal(Country country){
        CountryDto dto = CountryMapper.mapCountryToDto(country);
       // return countryRepository.save(dto).map(CountryMapper::mapDtoToCountry);
        return null;
    }

    public void deleteCountryFromLocal(Long id){
        //return countryRepository.deleteById(id).onErrorResume(Exception.class, e -> Mono.empty());
    }

//    public void getCountryFromWebService(GetCountryRequest input) {
//       //return Mono.create(sink -> getPortType().getCountryAsync(input, ReactorAsyncHandler.into(sink)));
//    }

    private void handleClientException(ClientException e, CountryRequest input){
        System.out.println("Handling client exception in CountryService.");
        //return recordException(e, input.toString());
    }

    private void handleServiceException(ServiceException e, CountryRequest input){
        System.out.println("Handling service exception in CountryService.");
        //return recordException(e, input.toString());
    }

    private void recordException(Exception e, String payload){
        System.out.println("Recording exception in CountryService.");
        //return exceptionService.recordExceptionEvent(buildExceptionEvent(e, payload));
    }

    private ExceptionEvent buildExceptionEvent(Exception e, String payload){
        return ExceptionEvent.builder()
            .message(e.getMessage())
            .service("CountryService")
            .exception(e.getClass().getSimpleName())
            .payload(payload)
            .datetime(ZonedDateTime.now(ZoneOffset.UTC).toString()) //UTC timestamp as string
            .build();
    }

}
