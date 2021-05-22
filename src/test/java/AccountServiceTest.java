import org.junit.Rule;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;


public class AccountServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(8088),false);
}
