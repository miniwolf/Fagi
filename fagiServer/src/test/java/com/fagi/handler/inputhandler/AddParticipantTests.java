package com.fagi.handler.inputhandler;

import com.fagi.conversation.Conversation;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.conversation.AddParticipantRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchConversation;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Unauthorized;
import com.fagi.responses.UserExists;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AddParticipantTests extends BaseInputHandlerTest {
    private static final String SENDER_USERNAME = "sender username";
    private static User newParticipant;
    private static final long CONVERSATION_ID = 42;
    private AddParticipantRequest addParticipantRequest;

    void beforeEach() {
        newParticipant = new User(
                "new participant username",
                "some password"
        );

        addParticipantRequest = new AddParticipantRequest(
                SENDER_USERNAME,
                newParticipant.getUserName(),
                CONVERSATION_ID
        );
    }

    @Test
    void whenAddingParticipantToNonExistingConversation_ShouldResultInNoSuchConversationResponse() {
        doReturn(null)
                .when(data)
                .getConversation(anyLong());

        inputHandler.handleInput(addParticipantRequest);

        verify(
                outputAgent,
                times(1)
        ).addResponse(any(NoSuchConversation.class));
    }

    @Test
    void whenAddingParticipantToConversationUserIsNotIn_ShouldResultInUnauthorizedResponse() {
        mockConversationAndRegisterInData();

        inputHandler.handleInput(addParticipantRequest);

        verify(
                outputAgent,
                times(1)
        ).addResponse(any(Unauthorized.class));
    }

    @Test
    void whenNewParticipantIsAlreadyInConversation_ShouldResultInUserExistsResponse() {
        mockConversationAndRegisterInData(
                SENDER_USERNAME,
                newParticipant.getUserName()
        );

        inputHandler.handleInput(addParticipantRequest);

        verify(
                outputAgent,
                times(1)
        ).addResponse(any(UserExists.class));
    }

    @Test
    void whenNewParticipantUsernameIsNotAnExistingUser_ShouldResultInNoSuchUserResponse() {
        mockConversationAndRegisterInData(SENDER_USERNAME);

        inputHandler.handleInput(addParticipantRequest);

        verify(
                outputAgent,
                times(1)
        ).addResponse(any(NoSuchUser.class));
    }

    @Test
    void whenAddingUserToConversation_ShouldResultInNewParticipantBeingInConversationParticipantList() {
        Conversation conversation = mockConversationAndRegisterInData(SENDER_USERNAME);
        mockOnlineStatusOfUser(
                data,
                newParticipant,
                false
        );

        inputHandler.handleInput(addParticipantRequest);

        assertTrue(conversation
                           .getParticipants()
                           .contains(newParticipant.getUserName()));
    }

    @Test
    void whenAddingUserToConversation_ShouldResultInConversationAddedToTheUsersConversationList() {
        mockConversationAndRegisterInData(SENDER_USERNAME);
        mockOnlineStatusOfUser(
                data,
                newParticipant,
                false
        );

        inputHandler.handleInput(addParticipantRequest);

        assertTrue(newParticipant
                           .getConversationIDs()
                           .contains(CONVERSATION_ID));
    }

    @Test
    void whenAddingOnlineUserToConversation_ShouldResultInSendingConversationToThatUser() {
        Conversation conversation = mockConversationAndRegisterInData(SENDER_USERNAME);
        OutputAgent outputAgent = mockOnlineStatusOfUser(
                data,
                newParticipant,
                true
        ).orElseThrow(() -> new AssertionError("Mocking new participant should return the OutputAgent"));

        inputHandler.handleInput(addParticipantRequest);

        verify(
                outputAgent,
                times(1)
        ).addResponse(conversation);
    }

    @Test
    void whenAddingUserToConversation_ShouldResultInConversationBeingStored() {
        Conversation conversation = mockConversationAndRegisterInData(SENDER_USERNAME);
        mockOnlineStatusOfUser(
                data,
                newParticipant,
                false
        );

        inputHandler.handleInput(addParticipantRequest);

        verify(
                data,
                times(1)
        ).storeConversation(conversation);
    }

    @Test
    void whenAddingUserToConversation_ShouldResultInNewParticipantBeingStored() {
        mockConversationAndRegisterInData(SENDER_USERNAME);
        mockOnlineStatusOfUser(
                data,
                newParticipant,
                false
        );

        inputHandler.handleInput(addParticipantRequest);

        verify(
                data,
                times(1)
        ).storeUser(newParticipant);
    }

    @Test
    void whenAddingOfflineUserToConversation_ShouldResultInAllIsWellResponse() {
        mockConversationAndRegisterInData(SENDER_USERNAME);
        mockOnlineStatusOfUser(
                data,
                newParticipant,
                false
        );

        inputHandler.handleInput(addParticipantRequest);

        verify(
                outputAgent,
                times(1)
        ).addResponse(any(AllIsWell.class));
    }

    @Test
    void whenAddingOnlineUserToConversation_ShouldResultInAllIsWellResponse() {
        mockConversationAndRegisterInData(SENDER_USERNAME);
        mockOnlineStatusOfUser(
                data,
                newParticipant,
                true
        );

        inputHandler.handleInput(addParticipantRequest);

        verify(
                outputAgent,
                times(1)
        ).addResponse(any(AllIsWell.class));
    }

    private Optional<OutputAgent> mockOnlineStatusOfUser(
            Data data,
            User user,
            boolean isOnline) {
        doReturn(user)
                .when(data)
                .getUser(user.getUserName());

        doReturn(isOnline)
                .when(data)
                .isUserOnline(user.getUserName());

        if (isOnline) {
            OutputAgent newParticipantOutputAgent = Mockito.mock(OutputAgent.class);
            doReturn(newParticipantOutputAgent)
                    .when(data)
                    .getOutputAgent(user.getUserName());
            return Optional.of(newParticipantOutputAgent);
        }

        return Optional.empty();
    }
}
