package com.example.feature;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class CountryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CountryService countryService;

    @InjectMocks
    CountryController countryController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();
    }

    @Test
    public void getCountryShouldReturnOk() throws Exception {
        Mockito.when(countryService.getCountryFromLocal(anyString())).thenReturn(new Country());
        MockHttpServletResponse response = mockMvc.perform(get("/country/Utopia")
                .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();
    }
}
