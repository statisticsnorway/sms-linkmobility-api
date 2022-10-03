package no.ssb.api.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import no.ssb.api.database.SmsStatus;
import no.ssb.api.repository.SmsStatusRepository;
import no.ssb.api.services.SmsStatusService;
import no.ssb.linkmobility.jaxb.statusupdate.request.MSG;
import no.ssb.linkmobility.jaxb.statusupdate.request.MSGLST;
import no.ssb.linkmobility.jaxb.statusupdate.response.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by runesr on 17.03.2016.
 */
@Api("Api mot SMS-leverandør")
@RestController
@RequestMapping("")
public class LinkmobilityStatusUpdateController {

    private static final String RESPONSE_STATUS_OK = "OK";

    @Autowired
    private SmsStatusService smsStatusService;

    private ObjectFactory objectFactory = new ObjectFactory();

    @ApiOperation("Tjeneste for å kunne motta oppdatert status på meldinger fra SMS-leverandør")
    @RequestMapping(value = "/statusoppdatering/", method = RequestMethod.POST, produces = "application/xml")
    public ResponseEntity<?> statusUpdate(@RequestBody MSGLST messages) {
        no.ssb.linkmobility.jaxb.statusupdate.response.MSGLST responseMessages = objectFactory.createMSGLST();
        for (MSG message : messages.getMSG()) {
            smsStatusService.lagreSmsStatus(new SmsStatus(message.getREF(), message.getSTATE()));
            no.ssb.linkmobility.jaxb.statusupdate.response.MSG responseMessage = objectFactory.createMSG();
            responseMessage.setID(message.getID());
            responseMessage.setSTATUS(RESPONSE_STATUS_OK);
            responseMessages.getMSG().add(responseMessage);
        }
        return new ResponseEntity<>(responseMessages, HttpStatus.OK);
    }
}
