package no.ssb.api;

import no.ssb.api.database.SmsStatus;
import no.ssb.api.services.SmsStatusService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class SmsLinkmobilityApiApplicationTests {

	@Autowired
	SmsStatusService smsStatusService;
	SmsStatus smsStatus;
	String refId = "7c1af8fe-7dde-4a9e-a1f6-ec6e6d163b6d";
	String status = "Failed";

	@Before
	public void init() {
		smsStatus = new SmsStatus(refId, status);
	}

	@Test
	public void contextLoads() {
		assertThat(true, is(true));
	}

	@Test
	public void skalLagreStatus() {
		SmsStatus lagretStatus = smsStatusService.lagreSmsStatus(smsStatus);
		assertThat(lagretStatus, is(smsStatus));
	}

	@Test
	public void skalHenteUtDenLagredeSmsStatusMedRefId() {
		SmsStatus lagretSmsStatus = smsStatusService.lagreSmsStatus(smsStatus);
		SmsStatus hentetSmsStatus = smsStatusService.hentSmsStatus(refId);
		assertThat(hentetSmsStatus.getRef(), is(lagretSmsStatus.getRef()));
		assertThat(hentetSmsStatus.getStatus(), is(lagretSmsStatus.getStatus()));
	}




}