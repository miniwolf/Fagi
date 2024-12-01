package com.fagi.handler.inputhandler;

import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.mockito.Mockito.when;

public class UnknownRequestTests extends BaseInputHandlerTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    void beforeEach() {
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
                outputStreamCaptor
                        .toString()
                        .trim()
        );
    }

    private static class UnknownRequest {
    }
}
