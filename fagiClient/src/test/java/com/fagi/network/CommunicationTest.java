package com.fagi.network;

import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.network.Communication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ObjectOutputStream;

/**
 * Testing the communication send from the client side
 *
 * @author miniwolf
 */
public class CommunicationTest {
    private Communication communication;
    @BeforeEach
    public void init() {
        System.out.println("Starting communicationTests");
        communication = new Communication();
    }

    @Test
    public void sendObject() throws Exception {
        var objectToBeSend = "Dimmer";
        var bytes = Conversion.convertToBytes(objectToBeSend);

        var out = Mockito.spy(ObjectOutputStream.class);
        Mockito.doNothing().when(out).writeObject(Mockito.any());
        Mockito.doNothing().when(out).flush();

        var encryptionAlgorithm = Mockito.mock(EncryptionAlgorithm.class);
        Mockito.doReturn(bytes).when(encryptionAlgorithm).encrypt(Mockito.any());
        communication.setOut(out);
        communication.setEncryption(encryptionAlgorithm);

        communication.sendObject(objectToBeSend);
        Mockito.verify(out, Mockito.times(1)).writeObject(bytes);
    }
}
