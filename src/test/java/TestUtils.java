import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.generated.GetCountryRequest;
import com.generated.GetCountryResponse;

import java.io.*;

public class TestUtils {
    public static GetCountryRequest getCountryRequestFromXml(String path) throws IOException {
        if (path == null || path.isBlank()) path = "src/test/resources/CountryRequest.xml";
        File file = new File(path);
        XmlMapper xmlMapper = new XmlMapper();
        String xml = inputStreamToString(new FileInputStream(file));
        return xmlMapper.readValue(xml, GetCountryRequest.class);
    }

    public static GetCountryResponse getCountryResponseFromXml(String path) throws IOException {
        if (path == null || path.isBlank()) path = "src/test/resources/CountryResponse.xml";
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
}
