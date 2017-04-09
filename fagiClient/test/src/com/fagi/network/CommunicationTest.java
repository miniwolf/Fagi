package com.fagi.network;

import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Testing the communication send from the client side
 *
 * @author miniwolf
 */
public class CommunicationTest {
    private Communication communication;
    @Before
    public void init() {
        communication = new Communication();
    }

    @Test
    public void sendObject() throws Exception {
        String objectToBeSend = "Dimmer";
        byte[] bytes = Conversion.convertToBytes(objectToBeSend);

        ObjectOutputStream out = Mockito.spy(ObjectOutputStream.class);
        Mockito.doNothing().when(out).writeObject(Mockito.any());
        Mockito.doNothing().when(out).flush();

        EncryptionAlgorithm encryptionAlgorithm = Mockito.mock(EncryptionAlgorithm.class);
        Mockito.doReturn(bytes).when(encryptionAlgorithm).encrypt(Mockito.any());
        communication.setOut(out);
        communication.setEncryption(encryptionAlgorithm);

        communication.sendObject(objectToBeSend);
        Mockito.verify(out, Mockito.times(1)).writeObject(bytes);
    }
}
