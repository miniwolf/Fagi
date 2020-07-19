package com.fagi.handler.inputhandler;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.conversation.AddParticipantRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchConversation;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Unauthorized;
import com.fagi.responses.UserExists;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AddParticipantTests {
    private static final String SENDER_USERNAME = "sender username";
    private static final User NEW_PARTICIPANT = new User("new participant username", "some password");
    private static final long CONVERSATION_ID = 42;
    private InputHandler inputHandler;
    private AddParticipantRequest addParticipantRequest;
    @Mock private OutputAgent outputAgent;
    @Mock private InputAgent inputAgent;
    @Mock private Data data;

    @BeforeEach
    void setup() {
        var conversationHandler = new ConversationHandler(data);

        inputHandler = new InputHandler(inputAgent, outputAgent, conversationHandler, data);
        addParticipantRequest = new AddParticipantRequest(SENDER_USERNAME,
                                                          NEW_PARTICIPANT.getUserName(),
                                                          CONVERSATION_ID
        );
    }

    @Test
    void whenAddingParticipantToNonExistingConversation_ShouldResultInNoSuchConversationResponse() {
        doReturn(null)
                .when(data)
                .getConversation(anyLong());

        inputHandler.handleInput(addParticipantRequest);

        verify(outputAgent, times(1)).addResponse(any(NoSuchConversation.class));
    }

    @Test
    void whenAddingParticipantToConversationUserIsNotIn_ShouldResultInUnauthorizedResponse() {
        mockConversation();

        inputHandler.handleInput(addParticipantRequest);

        verify(outputAgent, times(1)).addResponse(any(Unauthorized.class));
    }

    @Test
    void whenNewParticipantIsAlreadyInConversation_ShouldResultInUserExistsResponse() {
        mockConversation(SENDER_USERNAME, NEW_PARTICIPANT.getUserName());

        inputHandler.handleInput(addParticipantRequest);

        verify(outputAgent, times(1)).addResponse(any(UserExists.class));
    }

    @Test
    void whenNewParticipantUsernameIsNotAnExistingUser_ShouldResultInNoSuchUserResponse() {
        mockConversation(SENDER_USERNAME);

        inputHandler.handleInput(addParticipantRequest);

        verify(outputAgent, times(1)).addResponse(any(NoSuchUser.class));
    }

    @Test
    void whenAddingUserToConversation_ShouldResultInNewParticipantBeingInConversationParticipantList() {
        Conversation conversation = mockConversation(SENDER_USERNAME);
        mockNewParticipantUser(false);

        inputHandler.handleInput(addParticipantRequest);

        assertTrue(conversation
                           .getParticipants()
                           .contains(NEW_PARTICIPANT.getUserName()));
    }

    @Test
    void whenAddingUserToConversation_ShouldResultInConversationAddedToTheUsersConversationList() {
        mockConversation(SENDER_USERNAME);
        mockNewParticipantUser(false);

        inputHandler.handleInput(addParticipantRequest);

        assertTrue(NEW_PARTICIPANT
                           .getConversationIDs()
                           .contains(CONVERSATION_ID));
    }

    @Test
    void whenAddingOnlineUserToConversation_ShouldResultInSendingConversationToThatUser() {
        Conversation conversation = mockConversation(SENDER_USERNAME);
        OutputAgent outputAgent = mockNewParticipantUser(true).orElseThrow(() -> new AssertionError(
                "Mocking new participant should return the OutputAgent"));

        inputHandler.handleInput(addParticipantRequest);

        verify(outputAgent, times(1)).addResponse(conversation);
    }

    @Test
    void whenAddingUserToConversation_ShouldResultInConversationBeingStored() {
        Conversation conversation = mockConversation(SENDER_USERNAME);
        mockNewParticipantUser(false);

        inputHandler.handleInput(addParticipantRequest);

        verify(data, times(1)).storeConversation(conversation);
    }

    @Test
    void whenAddingUserToConversation_ShouldResultInNewParticipantBeingStored() {
        mockConversation(SENDER_USERNAME);
        mockNewParticipantUser(false);

        inputHandler.handleInput(addParticipantRequest);

        verify(data, times(1)).storeUser(NEW_PARTICIPANT);
    }

    @Test
    void whenAddingOfflineUserToConversation_ShouldResultInAllIsWellResponse() {
        mockConversation(SENDER_USERNAME);
        mockNewParticipantUser(false);

        inputHandler.handleInput(addParticipantRequest);

        verify(outputAgent, times(1)).addResponse(any(AllIsWell.class));
    }

    @Test
    void whenAddingOnlineUserToConversation_ShouldResultInAllIsWellResponse() {
        mockConversation(SENDER_USERNAME);
        mockNewParticipantUser(true);

        inputHandler.handleInput(addParticipantRequest);

        verify(outputAgent, times(1)).addResponse(any(AllIsWell.class));
    }

    private Conversation mockConversation(String... participants) {
        var conversation = new Conversation(CONVERSATION_ID, "Some conversation", ConversationType.Single);

        conversation
                .getParticipants()
                .addAll(Arrays.asList(participants));

        doReturn(conversation)
                .when(data)
                .getConversation(anyLong());

        return conversation;
    }

    private Optional<OutputAgent> mockNewParticipantUser(boolean isOnline) {
        doReturn(NEW_PARTICIPANT)
                .when(data)
                .getUser(NEW_PARTICIPANT.getUserName());

        doReturn(isOnline)
                .when(data)
                .isUserOnline(NEW_PARTICIPANT.getUserName());

        if (isOnline) {
            OutputAgent newParticipantOutputAgent = Mockito.mock(OutputAgent.class);
            doReturn(newParticipantOutputAgent)
                    .when(data)
                    .getOutputAgent(anyString());
            return Optional.of(newParticipantOutputAgent);
        }

        return Optional.empty();
    }
}
