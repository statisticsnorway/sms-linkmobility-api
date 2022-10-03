package no.ssb.api.repository;

import no.ssb.api.database.SmsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Created by mnm on 15.03.2016.
 */
@Repository
public interface SmsStatusRepository extends JpaRepository<SmsStatus, String> {
    SmsStatus findByRef(String ref);
}
