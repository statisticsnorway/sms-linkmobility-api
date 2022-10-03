package no.ssb.api.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import no.ssb.api.consumer.LinkmobilityService;
import no.ssb.api.database.SmsStatus;
import no.ssb.api.services.SmsStatusService;
import no.ssb.api.util.Util;
import no.ssb.linkmobility.jaxb.request.MSG;
import no.ssb.linkmobility.jaxb.request.MSGLST;
import no.ssb.linkmobility.jaxb.request.ObjectFactory;
import no.ssb.linkmobility.jaxb.request.SESSION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by runesr on 11.03.2016.
 */
@Api("SMS-linkmobility-api")
@RestController
@RequestMapping("")
public class SmsController {
    private static final String HEADER_APIKEY = "X-SSB-APIKEY";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LinkmobilityService linkmobilityService;
    @Autowired
    SmsStatusService smsStatusService;

    @Value("${api-keys}")
    private List<String> acceptedApiKeys;

    @Value("${test.telefonnummer.fil}")
    private String testTelefonnummerFil;

    private String landsnrNorge = "47";


    @ApiOperation(value = "Sende SMS")
    @RequestMapping(value = "/sms", method = RequestMethod.POST, produces = "application/xml")
    public ResponseEntity<?> sendSms(@RequestBody SESSION session, @RequestHeader HttpHeaders header) throws IOException, JAXBException {
        if (!authorizeRequest(header)) {
            return new ResponseEntity<>("Unauthorized. Api-key er ugyldig eller mangler", HttpStatus.UNAUTHORIZED);
        }
        logger.info("Antall meldinger som skal sendes: {}", session.getMSGLST().getMSG().size());
        session.setMSGLST(leggTilLandsnrHvisLengde8(session.getMSGLST()));
        no.ssb.linkmobility.jaxb.response.SESSION responseSession = linkmobilityService.sendSms(session);
        return new ResponseEntity<>(responseSession, HttpStatus.OK);
    }


