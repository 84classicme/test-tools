import com.generated.CountriesPort;
import com.generated.CountriesPortService;
import com.generated.GetCountryRequest;
import com.generated.GetCountryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.xml.ws.BindingProvider;

@Service
public class CountryService {

    @Value( "${soap.country-service.endpoint-url}" )
    private String SERVICE_ENDPOINT_URL = "http://localhost:8088/getCountry";

    public Mono<GetCountryResponse> getCountry(GetCountryRequest input) {
       return Mono.create(sink -> getPortType().getCountryAsync(input, ReactorAsyncHandler.into(sink)));
    }

    private CountriesPort getPortType(){
        CountriesPortService service = new CountriesPortService();
        CountriesPort portType = service.getCountriesPortSoap11();
        BindingProvider bp = (BindingProvider)portType;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, SERVICE_ENDPOINT_URL);
        return portType;
    }
}
