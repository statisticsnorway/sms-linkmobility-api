package no.ssb.api.services;

import no.ssb.api.database.SmsStatus;
import no.ssb.api.repository.SmsStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mnm on 15.03.2016.
 */
@Service
@Transactional
public class SmsStatusService {
    @Autowired
    SmsStatusRepository smsStatusRepository;

    public SmsStatus lagreSmsStatus(SmsStatus status){
        smsStatusRepository.save(status);
        return status;
    }

    public SmsStatus hentSmsStatus(String ref){
        SmsStatus status = smsStatusRepository.findByRef(ref);
        return status;
    }
}

