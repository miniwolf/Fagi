package com.fagi.handler.inputhandler;

import com.fagi.handler.InputHandler;
import com.fagi.logging.TestLogLevel;
import com.fagi.logging.TestLogRecord;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.when;

public class NotValidRequestTests extends BaseInputHandlerTest {
    void beforeEach() {
        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);
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
    void whenSendingUnknownRequestObject_ShouldLogMessage() {
        inputHandler.handleInput(new UnknownRequest());

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(InputHandler.class);

        Assertions.assertEquals(
                1,
                testLogRecords.size()
        );

        TestLogRecord<?> logRecord = testLogRecords.getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        "Unknown handle: " + UnknownRequest.class,
                        logRecord.message()
                ),
                () -> Assertions.assertEquals(
                        TestLogLevel.INFO,
                        logRecord.logLevel()
                )
        );
    }

    @Test
    void givenInputIsNull_ShouldGiveNoResponse() {
        inputHandler.handleInput(null);

        OutputAgentTestUtil.verifyNoResponseOfType(
                outputAgent,
                Object.class
        );
    }

    @Test
    void givenInputIsNull_ShouldLogMessage() {
        inputHandler.handleInput(null);

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(InputHandler.class);

        Assertions.assertEquals(
                1,
                testLogRecords.size()
        );

        TestLogRecord<?> logRecord = testLogRecords.getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        "Input is null. Doing nothing.",
                        logRecord.message()
                ),
                () -> Assertions.assertEquals(
                        TestLogLevel.INFO,
                        logRecord.logLevel()
                )
        );
    }

    private static class UnknownRequest {
    }
}
