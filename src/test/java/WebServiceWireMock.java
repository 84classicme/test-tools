import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;

@TestComponent
public class WebServiceWireMock {

    private  final String NAMESPACE_URI = "http://localhost";
    private  final String NAMESPACE_PREFIX = "tns1";
    private final String SERVICE_ENDPOINT = "/rel/path/to/SEI";

    public List<ServeEvent> getRequests(){
        return getAllServeEvents();
    }

    public void mockVertexResponse(final String pathToXml){
        String response = "";
        SoapObject fromXmlSoapObject = new SoapObject();
        String serSoapObj = serializeObject(fromXmlSoapObject);
    }

    public <T> String serializeObject(T object){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Class clazz = object.getClass();
        String responseRootTag = StringUtils.uncapitalize(clazz.getSimpleName());
        QName payloadName = new QName(NAMESPACE_URI,responseRootTag,NAMESPACE_PREFIX);
        try{
            JAXBContext ctx = JAXBContext.newInstance(clazz);
            Marshaller objectMarshaller = ctx.createMarshaller();

            JAXBElement<T> jaxbElement = new JAXBElement<>(payloadName, clazz, null, object);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            objectMarshaller.marshal(jaxbElement, document);

            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();
            body.addDocument(document);
            
            //byteArrayOutputStream = new ByteArrayOutputStream();
            soapMessage.saveChanges();
            soapMessage.writeTo(byteArrayOutputStream);

        } catch (JAXBException | ParserConfigurationException | SOAPException | IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    public <T> T deserializeSoapRequest(String soapRequest, Class<T> clazz){
        XMLInputFactory xif = XMLInputFactory.newFactory();
        JAXBElement<T> jb = null;
        try{
            XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(soapRequest));
            do{
                xsr.nextTag();
            } while(!xsr.getLocalName().equals("Body"));
            xsr.nextTag();
            JAXBContext ctx = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            jb = unmarshaller.unmarshal(xsr, clazz);
            xsr.close();
        } catch (XMLStreamException | JAXBException e) {
            e.printStackTrace();
        }
        return jb.getValue();
    }

    private XPath getXPathFactory(){
        Map<String, String> namespaceUris = new HashMap<>();
        namespaceUris.put("xml", XMLConstants.XML_NS_URI);
        namespaceUris.put("soap", "http://schemas.xmlsoap.org/soap/envelope");
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if(namespaceUris.containsKey(prefix)) {
                    return namespaceUris.get(prefix);
                }else {
                    return XMLConstants.NULL_NS_URI;
                }
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                return null;
            }
        });
        return xPath;
    }
}
