package com.example.feature;

import com.generated.CountriesPort;
import com.generated.CountriesPortService;
import com.generated.GetCountryRequest;
import com.generated.GetCountryResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.xml.ws.BindingProvider;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
@Setter
public class CountryService {

    @Value( "${soap.country-service.endpoint-url}" )
    private String serviceEndpointUrl;

    @Autowired
    ExceptionService exceptionService;

    private WebClientConfig webClientConfig;

    public Mono<Country> getCountryFromRestService(Country input) {
        webClientConfig = new WebClientConfig();
        WebClient reactiveRestClient = webClientConfig.getReactiveRestClient();
        return reactiveRestClient.get()
                .uri(serviceEndpointUrl+"/{name}", input.getName())
                .retrieve()
                // exchange() does not throw exceptions in case of 4xx or 5xx responses,
                // retrieve does so handle them explicitly.
                .onStatus(HttpStatus::is4xxClientError, response ->
                    Mono.error(
                        new ClientException(
                            "CLIENT EXCEPTION in com.example.feature.CountryService.",
                            response.rawStatusCode())) )
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    System.err.println("EXCEPTION in com.example.feature.CountryService. Server returned code: " + response.rawStatusCode());
                    return Mono.error( new ServiceException(
                                            "EXTERNAL SERVICE EXCEPTION in com.example.feature.CountryService.",
                                            response.rawStatusCode())); })
                .bodyToMono(Country.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                    .filter(throwable -> throwable instanceof ServiceException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                        ServiceException e = new ServiceException(
                                                "External Service failed to process after max retries.",
                                                500);
                        this.handleServiceException(e, input);
                        throw e;
                    }))
                .onErrorResume(ClientException.class, clientException ->
                    this.handleClientException(clientException, input)
                        .then(Mono.error(
                                new ApplicationException(
                                        "Cannot process com.example.feature.CountryService.getCountry due to client error.",
                                        clientException))));
    }

    public Mono<GetCountryResponse> getCountryFromWebService(GetCountryRequest input) {
       return Mono.create(sink -> getPortType().getCountryAsync(input, ReactorAsyncHandler.into(sink)));
    }

    private Mono<Void> handleClientException(ClientException e, Country input){
        System.out.println("Handling client exception in com.example.feature.CountryService.");
        return this.recordException(e, input.toString());
    }

    private Mono<Void> handleServiceException(ServiceException e, Country input){
        System.out.println("Handling client exception in com.example.feature.CountryService.");
        return this.recordException(e, input.toString()).then();
    }

    private Mono<Void> recordException(Exception e, String payload){
        System.out.println("Handling client exception in com.example.feature.CountryService.");
        return exceptionService.recordExceptionEvent(buildExceptionEvent(e, payload)).then();
    }

    private ExceptionEvent buildExceptionEvent(Exception e, String payload){
        return ExceptionEvent.builder()
            .message(e.getMessage())
            .service("com.example.feature.CountryService")
            .exception(e.getClass().getSimpleName())
            .payload(payload)
            .timestamp(ZonedDateTime.now(ZoneOffset.UTC).toString()) //UTC timestamp as string
            .build();
    }

    private CountriesPort getPortType(){
        CountriesPortService service = new CountriesPortService();
        CountriesPort portType = service.getCountriesPortSoap11();
        BindingProvider bp = (BindingProvider)portType;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpointUrl);
        return portType;
    }
}
