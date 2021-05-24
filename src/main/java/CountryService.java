import com.generated.CountriesPort;
import com.generated.CountriesPortService;
import com.generated.GetCountryRequest;
import com.generated.GetCountryResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.xml.ws.BindingProvider;
import java.time.Duration;

@Service
@Setter
public class CountryService {

    @Value( "${soap.country-service.endpoint-url}" )
    private String serviceEndpointUrl;

    private WebClientConfig webClientConfig;

    public Mono<Country> getCountryFromRestService(Country input) {
        webClientConfig = new WebClientConfig();
        WebClient webClient = webClientConfig.reactiveRestClient();
        return webClient.get()
                .uri(serviceEndpointUrl+"/{name}", input.getName())
                // exchange() does not throw exceptions in case of 4xx or 5xx responses, retrieve does so handle
                // them explicitly below.
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                    Mono.error( new ClientException("CLIENT EXCEPTION in CountryService.", response.rawStatusCode())) )
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    System.err.println("EXCEPTION in CountryService. Server returned code: " + response.rawStatusCode());
                    return Mono.error( new ServiceException("EXTERNAL SERVICE EXCEPTION in CountryService.", response.rawStatusCode())); })
                .bodyToMono(Country.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                    .filter(throwable -> throwable instanceof ServiceException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                        throw new ApplicationException("External Service failed to process after max retries.");
                    }))
                .onErrorResume(ClientException.class, clientException ->
                    this.handleClientException(clientException)
                        .then(Mono.error( new ApplicationException("Cannot process CountryService.getCountry due to client error.", clientException))));

    }

    private Mono<Void> handleClientException(ClientException e){
        System.out.println("Handling client exception in CountryService.");
        return Mono.empty();
    }

    public Mono<GetCountryResponse> getCountryFromWebService(GetCountryRequest input) {
       return Mono.create(sink -> getPortType().getCountryAsync(input, ReactorAsyncHandler.into(sink)));
    }

    private CountriesPort getPortType(){
        CountriesPortService service = new CountriesPortService();
        CountriesPort portType = service.getCountriesPortSoap11();
        BindingProvider bp = (BindingProvider)portType;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpointUrl);
        return portType;
    }
}
