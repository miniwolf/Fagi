package com.fagi.handler.inputhandler;

import com.fagi.encryption.AES;
import com.fagi.encryption.AESKey;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.Session;
import com.fagi.responses.AllIsWell;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SessionTests {
    private InputHandler inputHandler;
    @Mock private OutputAgent outputAgent;
    @Mock private InputAgent inputAgent;
    @Mock private Data data;
    private Session session;

    @BeforeEach
    void setup() {
        var conversationHandler = new ConversationHandler(data);

        inputHandler = new InputHandler(inputAgent, outputAgent, conversationHandler, data);
        var aes = new AES();
        aes.generateKey(128);
        session = new Session((AESKey) aes.getKey());
    }

    @Test
    void whenReceivingASession_InputAgentShouldHaveAnAESEncryptionWithCorrectKey() {
        inputHandler.handleInput(session);

        ArgumentCaptor<AES> argumentCaptor = ArgumentCaptor.forClass(AES.class);
        verify(inputAgent, times(1)).setAes(argumentCaptor.capture());

        AES argument = argumentCaptor.getValue();
        assertNotNull(argument);
        assertEquals(session.getKey(), argument.getKey());
    }

    @Test
    void whenReceivingASession_OutputAgentShouldHaveAnAESEncryptionWithCorrectKey() {
        inputHandler.handleInput(session);

        ArgumentCaptor<AES> argumentCaptor = ArgumentCaptor.forClass(AES.class);
        verify(outputAgent, times(1)).setAes(argumentCaptor.capture());

        AES argument = argumentCaptor.getValue();
        assertNotNull(argument);
        assertEquals(session.getKey(), argument.getKey());
    }

    @Test
    void whenReceivingASession_InputAgentShouldHaveSessionCreatedSetToTrue() {
        inputHandler.handleInput(session);

        ArgumentCaptor<Boolean> booleanArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(inputAgent, times(1)).setSessionCreated(booleanArgumentCaptor.capture());

        assertTrue(booleanArgumentCaptor.getValue());
    }

    @Test
    void whenReceivingASession_ShouldReturnAllIsWellResponse() {
        inputHandler.handleInput(session);

        verify(outputAgent, times(1)).addResponse(any(AllIsWell.class));
    }
}
