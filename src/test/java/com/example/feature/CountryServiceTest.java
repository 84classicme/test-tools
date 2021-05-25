package com.example.feature;

import com.example.RestServiceWireMock;
import com.example.TestUtils;
import com.example.WebServiceWireMock;
import com.example.feature.ClientException;
import com.example.feature.Country;
import com.example.feature.CountryService;
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
    WebServiceWireMock wsWireMock;
    RestServiceWireMock restWireMock;

    @Test
    public void testGetCountryFromWS() throws IOException {
        wsWireMock = new WebServiceWireMock();
        wsWireMock.mockGetCountrySoapResponse("src/test/resources/xml/CountryResponse.xml");
        GetCountryRequest request = TestUtils.getCountryRequestFromXml("src/test/resources/xml/CountryRequest.xml");

        countryService = new CountryService();
        countryService.setServiceEndpointUrl("http://localhost:8088/getCountry");

        StepVerifier.create(countryService.getCountryFromWebService(request))
            .expectNextMatches(response -> "NewTest".equals(response.getCountry().getName()))
            .verifyComplete();
    }

    @Test
    public void testGetCountryFromRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockGetCountryRest("src/test/resources/json/country.json");
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");

        countryService = new CountryService();
        countryService.setServiceEndpointUrl("http://localhost:8088/country");

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectNextMatches(response -> "MyCountry".equals(response.getName()) && response.getPopulation() == 1)
            .verifyComplete();
    }

    @Test
    public void testHandle4xxFromRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockThisStatusForRest(400);
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");

        countryService = new CountryService();
        countryService.setServiceEndpointUrl("http://localhost:8088/country");

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(t -> t.getMessage().equals("Cannot process com.example.feature.CountryService.getCountry due to client error.") &&
                t.getSuppressed()[0] instanceof ClientException &&
                t.getSuppressed()[0].getMessage().equals("CLIENT EXCEPTION in com.example.feature.CountryService.") &&
                ((ClientException)t.getSuppressed()[0]).getCode() == 400)
            .verify();
    }

    @Test
    public void testHandle5xxFromRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockThisStatusForRest(500);
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");

        countryService = new CountryService();
        countryService.setServiceEndpointUrl("http://localhost:8088/country");

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(t -> t.getMessage().equals("External Service failed to process after max retries."))
            .verify();
    }

    @Test
    public void testConnectionResetByPeerRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockNoConnectionForRestResponse();
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");

        countryService = new CountryService();
        countryService.setServiceEndpointUrl("http://localhost:8088/country");

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(t -> t.getMessage().equals("External Service failed to process after max retries."))
            .verify();
    }

    @Test
    public void testEmptyResponseRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockEmptyResponseForRestResponse();
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");

        countryService = new CountryService();
        countryService.setServiceEndpointUrl("http://localhost:8088/country");

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(t -> t.getMessage().equals("External Service failed to process after max retries."))
            .verify();
    }

    @Test
    public void testMalformedResponseChunkRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockMalformedResponseForRestResponse();
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");

        countryService = new CountryService();
        countryService.setServiceEndpointUrl("http://localhost:8088/country");

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(t -> t.getMessage().equals("External Service failed to process after max retries."))
            .verify();
    }

    @Test
    public void testRandomDataRest() throws IOException {
        restWireMock = new RestServiceWireMock();
        restWireMock.mockRandomDataForRestResponse();
        Country request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");

        countryService = new CountryService();
        countryService.setServiceEndpointUrl("http://localhost:8088/country");

        StepVerifier.create(countryService.getCountryFromRestService(request))
            .expectErrorMatches(t -> t.getMessage().equals("External Service failed to process after max retries."))
            .verify();
    }
}
