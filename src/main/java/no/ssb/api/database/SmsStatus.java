package no.ssb.api.database;

import javax.persistence.*;

/**
 * Created by mnm on 15.03.2016.
 */
@Entity
public class SmsStatus {

    @Id
    private String ref;

    private String status;

    public SmsStatus() {}

    public SmsStatus(String ref, String status) {
        this.ref = ref;
        this.status  = status;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
