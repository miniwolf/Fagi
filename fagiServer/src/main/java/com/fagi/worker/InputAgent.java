package com.fagi.worker;

import com.fagi.encryption.AESKey;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.handler.InputHandler;

public interface InputAgent {
    void setUsername(String username);

    void setAes(EncryptionAlgorithm<AESKey> aes);

    void setSessionCreated(boolean sessionCreated);

    String getUsername();

    void setRunning(boolean running);

    InputHandler getInputHandler();
}
