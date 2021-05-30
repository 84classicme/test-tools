package com.example.country;

import com.example.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;

public class CountryControllerTest {

    @Mock
    CountryService countryServiceMock;

    @InjectMocks
    CountryController countryController;

    @Before
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCountryFromRest() throws IOException {
        CountryRequest request = TestUtils.getCountryFromJson("src/test/resources/json/country.json");
        Mockito.when(countryServiceMock.getCountryFromLocal(anyString())).thenReturn(Mono.just(TestUtils.buildCountry()));

        StepVerifier.create(countryController.getCountry(request.getName()))
            .expectNextMatches(response -> "Utopia".equals(response.getName()) && response.getPopulation() == 1)
            .verifyComplete();
    }
}
