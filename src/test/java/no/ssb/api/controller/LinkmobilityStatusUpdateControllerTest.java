package no.ssb.api.controller;

import no.ssb.api.database.SmsStatus;
import no.ssb.api.services.SmsStatusService;
import no.ssb.linkmobility.jaxb.statusupdate.request.MSG;
import no.ssb.linkmobility.jaxb.statusupdate.request.MSGLST;
import no.ssb.linkmobility.jaxb.statusupdate.request.ObjectFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by runesr on 18.03.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkmobilityStatusUpdateControllerTest {

    @Mock
    SmsStatusService smsStatusService;
    @InjectMocks
    LinkmobilityStatusUpdateController controller = new LinkmobilityStatusUpdateController();

    ArgumentCaptor<SmsStatus> lagretStatus = ArgumentCaptor.forClass(SmsStatus.class);

    @Test
    public void skalKalleSmsStatusServiceForLagringMedRiktigeVerdier() {
        String meldingId = "1";
        String refnr = UUID.randomUUID().toString();
        String status = "DEVLIVRD";

        ResponseEntity<no.ssb.linkmobility.jaxb.statusupdate.response.MSGLST> response =
                (ResponseEntity<no.ssb.linkmobility.jaxb.statusupdate.response.MSGLST>) controller
                        .statusUpdate(opprettRequestMedMessageList(meldingId, refnr, status));

        Mockito.verify(smsStatusService).lagreSmsStatus(lagretStatus.capture());
        assertThat(lagretStatus.getValue().getRef(), is(refnr));
        assertThat(lagretStatus.getValue().getStatus(), is(status));

        assertThat(response.getBody().getMSG().get(0).getID(), is(meldingId));

    }

    private MSGLST opprettRequestMedMessageList(String meldingId, String refnr, String status) {
        ObjectFactory objectFactory = new ObjectFactory();
        MSGLST messages = objectFactory.createMSGLST();
        MSG message = objectFactory.createMSG();
        message.setID(meldingId);
        message.setREF(refnr);
        message.setSTATE(status);
        messages.getMSG().add(message);
        return messages;
    }

}
