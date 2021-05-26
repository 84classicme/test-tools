package com.example.feature;

import com.example.RestServiceWireMock;
import com.example.TestUtils;
import com.example.WebServiceWireMock;
import com.generated.GetCountryRequest;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.ArgumentMatchers.any;

public class CountryServiceTest {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(options().port(8088),false);

    CountryService countryService;
    WebServiceWireMock wsWireMock;
    RestServiceWireMock restWireMock;
    ExceptionService exceptionServiceMock;

    private static final String WEBSERVICE_URL = "http://localhost:8088/getCountry";
    private static final String REST_SERVICE_URL = "http://localhost:8088/country";
    private static final String MAX_RETRY_MESSAGE = "External Service failed to process after max retries.";

    @Before
    public void setup(){
        exceptionServiceMock = Mockito.mock(ExceptionService.class);
        countryService = new CountryService(exceptionServiceMock);
        ReflectionTestUtils.setField(countryService, "webserviceEndpointUrl", WEBSERVICE_URL);
        ReflectionTestUtils.setField(countryService, "restEndpointUrl", REST_SERVICE_URL);
    }

    @Test
    public void testGetCountryFromWS() throws IOException {
        wsWireMock = new WebServiceWireMock();
        wsWireMock.mockGetCountrySoapResponse("src/test/resources/xml/CountryResponse.xml");
        GetCountryRequest request = TestUtils.getCountryRequestFromXml("src/test/resources/xml/CountryRequest.xml");

        StepVerifier.create(countryService.getCountryFromWebService(request))
            .expectNextMatches(response -> "NewTest".equals(response.getCountry().getName()))
            .verifyComplete();
    }

    @Test
    public void testGetCountryFromRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockGetCountryRest("src/test/resources/json/country.json");
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectNextMatches(response -> "MyCountry".equals(response.getName()) && response.getPopulation() == 1)
            .verifyComplete();
    }

    @Test
    public void testHandle4xxFromRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockThisStatusForRest(400);
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");
        Mockito.when(exceptionServiceMock.recordExceptionEvent(any(ExceptionEvent.class))).thenReturn(Mono.empty());

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(t -> t.getMessage().equals("Cannot process CountryService.getCountry due to client error.") &&
                t.getSuppressed()[0] instanceof ClientException &&
                t.getSuppressed()[0].getMessage().equals("CLIENT EXCEPTION in CountryService.") &&
                ((ClientException)t.getSuppressed()[0]).getCode() == 400)
            .verify();
    }

    @Test
    public void testHandle5xxFromRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockThisStatusForRest(500);
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");
        Mockito.when(exceptionServiceMock.recordExceptionEvent(any(ExceptionEvent.class))).thenReturn(Mono.empty());

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(this::checkRetryTimeoutMessage)
            .verify();
    }

    @Test
    public void testConnectionResetByPeerRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockNoConnectionForRestResponse();
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");
        Mockito.when(exceptionServiceMock.recordExceptionEvent(any(ExceptionEvent.class))).thenReturn(Mono.empty());

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(this::checkRetryTimeoutMessage)
            .verify();
    }

    @Test
    public void testEmptyResponseRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockEmptyResponseForRestResponse();
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");
        Mockito.when(exceptionServiceMock.recordExceptionEvent(any(ExceptionEvent.class))).thenReturn(Mono.empty());

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(this::checkRetryTimeoutMessage)
            .verify();
    }

    @Test
    public void testMalformedResponseChunkRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockMalformedResponseForRestResponse();
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");
        Mockito.when(exceptionServiceMock.recordExceptionEvent(any(ExceptionEvent.class))).thenReturn(Mono.empty());

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(this::checkRetryTimeoutMessage)
            .verify();
    }

    @Test
    public void testRandomDataRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockRandomDataForRestResponse();
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");
        Mockito.when(exceptionServiceMock.recordExceptionEvent(any(ExceptionEvent.class))).thenReturn(Mono.empty());

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(this::checkRetryTimeoutMessage)
            .verify();
    }

    private boolean checkRetryTimeoutMessage(Throwable t){
        return t.getMessage().equals(MAX_RETRY_MESSAGE);
    }
}
