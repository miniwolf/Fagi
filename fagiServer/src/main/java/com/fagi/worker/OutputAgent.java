package com.fagi.worker;

import com.fagi.encryption.AESKey;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.model.messages.InGoingMessages;

public interface OutputAgent {
    void setAes(EncryptionAlgorithm<AESKey> aes);

    void addMessage(InGoingMessages message);

    void addResponse(Object responseObj);

    void setUserName(String userName);

    void setRunning(boolean running);
}