    @ApiOperation(value = "Sende mange SMS. Splitter i batcher på 150")
    @RequestMapping(value = "/mangesms", method = RequestMethod.POST, produces = "application/xml")
    public ResponseEntity<?> sendMangeSms(@RequestBody SESSION session, @RequestHeader HttpHeaders header) throws IOException, JAXBException {
        if (!authorizeRequest(header)) {
            return new ResponseEntity<>("Unauthorized. Api-key er ugyldig eller mangler", HttpStatus.UNAUTHORIZED);
        }
        logger.info("Antall meldinger som skal sendes: {}", session.getMSGLST().getMSG().size());
        Util util = new Util();
        List<SESSION> sessions = util.splittSESSION(session, 1);
        no.ssb.linkmobility.jaxb.response.SESSION responseSession = null;
        no.ssb.linkmobility.jaxb.response.SESSION response = null;
        for (SESSION newSession : sessions) {
            try {
                session.setMSGLST(leggTilLandsnrHvisLengde8(session.getMSGLST()));
                response = linkmobilityService.sendMangeSMS(newSession);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            if (response != null) {
                if (responseSession == null) {
                    responseSession = response;
                } else {
                    responseSession.getMSGLST().getMSG().addAll(response.getMSGLST().getMSG());
                }
            }
        }
        return new ResponseEntity<>(responseSession, HttpStatus.OK);
    }


    @ApiOperation(value = "Late-som-sende SMS")
    @RequestMapping(value = "/fakesms", method = RequestMethod.POST, produces = "application/xml")
    public ResponseEntity<?> fakeSendSms(@RequestBody SESSION session, @RequestHeader HttpHeaders header) throws IOException, JAXBException {
        if (!authorizeRequest(header)) {
            return new ResponseEntity<>("Unauthorized. Api-key er ugyldig eller mangler", HttpStatus.UNAUTHORIZED);
        }
        logger.info("Antall meldinger som skal late-som-sendes: {}", session.getMSGLST().getMSG().size());
        no.ssb.linkmobility.jaxb.response.SESSION responseSession = linkmobilityService.mockSendSms(session);
        return new ResponseEntity<>(responseSession, HttpStatus.OK);
    }



    @ApiOperation(value = "Hente status for en melding")
    @RequestMapping(value = "/smsstatus/{refId}", method = RequestMethod.GET, headers="Accept=application/xml")
    public ResponseEntity<?> hentStatus(@PathVariable String refId, @RequestHeader HttpHeaders header) throws IOException {
        if (!authorizeRequest(header)) {
            return new ResponseEntity<>("Unauthorized. Api-key er ugyldig eller mangler", HttpStatus.UNAUTHORIZED);
        }
        logger.info("ReferanseId: {}", refId);
        SmsStatus smsStatus = smsStatusService.hentSmsStatus(refId);
        return new ResponseEntity<>(smsStatus.getStatus() != null ? smsStatus.getStatus() : null, HttpStatus.OK);
    }


    @ApiOperation(value = "Ping metode for å kunne sjekke at API'et er oppe")
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity<?> ping() {
        return new ResponseEntity<>("pong", HttpStatus.OK);
    }


    @ApiOperation(value = "Sender sms til telefonnumrene som også ligger på fil med testnr")
    @RequestMapping(value = "/testsms", method = RequestMethod.POST, produces = "application/xml")
    public ResponseEntity<?> testSendSms(@RequestBody SESSION session, @RequestHeader HttpHeaders header) throws IOException, JAXBException {
        if (!authorizeRequest(header)) {
            return new ResponseEntity<>("Unauthorized. Api-key er ugyldig eller mangler", HttpStatus.UNAUTHORIZED);
        }
        ObjectFactory requestObjectFactory = new ObjectFactory();
        no.ssb.linkmobility.jaxb.request.SESSION nySession = requestObjectFactory.createSESSION();
        MSGLST msglst = requestObjectFactory.createMSGLST();

        logger.info("Antall meldinger på sms-liste: {}", session.getMSGLST().getMSG().size());
        session.setMSGLST(leggTilLandsnrHvisLengde8(session.getMSGLST()));
        session.getMSGLST().getMSG().forEach(msg -> {
             if (mottakerOk(msg.getRCV())) {
                 msglst.getMSG().add(msg);
             }
        });
        nySession.setMSGLST(msglst);
        logger.info("Antall meldinger det vil bli sendt sms til: {}", nySession.getMSGLST().getMSG().size());
        no.ssb.linkmobility.jaxb.response.SESSION responseSession = linkmobilityService.sendSms(nySession);
        return new ResponseEntity<>(responseSession, HttpStatus.OK);
    }


    @ApiOperation(value = "Sender sms til mottaker")
    @RequestMapping(value = "/smsmottaker", method = RequestMethod.POST, produces = "application/xml")
    public ResponseEntity<?> SendSmsMottaker(@RequestParam String mottaker
            , @RequestParam String tekst, @RequestHeader HttpHeaders header) throws IOException, JAXBException {
        if (!authorizeRequest(header)) {
            return new ResponseEntity<>("Unauthorized. Api-key er ugyldig eller mangler", HttpStatus.UNAUTHORIZED);
        }
        if (mottakerOk(mottaker)) {
            try {
                ObjectFactory requestObjectFactory = new ObjectFactory();
                no.ssb.linkmobility.jaxb.request.SESSION session = requestObjectFactory.createSESSION();
                MSGLST msglst = requestObjectFactory.createMSGLST();
                MSG msg = requestObjectFactory.createMSG();
                msg.setID("0");
                msg.setTEXT(tekst);
                msg.setRCV((mottaker.length() == 8 ? landsnrNorge : "") + mottaker);
                msg.setSND("SSB");
                msglst.getMSG().add(msg);
                session.setMSGLST(msglst);

                no.ssb.linkmobility.jaxb.response.SESSION responseSession = linkmobilityService.sendSms(session);
                return new ResponseEntity<>(responseSession, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("noe feilet ved sending til " + mottaker + ": " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(mottaker + " ikke godkjent mottaker", HttpStatus.OK);
        }
    }



    private boolean mottakerOk(String mottaker) {
        try {
            List<String> testTelefonnr = Files.readAllLines(Paths.get(testTelefonnummerFil));
            return testTelefonnr.contains(mottaker);
        } catch (IOException ioe) {
            logger.error("IOException ved forsøk på å lese testadresser fra " + testTelefonnummerFil + ": " + ioe.getMessage());
        }
        return false;
    }

    private MSGLST leggTilLandsnrHvisLengde8(MSGLST msglst) {
        ObjectFactory requestObjectFactory = new ObjectFactory();
        MSGLST okMsglst = requestObjectFactory.createMSGLST();
        msglst.getMSG().forEach(msg -> {
            msg.setRCV(((msg.getRCV() != null && msg.getRCV().length() == 8) ? landsnrNorge : "") + msg.getRCV());
            okMsglst.getMSG().add(msg);
        });
        return okMsglst;
    }

    private boolean authorizeRequest(HttpHeaders header) {
        if (header.get(HEADER_APIKEY) == null) {
            return false;
        }
        return acceptedApiKeys.contains(header.get(HEADER_APIKEY).get(0));

    }


}
