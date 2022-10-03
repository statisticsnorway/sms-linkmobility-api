package no.ssb.api.util;

import com.google.common.collect.Lists;
import no.ssb.linkmobility.jaxb.request.MSG;
import no.ssb.linkmobility.jaxb.request.MSGLST;
import no.ssb.linkmobility.jaxb.request.ObjectFactory;
import no.ssb.linkmobility.jaxb.request.SESSION;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lrb on 03.10.2016.
 */
public class Util {
    public List<SESSION> splittSESSION(SESSION session, int antallMsgPrSESSION){
        ObjectFactory sessionObjectFactory = new ObjectFactory();
        List<MSG> smser = session.getMSGLST().getMSG();
        List<List<MSG>> listeavliste = Lists.partition(smser,antallMsgPrSESSION);
        List<SESSION> sesjoner = new ArrayList<>(listeavliste.size());
        for (List<MSG> msg : listeavliste){
            SESSION nySession = lagNySession(session, sessionObjectFactory);
            MSGLST msglst = sessionObjectFactory.createMSGLST();
            msglst.getMSG().addAll(msg);
            nySession.setMSGLST(msglst);
            sesjoner.add(nySession);
        }
        return sesjoner;
    }
    private static SESSION lagNySession(SESSION session,ObjectFactory sessionObjectFactory){
        SESSION nySession = sessionObjectFactory.createSESSION();
        nySession.setAP(session.getAP());
        nySession.setCLIENT(session.getCLIENT());
        nySession.setPW(session.getPW());
        nySession.setSD(session.getSD());
        //QRY bruker vi foreløpig ikke
        return nySession;
    }
}
