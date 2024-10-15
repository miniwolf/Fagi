package com.fagi.util;

import com.fagi.responses.Response;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;

public class OutputAgentTestUtil {
    private OutputAgentTestUtil() {
        // Disallows newing class
    }

    public static <T extends Response> void verifyOutputAgentResponseClass(
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
}
