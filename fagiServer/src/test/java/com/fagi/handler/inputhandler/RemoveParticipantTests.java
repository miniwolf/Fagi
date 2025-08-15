package com.fagi.handler.inputhandler;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.model.User;
import com.fagi.model.conversation.RemoveParticipantRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchConversation;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Unauthorized;
import com.fagi.util.DataTestUtil;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class RemoveParticipantTests extends BaseInputHandlerTest {
    void beforeEach() {
        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);
        when(inputAgent.getUsername()).thenReturn("Sender");
    }

    @Test
    void removingParticipantFromNonExistentConversation_ShouldReturnNoSuchConversation() {
        var notExistingConversationId = 12;
        inputHandler.handleInput(new RemoveParticipantRequest(
                "Sender",
                "Participant",
                notExistingConversationId
        ));
        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                NoSuchConversation.class
        );
    }

    @Test
    void nonParticipantRemovesParticipantFromConversation_ShouldReturnUnauthorized() {
        Conversation testConversation = new Conversation(
                42,
                "Test Conversation",
                ConversationType.Multi
        );
        testConversation.addUser("Random person");
        when(data.getConversation(Mockito.anyLong())).thenReturn(testConversation);
        inputHandler.handleInput(new RemoveParticipantRequest(
                "Sender",
                "Participant",
                42
        ));
        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                Unauthorized.class
        );
    }

    @Test
    void removingNonExistentUser_ShouldReturnNoSuchUser() {
        Conversation testConversation = new Conversation(
                42,
                "Test Conversation",
                ConversationType.Multi
        );
        testConversation.addUser("Sender");
        testConversation.addUser("Participant");
        when(data.getConversation(Mockito.anyLong())).thenReturn(testConversation);
        inputHandler.handleInput(new RemoveParticipantRequest(
                "Sender",
                "Participant",
                42
        ));
        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                NoSuchUser.class
        );
    }

    @Test
    void removingParticipant_ShouldRemoveThemFromConversation() {
        var partic = new User(
                "Participant",
                "password"
        );
        partic.addConversationID(42);
        partic.addConversationID(41);
        when(data.getUser("Participant")).thenReturn(partic);
        Conversation testConversation = new Conversation(
                42,
                "Test Conversation",
                ConversationType.Multi
        );
        testConversation.addUser("Sender");
        testConversation.addUser("Participant");
        when(data.getConversation(Mockito.anyLong())).thenReturn(testConversation);
        inputHandler.handleInput(new RemoveParticipantRequest(
                "Sender",
                "Participant",
                42
        ));

        User storedUser = DataTestUtil
                .verifyStoreUserCalled(
                        data,
                        1
                )
                .getValue();
        Conversation storedConversation = DataTestUtil
                .verifyStoreConversationCalled(
                        data,
                        1
                )
                .getValue();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        storedUser
                                .getConversationIDs()
                                .size()
                ),
                () -> Assertions.assertEquals(
                        41,
                        storedUser
                                .getConversationIDs()
                                .getFirst()
                ),
                () -> Assertions.assertEquals(
                        1,
                        storedConversation
                                .getParticipants()
                                .size()
                ),
                () -> Assertions.assertEquals(
                        "Sender",
                        storedConversation
                                .getParticipants()
                                .getFirst()
                )
        );

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                AllIsWell.class
        );
    }
}
