package com.fagi.server.running;

import com.fagi.model.Data;
import com.fagi.server.Server;
import com.fagi.utility.JsonFileOperations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class CheckFieldServerRunningStrategyTest {
    @AfterEach
    void tearDown() {
        var configFolder = new File(JsonFileOperations.CONFIG_FOLDER_PATH);

        if (configFolder.exists()) {
            configFolder.delete();
        }
    }

    @Test
    void givenServerIsRunning_WhenCallingStrategy_ThenShouldReturnTrue() {
        var server = new Server(
                4242,
                new Data()
        );

        var strategy = new CheckFieldServerRunningStrategy(server);

        Assertions.assertTrue(strategy.isRunning());
    }

    @Test
    void givenServerNotIsRunning_WhenCallingStrategy_ThenShouldReturnFalse() {
        var server = new Server(
                4242,
                new Data()
        );
        server.setRunning(false);

        var strategy = new CheckFieldServerRunningStrategy(server);

        Assertions.assertFalse(strategy.isRunning());
    }
}