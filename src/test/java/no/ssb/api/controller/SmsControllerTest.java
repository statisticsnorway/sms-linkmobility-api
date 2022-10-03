package no.ssb.api.controller;

import no.ssb.api.consumer.LinkmobilityService;
import no.ssb.api.database.SmsStatus;
import no.ssb.api.services.SmsStatusService;
import no.ssb.linkmobility.jaxb.request.MSG;
import no.ssb.linkmobility.jaxb.request.MSGLST;
import no.ssb.linkmobility.jaxb.request.ObjectFactory;
import no.ssb.linkmobility.jaxb.request.SESSION;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

/**
 * Created by runesr on 30.03.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SmsControllerTest {
    private static final String ACCEPTED_APIKEY = "123";
    private static final String NOT_ACCEPTED_APIKEY = "11111111-bd83-4a88-ad12-58c801bc7c23";
    @Mock
    SmsStatusService smsStatusService;
    @Mock
    LinkmobilityService linkmobilityService;
    @InjectMocks
    SmsController controller = new SmsController();

    String refId = "7c1af8fe-7dde-4a9e-a1f6-ec6e6d163b6d";
    String oppdatertStatus = "DELIVRD";
    String responseStatus = "OK";
    SmsStatus smsStatus;
    HttpHeaders httpHeader;

    @Before
    public void init() {
        httpHeader = new HttpHeaders();
        ReflectionTestUtils.setField(controller, "acceptedApiKeys", Arrays.asList(ACCEPTED_APIKEY) );
    }

    @Test
    public void skalHenteStatusForEnReferanseId() throws Exception {
        smsStatus = new SmsStatus(refId, oppdatertStatus);
        httpHeader.add("X-SSB-APIKEY", ACCEPTED_APIKEY);
        when(smsStatusService.hentSmsStatus(eq(refId))).thenReturn(smsStatus);
        ResponseEntity<String> response = (ResponseEntity<String>) controller.hentStatus(refId, httpHeader);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(oppdatertStatus));
    }

    @Test
    public void skalSendeSms() throws Exception {
        SESSION requestSession = createRequestSession();
        httpHeader.add("X-SSB-APIKEY", ACCEPTED_APIKEY);

        when(linkmobilityService.sendSms(isA(SESSION.class))).thenReturn(createResponseSession());
        ResponseEntity<no.ssb.linkmobility.jaxb.response.SESSION> response =
                (ResponseEntity<no.ssb.linkmobility.jaxb.response.SESSION>) controller
                        .sendSms(requestSession, httpHeader);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getMSGLST().getMSG().get(0).getREF(), is(refId));
        assertThat(response.getBody().getMSGLST().getMSG().get(0).getSTATUS(), is(responseStatus));
    }

    @Test
    public void skalIkkeSendeSmsMedUkjentApiKey() throws Exception {
        SESSION requestSession = createRequestSession();
        httpHeader.add("X-SSB-APIKEY", NOT_ACCEPTED_APIKEY);

        ResponseEntity<?> response = controller.sendSms(requestSession, httpHeader);

        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));

    }

    private SESSION createRequestSession() {
        ObjectFactory requestObjectFactory = new ObjectFactory();
        SESSION requestSession = requestObjectFactory.createSESSION();
        MSGLST requestMsgLst = requestObjectFactory.createMSGLST();
        MSG requestMsg = requestObjectFactory.createMSG();
        requestMsg.setTEXT("Test melding");
        requestMsgLst.getMSG().add(requestMsg);
        requestSession.setMSGLST(requestMsgLst);
        return requestSession;
    }

    private no.ssb.linkmobility.jaxb.response.SESSION createResponseSession() {
        no.ssb.linkmobility.jaxb.response.ObjectFactory responseObjectFactory = new no.ssb.linkmobility.jaxb.response.ObjectFactory();
        no.ssb.linkmobility.jaxb.response.SESSION responseSession = responseObjectFactory.createSESSION();
        no.ssb.linkmobility.jaxb.response.MSGLST responseMsglist = responseObjectFactory.createMSGLST();
        no.ssb.linkmobility.jaxb.response.MSG responseMsg = responseObjectFactory.createMSG();
        responseMsg.setREF(refId);
        responseMsg.setSTATUS(responseStatus);
        responseMsglist.getMSG().add(responseMsg);
        responseSession.setMSGLST(responseMsglist);
        return responseSession;
    }
}
