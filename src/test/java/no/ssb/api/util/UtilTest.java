package no.ssb.api.util;

import no.ssb.linkmobility.jaxb.request.MSG;
import no.ssb.linkmobility.jaxb.request.MSGLST;
import no.ssb.linkmobility.jaxb.request.ObjectFactory;
import no.ssb.linkmobility.jaxb.request.SESSION;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by lrb on 03.10.2016.
 */
public class UtilTest {
    @Test
    public void testSplittAvSESSION(){
        Util util = new Util();
        SESSION session = createRequestSession();
        List<SESSION> nyeSession = util.splittSESSION(session,1);
        assertEquals(nyeSession.size(),3);
        assertEquals(nyeSession.get(0).getMSGLST().getMSG().get(0).getRCV(),"telefonnummer1");
        assertEquals(nyeSession.get(1).getMSGLST().getMSG().get(0).getRCV(),"telefonnummer2");
        assertEquals(nyeSession.get(2).getMSGLST().getMSG().get(0).getRCV(),"telefonnummer3");
    }
    private SESSION createRequestSession() {
        ObjectFactory requestObjectFactory = new ObjectFactory();
        SESSION requestSession = requestObjectFactory.createSESSION();
        requestSession.setSD("4242");
        requestSession.setPW("passord");
        requestSession.setCLIENT("junit");
        requestSession.setAP("ap");
        MSGLST requestMsgLst = requestObjectFactory.createMSGLST();
        MSG requestMsg = requestObjectFactory.createMSG();
        requestMsg.setTEXT("meldingstekst1");
        requestMsg.setRCV("telefonnummer1");
        requestMsg.setID("id1");
        requestMsg.setSND("SSB");
        requestMsgLst.getMSG().add(requestMsg);

        MSG requestMsg2 = requestObjectFactory.createMSG();
        requestMsg2.setTEXT("meldingstekst2");
        requestMsg2.setRCV("telefonnummer2");
        requestMsg2.setID("id2");
        requestMsg2.setSND("SSB");
        requestMsgLst.getMSG().add(requestMsg2);

        MSG requestMsg3 = requestObjectFactory.createMSG();
        requestMsg3.setTEXT("meldingstekst3");
        requestMsg3.setRCV("telefonnummer3");
        requestMsg3.setID("id3");
        requestMsg3.setSND("SSB");
        requestMsgLst.getMSG().add(requestMsg3);

        requestSession.setMSGLST(requestMsgLst);
        return requestSession;
    }
}
