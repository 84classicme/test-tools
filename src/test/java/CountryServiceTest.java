import com.generated.GetCountryRequest;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class CountryServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(8088),false);

    CountryService countryService;
    WebServiceWireMock wireMock;

    @Test
    public void testGetCountry() throws IOException {
        wireMock = new WebServiceWireMock();
        wireMock.mockGetCountryResponse("src/test/resources/CountryResponse.xml");
        GetCountryRequest request = TestUtils.getCountryRequestFromXml("src/test/resources/CountryRequest.xml");
        countryService = new CountryService();

        StepVerifier.create(countryService.getCountry(request))
            .expectNextMatches(response -> "NewTest".equals(response.getCountry().getName()))
            .verifyComplete();

    }


}
