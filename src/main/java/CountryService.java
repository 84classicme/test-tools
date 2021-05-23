import com.generated.CountriesPort;
import com.generated.CountriesPortService;
import com.generated.GetCountryRequest;
import com.generated.GetCountryResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.xml.ws.BindingProvider;

@Service
@Setter
public class CountryService {

    @Value( "${soap.country-service.endpoint-url}" )
    private String serviceEndpointUrl;

    public Mono<GetCountryResponse> getCountry(GetCountryRequest input) {
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
