package com.fagi.util;

import com.fagi.responses.Response;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.times;

/**
 * Helper functions to use an {@link OutputAgent} in tests
 */
public class OutputAgentTestUtil {
    private OutputAgentTestUtil() {
        // Disallows newing class
    }

    /**
     * Verifies the {@link OutputAgent} has received exactly one of the response type. The type must implement the {@link Response} interface
     *
     * @param outputAgent   the agent that received the response
     * @param responseClass the type of response wanted
     */
    public static <T extends Response> void assertOutputAgentReceivedResponseClass(
            OutputAgent outputAgent,
            Class<T> responseClass) {
        var argumentCaptor = ArgumentCaptor.forClass(Response.class);

        Mockito
                .verify(
                        outputAgent,
                        times(1)
                )
                .addResponse(argumentCaptor.capture());

        Assertions.assertTrue(responseClass.isInstance(argumentCaptor.getValue()));
    }

    /**
     * Capture exactly one of the response objects given to the {@link OutputAgent}
     *
     * @param outputAgent   the agent that received the response
     * @param responseClass the type the response wanted
     * @return the response given to the outputAgent
     */
    public static <T> T captureResponse(
            OutputAgent outputAgent,
            Class<T> responseClass) {
        var argumentCaptor = ArgumentCaptor.forClass(responseClass);
        Mockito
                .verify(
                        outputAgent,
                        times(1)
                )
                .addResponse(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    /**
     * Capture all the responses of a given type sent to the {@link OutputAgent}
     *
     * @param outputAgent   the agent that received the response
     * @param responseClass the type the response wanted
     * @param timesCalled   the expected number of calls to the outputAgent with the given responseClass
     * @return a list of objects of the given type
     */
    public static <T> List<T> captureResponses(
            OutputAgent outputAgent,
            Class<T> responseClass,
            int timesCalled) {
        var argumentCaptor = ArgumentCaptor.forClass(responseClass);
        Mockito
                .verify(
                        outputAgent,
                        times(timesCalled)
                )
                .addResponse(argumentCaptor.capture());
        return argumentCaptor.getAllValues();
    }

    /**
     * Verifies that no response of the given type have been sent to the {@link OutputAgent}
     *
     * @param outputAgent   the agent that should have received no response
     * @param responseClass the type of response not wanted
     */
    public static <T> void verifyNoResponseOfType(
            OutputAgent outputAgent,
            Class<T> responseClass) {
        var argumentCaptor = ArgumentCaptor.forClass(responseClass);
        Mockito
                .verify(
                        outputAgent,
                        times(0)
                )
                .addResponse(argumentCaptor.capture());
    }
}
