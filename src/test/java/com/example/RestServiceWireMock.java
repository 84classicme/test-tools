package com.example;

import com.example.feature.CountryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import org.springframework.boot.test.context.TestComponent;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestComponent
public class RestServiceWireMock {

    public List<ServeEvent> getRequests(){
        return getAllServeEvents();
    }

    private final String REST_SERVICE_ENDPOINT = "/country/([A-Za-z]*)";

    public void mockGetCountryRest(final String pathToJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CountryRequest response = mapper.readValue(new File(pathToJson), CountryRequest.class);
        stubFor(get(urlMatching(REST_SERVICE_ENDPOINT))
            .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(mapper.writeValueAsString(response))));
    }

    public void mockThisStatusForRest(int status) throws IOException {
        stubFor(get(urlMatching(REST_SERVICE_ENDPOINT))
            .willReturn(aResponse().withStatus(status)));
    }

    public void mockEmptyResponseForRestResponse() throws IOException {
        stubFor(get(urlMatching(REST_SERVICE_ENDPOINT))
            .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));
    }

    public void mockMalformedResponseForRestResponse() throws IOException {
        stubFor(get(urlMatching(REST_SERVICE_ENDPOINT))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    }

    public void mockRandomDataForRestResponse() throws IOException {
        stubFor(get(urlMatching(REST_SERVICE_ENDPOINT))
            .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));
    }

    public void mockNoConnectionForRestResponse() throws IOException {
        stubFor(get(urlMatching(REST_SERVICE_ENDPOINT)).willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));
    }


}
