package com.example;

import com.example.country.Country;
import com.example.country.CountryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.generated.GetCountryRequest;
import com.generated.GetCountryResponse;

import java.io.*;

public class TestUtils {

    public static CountryRequest getCountryFromJson(String path) throws IOException {
        if (path == null || path.isEmpty()) path = "src/test/resources/json/country.json";
        File file = new File(path);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, CountryRequest.class);
    }

    public static GetCountryRequest getCountryRequestFromXml(String path) throws IOException {
        if (path == null || path.isEmpty()) path = "src/test/resources/xml/CountryRequest.xml";
        File file = new File(path);
        XmlMapper xmlMapper = new XmlMapper();
        String xml = inputStreamToString(new FileInputStream(file));
        return xmlMapper.readValue(xml, GetCountryRequest.class);
    }

    public static GetCountryResponse getCountryResponseFromXml(String path) throws IOException {
        if (path == null || path.isEmpty()) path = "src/test/resources/xml/CountryResponse.xml";
        File file = new File(path);
        XmlMapper xmlMapper = new XmlMapper();
        String xml = inputStreamToString(new FileInputStream(file));
        return xmlMapper.readValue(xml, GetCountryResponse.class);
    }

    private static String inputStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    public static Country buildCountry(){
        return Country.builder()
            .id(0)
            .capital("Ritehere")
            .population(1)
            .name("Utopia")
            .currency("MGB")
            .build();
    }
}
