import org.junit.Before;
import org.junit.Rule;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@TestPropertySource(properties = {"service.wsdl.url=localhost:8080?url"})
public class AccountServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(8088),false);


    private String wsdlUrl = "";
    private String endpointUrl = "";
}
