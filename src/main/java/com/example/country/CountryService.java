package com.example.country;

import com.example.WebClientConfig;
import com.generated.CountriesPort;
import com.generated.CountriesPortService;
import com.generated.GetCountryRequest;
import com.generated.GetCountryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.xml.ws.BindingProvider;
import java.time.Duration;
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

    private WebClientConfig webClientConfig;

    @Autowired
    public CountryService(CountryRepository countryRepository, ExceptionService exceptionService){
        this.countryRepository = countryRepository;
        this.exceptionService = exceptionService;
    }

    public Mono<CountryRequest> getCountryFromRestService(CountryRequest input) {
        webClientConfig = new WebClientConfig();
        WebClient reactiveRestClient = webClientConfig.getReactiveRestClient();
        return reactiveRestClient.get()
                .uri(this.restEndpointUrl+"/{name}", input.getName())
                .exchangeToMono(response -> {
                    if(response.rawStatusCode() == 200){
                        return response.bodyToMono(CountryRequest.class);
                    }
                    else if (response.statusCode().is4xxClientError()){
                        return Mono.error(
                            new ClientException(
                                "CLIENT EXCEPTION in CountryService.",
                                response.rawStatusCode())) ;
                    } else if (response.statusCode().is5xxServerError()){
                        return Mono.error(
                            new ServiceException(
                                "EXTERNAL SERVICE EXCEPTION in CountryService.",
                                response.rawStatusCode()));
                    } else {
                        return Mono.error( new ServiceException(
                            "SERVICE EXCEPTION in CountryService. Unexpected response. Retrying...",
                            response.rawStatusCode()));
                    }})
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                    .filter(throwable -> !(throwable instanceof ClientException))
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                        ServiceException e =
                            new ServiceException(
                                "External Service failed to process after max retries.",
                                500);
                        handleServiceException(e, input);
                        throw e;
                    }))
                .onErrorResume(ClientException.class, clientException ->
                    handleClientException(clientException, input)
                        .then(Mono.error(
                            new ApplicationException(
                                    "Cannot process CountryService.getCountry due to client error.",
                                    clientException))));
    }

    public Mono<Country> getCountryFromLocal(String name){
        return countryRepository.findByName(name).map(CountryMapper::mapDtoToCountry);
    }

    public Mono<Country> saveCountryToLocal(Country country){
        CountryDto dto = CountryMapper.mapCountryToDto(country);
        return countryRepository.save(dto).map(CountryMapper::mapDtoToCountry);
    }

    public Mono<Void> deleteCountryFromLocal(Long id){
        return countryRepository.deleteById(id).onErrorResume(Exception.class, e -> Mono.empty());
    }

    public Mono<GetCountryResponse> getCountryFromWebService(GetCountryRequest input) {
       return Mono.create(sink -> getPortType().getCountryAsync(input, ReactorAsyncHandler.into(sink)));
    }

    private Mono<Void> handleClientException(ClientException e, CountryRequest input){
        System.out.println("Handling client exception in CountryService.");
        return recordException(e, input.toString());
    }

    private Mono<Void> handleServiceException(ServiceException e, CountryRequest input){
        System.out.println("Handling service exception in CountryService.");
        return recordException(e, input.toString());
    }

    private Mono<Void> recordException(Exception e, String payload){
        System.out.println("Recording exception in CountryService.");
        return exceptionService.recordExceptionEvent(buildExceptionEvent(e, payload));
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

    private CountriesPort getPortType(){
        CountriesPortService service = new CountriesPortService();
        CountriesPort portType = service.getCountriesPortSoap11();
        BindingProvider bp = (BindingProvider)portType;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, webserviceEndpointUrl);
        return portType;
    }
}
