package com.fagi.server;

import com.fagi.model.Data;
import com.fagi.utility.JsonFileOperations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.File;

abstract class ServerTests {
    protected final int serverPort = 4242;
    protected Data data;

    @BeforeEach
    void setup() {
        cleanConfigFolder();
        data = Mockito.mock(Data.class);
    }

    @AfterEach
    void tearDown() {
        cleanConfigFolder();
    }

    private static void cleanConfigFolder() {
        var configFile = new File(Server.CONFIG_FILE);
        var inviteCodesFile = new File(JsonFileOperations.INVITE_CODES_FILE_PATH);

        if (configFile.exists()) {
            configFile.delete();
        }

        if (inviteCodesFile.exists()) {
            inviteCodesFile.delete();
        }
    }
}
