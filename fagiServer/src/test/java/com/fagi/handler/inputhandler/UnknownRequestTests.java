package com.fagi.handler.inputhandler;

import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.util.OutputAgentTestUtil;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.mockito.Mockito.when;

public class UnknownRequestTests {
    private OutputAgent outputAgent;
    private InputHandler inputHandler;
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setup() {
        Data data = Mockito.mock(Data.class);
        InputAgent inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.spy(OutputAgent.class);
        ConversationHandler conversationHandler = new ConversationHandler(data);
        inputHandler = new InputHandler(
                inputAgent,
                outputAgent,
                conversationHandler,
                data
        );

        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void whenSendingUnknownRequestObject_ShouldGiveNoResponse() {
        inputHandler.handleInput(new UnknownRequest());

        OutputAgentTestUtil.verifyNoResponseOfType(
                outputAgent,
                Object.class
        );
    }

    @Test
    void whenSendingUnknownRequestObject_ShouldPrintMessageInSysOut() {
        inputHandler.handleInput(new UnknownRequest());

        Assertions.assertEquals(
                "Unknown handle: " + UnknownRequest.class,
                outputStreamCaptor.toString().trim()
        );
    }

    private static class UnknownRequest {
    }
}
