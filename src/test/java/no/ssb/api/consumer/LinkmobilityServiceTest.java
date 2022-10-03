package no.ssb.api.consumer;

import no.ssb.linkmobility.jaxb.request.MSG;
import no.ssb.linkmobility.jaxb.request.MSGLST;
import no.ssb.linkmobility.jaxb.request.ObjectFactory;
import no.ssb.linkmobility.jaxb.request.SESSION;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by runesr on 15.03.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkmobilityServiceTest {

    private static final String BRUKER = "testbruker";
    private static final String PASSORD = "testpassord";

    RestTemplate restTemplate;
    LinkmobilityService linkmobilityService = new LinkmobilityService();

    private SESSION requestSession;
    private no.ssb.linkmobility.jaxb.response.SESSION responseSession;
    private ObjectFactory requestObjectFactory;
    private no.ssb.linkmobility.jaxb.response.ObjectFactory responseObjectFactory;

    @Before
    public void init() {
        restTemplate = new RestTemplate();
        requestObjectFactory = new ObjectFactory();
        responseObjectFactory = new no.ssb.linkmobility.jaxb.response.ObjectFactory();
        ReflectionTestUtils.setField(linkmobilityService, "bruker", BRUKER );
        ReflectionTestUtils.setField(linkmobilityService, "passord", PASSORD );
        ReflectionTestUtils.setField(linkmobilityService, "restTemplate", restTemplate );

    }

    @Test
    public void skalSetteBrukernavnOgPassordOgKalleLinkmobility() throws Exception {
        requestSession = requestObjectFactory.createSESSION();

        ReflectionTestUtils.setField(linkmobilityService, "linkmobilityUrl", "/test");
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(requestTo("/test")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("", MediaType.APPLICATION_XML));

        linkmobilityService.sendSms(requestSession);

        mockServer.verify();
        assertThat(requestSession.getCLIENT(), is(BRUKER));
        assertThat(requestSession.getPW(), is(PASSORD));
    }

    @Test
    @Ignore("Brukt for testing for sending av sms. Denne testen sender SMS med riktig brukenavn/passord")
    public void skalSendeSms() {
        RestTemplate restTemplate = new RestTemplate();
//        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
//        InetSocketAddress inetSocketAddress = new InetSocketAddress("proxy.ssb.no",3128);
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
//        simpleClientHttpRequestFactory.setProxy(proxy);
//        restTemplate.setRequestFactory(simpleClientHttpRequestFactory);

        String url = "https://xml.pswin.com";
        String bruker = "<brukernavn>";
        String passord = "<passord>";

        SESSION session = requestObjectFactory.createSESSION();
        session.setCLIENT(bruker);
        session.setPW(passord);
        MSGLST requestMsgLst = requestObjectFactory.createMSGLST();
        MSG requestMsg = requestObjectFactory.createMSG();
        requestMsg.setTEXT("Test melding");
        requestMsg.setRCV("<TELEFONNUMMER>");
        requestMsg.setSND("SSB");
        requestMsgLst.getMSG().add(requestMsg);
        session.setMSGLST(requestMsgLst);

        restTemplate.postForObject(url, session, no.ssb.linkmobility.jaxb.response.SESSION.class);

    }

}
