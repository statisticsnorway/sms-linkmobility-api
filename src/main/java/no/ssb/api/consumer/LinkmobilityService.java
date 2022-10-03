package no.ssb.api.consumer;

import no.ssb.linkmobility.jaxb.request.SESSION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.UUID;

/**
 * Created by runesr on 14.03.2016.
 */
@Service
public class LinkmobilityService {

    @Autowired
    RestTemplate restTemplate;
    @Value("${sms.linkmobility.url}")
    private String linkmobilityUrl;
    @Value("${sms.linkmobility.bruker}")
    private String bruker;
    @Value("${sms.linkmobility.passord}")
    private String passord;

    public no.ssb.linkmobility.jaxb.response.SESSION sendSms(SESSION session) throws JAXBException {
        session.setCLIENT(bruker);
        session.setPW(passord);
        if (session.getSD() == null) {
            session.setSD(UUID.randomUUID().toString());
        }

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "plain/text; charset=ISO-8859-1");

        String xmlRequest = konverterRequestSESSIONtilXml(session);
        HttpEntity<String> entity = new HttpEntity<>(xmlRequest, requestHeaders);

        ResponseEntity<no.ssb.linkmobility.jaxb.response.SESSION> response =
                restTemplate.postForEntity(linkmobilityUrl, entity, no.ssb.linkmobility.jaxb.response.SESSION.class);
        return response.getBody();
    }


    public no.ssb.linkmobility.jaxb.response.SESSION sendMangeSMS(SESSION session) throws JAXBException {
        return sendSms(session);
    }


    private String konverterRequestSESSIONtilXml(SESSION session) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(SESSION.class);
        java.io.StringWriter sw = new StringWriter();
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
        marshaller.marshal(session, sw);
        return sw.toString();
    }

    public no.ssb.linkmobility.jaxb.response.SESSION mockSendSms(SESSION session) {
        no.ssb.linkmobility.jaxb.response.ObjectFactory responseObjectFactory = new no.ssb.linkmobility.jaxb.response.ObjectFactory();
        no.ssb.linkmobility.jaxb.response.SESSION responseSession = responseObjectFactory.createSESSION();
        no.ssb.linkmobility.jaxb.response.MSGLST messages = responseObjectFactory.createMSGLST();
        for (no.ssb.linkmobility.jaxb.request.MSG msg : session.getMSGLST().getMSG()) {
            no.ssb.linkmobility.jaxb.response.MSG responseMsg = responseObjectFactory.createMSG();
            responseMsg.setID(msg.getID());
            responseMsg.setSTATUS("OK");
            responseMsg.setREF(UUID.randomUUID().toString());
            messages.getMSG().add(responseMsg);
        }
        responseSession.setLOGON("OK");
        responseSession.setMSGLST(messages);

        return responseSession;
    }

}
