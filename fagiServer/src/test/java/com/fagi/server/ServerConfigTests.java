package com.fagi.server;

import com.fagi.config.ServerConfig;
import com.fagi.encryption.Encryption;
import com.fagi.encryption.RSA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;

class ServerConfigTests extends ServerTests {
    @Test
    void givenNoServerConfigFileExists_WhenNewServerObjectIsCreated_ThenConfigFileExists() {
        File serverConfigFile = new File(Server.CONFIG_FILE);

        Assumptions.assumeFalse(serverConfigFile.exists());

        new Server(
                serverPort,
                data
        );

        Assertions.assertTrue(serverConfigFile.exists());
    }

    @Test
    void whenServerConfigExists_ThenConfigContainsValidServerConfiguration() throws IOException, ClassNotFoundException {
        new Server(
                serverPort,
                data
        );

        var serverConfig = ServerConfig.pathToServerConfig(Server.CONFIG_FILE);


        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        serverPort,
                        serverConfig.getPort()
                ),
                () -> Assertions.assertEquals(
                        "test",
                        serverConfig.getName()
                ),
                () -> Assertions.assertEquals(
                        "127.0.0.1",
                        serverConfig.getIp()
                ),
                () -> {
                    Assertions.assertNotNull(serverConfig.getServerKey());

                    var testMessage = "test message";

                    var configRsa = new RSA(new KeyPair(
                            serverConfig.getServerKey(),
                            null
                    ));

                    var encryptedMessage = configRsa.encrypt(testMessage.getBytes());

                    Assertions.assertEquals(
                            testMessage,
                            new String(Encryption
                                               .getInstance()
                                               .getRSA()
                                               .decrypt(encryptedMessage))
                    );
                }
        );
    }

    @Test
    void givenServerNewlyCreated_ThenShouldBeRunning() {
        var server = new Server(
                serverPort,
                data
        );

        Assertions.assertTrue(server.isRunning());
    }
}
