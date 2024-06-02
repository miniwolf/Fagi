package com.fagi.util;

import com.fagi.conversation.Conversation;
import com.fagi.model.Data;
import com.fagi.model.User;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;

public final class DataTestUtil {
    public DataTestUtil() {
        // Disallowing newing class
    }

    public static ArgumentCaptor<User> verifyStoreUserCalled(Data dataMock, int numberOfCalls) {
        var argumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito
                .verify(
                        dataMock,
                        times(numberOfCalls)
                )
                .storeUser(argumentCaptor.capture());
        return argumentCaptor;
    }
    public static ArgumentCaptor<Conversation> verifyStoreConversationCalled(Data dataMock, int numberOfCalls) {
        var argumentCaptor = ArgumentCaptor.forClass(Conversation.class);
        Mockito
                .verify(
                        dataMock,
                        times(numberOfCalls)
                )
                .storeConversation(argumentCaptor.capture());
        return argumentCaptor;
    }
}
