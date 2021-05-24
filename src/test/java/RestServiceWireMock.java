import com.generated.GetCountryResponse;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import org.springframework.boot.test.context.TestComponent;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestComponent
public class RestServiceWireMock {

    public List<ServeEvent> getRequests(){
        return getAllServeEvents();
    }

    public void mockGetCountryResponse(final String pathToXml) throws IOException {
        String response = "";
        //GetCountryResponse fromXmlSoapObject = TestUtils.getCountryResponseFromXml(pathToXml);
        //response = serializeObject(fromXmlSoapObject);
        //stubFor(post(urlPathMatching(SERVICE_ENDPOINT))
        //    .willReturn(aResponse().withHeader("Content-Type", "text/xml").withBody(response)));
    }
}
