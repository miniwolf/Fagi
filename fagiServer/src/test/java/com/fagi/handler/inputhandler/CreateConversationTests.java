package com.fagi.handler.inputhandler;

import com.fagi.conversation.Conversation;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.mockhelpers.ConversationMocks;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.conversation.CreateConversationRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Response;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CreateConversationTests {
    private InputHandler inputHandler;
    @Mock private OutputAgent outputAgent;
    @Mock private InputAgent inputAgent;
    @Mock private Data data;

    @BeforeEach
    void setup() {
        var conversationHandler = new ConversationHandler(data);

        inputHandler = new InputHandler(
                inputAgent,
                outputAgent,
                conversationHandler,
                data
        );
    }

    @Test
    void creatingConversationWithNonExistingUser_ShouldResultIn_NoSuchUserResponse() {
        doReturn(null)
                .when(data)
                .getUser(anyString());

        inputHandler.handleInput(new CreateConversationRequest(Collections.singletonList("NotExistingUser")));

        var argumentCaptor = ArgumentCaptor.forClass(Response.class);
        Mockito
                .verify(
                        outputAgent,
                        times(1)
                )
                .addResponse(argumentCaptor.capture());

        Assertions.assertTrue(argumentCaptor.getValue() instanceof NoSuchUser);
    }

    @Test
    void creatingConversation_ShouldResultIn_AddingConversationIdToAllParticipants() {
        User self = new User(
                "Me",
                "Some password"
        );
        User other = new User(
                "Someone else",
                "Their password"
        );
        Conversation conversation = ConversationMocks.createConversation(
                self.getUserName(),
                other.getUserName()
        );

        doReturn(self)
                .when(data)
                .getUser(self.getUserName());
        doReturn(other)
                .when(data)
                .getUser(other.getUserName());
        doReturn(false)
                .when(data)
                .isUserOnline(anyString());
        doReturn(conversation)
                .when(data)
                .createConversation(anyList());

        inputHandler.handleInput(new CreateConversationRequest(Arrays.asList(
                self.getUserName(),
                other.getUserName()
        )));

        Assertions.assertAll(
                () -> Assertions.assertTrue(self
                                                    .getConversationIDs()
                                                    .contains(conversation.getId())),
                () -> Assertions.assertTrue(other
                                                    .getConversationIDs()
                                                    .contains(conversation.getId()))
        );
    }

    @Test
    void creatingConversation_ShouldResultIn_StoringParticipants() throws Exception {
        User self = new User(
                "Me",
                "Some password"
        );
        User other = new User(
                "Someone else",
                "Their password"
        );
        Conversation conversation = ConversationMocks.createConversation(
                self.getUserName(),
                other.getUserName()
        );

        doReturn(self)
                .when(data)
                .getUser(self.getUserName());
        doReturn(other)
                .when(data)
                .getUser(other.getUserName());
        doReturn(false)
                .when(data)
                .isUserOnline(anyString());
        doReturn(conversation)
                .when(data)
                .createConversation(anyList());

        inputHandler.handleInput(new CreateConversationRequest(Arrays.asList(
                self.getUserName(),
                other.getUserName()
        )));

        var argumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito
                .verify(
                        data,
                        times(2)
                )
                .storeUser(argumentCaptor.capture());

        var storedUsersList = argumentCaptor.getAllValues();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        2,
                        storedUsersList.size()
                ),
                () -> Assertions.assertEquals(
                        self.getUserName(),
                        storedUsersList
                                .get(0)
                                .getUserName()
                ),
                () -> Assertions.assertEquals(
                        other.getUserName(),
                        storedUsersList
                                .get(1)
                                .getUserName()
                )
        );
    }

    @Test
    void creatingConversation_ShouldNotResultIn_DataRequestedForCurrentUserOuptAgent() throws Exception {
        User self = new User(
                "Me",
                "Some password"
        );
        User other = new User(
                "Someone else",
                "Their password"
        );
        Conversation conversation = ConversationMocks.createConversation(
                self.getUserName(),
                other.getUserName()
        );

        doReturn(self)
                .when(data)
                .getUser(self.getUserName());
        doReturn(other)
                .when(data)
                .getUser(other.getUserName());
        doReturn(false)
                .when(data)
                .isUserOnline(anyString());
        doReturn(conversation)
                .when(data)
                .createConversation(anyList());
        doReturn(self.getUserName())
                .when(inputAgent)
                .getUsername();

        inputHandler.handleInput(new CreateConversationRequest(Arrays.asList(
                self.getUserName(),
                other.getUserName()
        )));

        Mockito
                .verify(
                        data,
                        never()
                )
                .getOutputAgent(self.getUserName());
    }

    @Test
    void creatingConversation_ShouldResultIn_NotifyingOnlineParticipants() throws Exception {
        User self = new User(
                "Me",
                "Some password"
        );
        User other = new User(
                "Someone else",
                "Their password"
        );
        User different = new User(
                "Some third person",
                "Their different password"
        );
        User someOfflineUser = new User(
                "Some offline person",
                "Their offline password"
        );
        Conversation conversation = ConversationMocks.createConversation(
                self.getUserName(),
                other.getUserName(),
                different.getUserName(),
                someOfflineUser.getUserName()
        );

        OutputAgent otherOutputAgent = Mockito.mock(OutputAgent.class);

        doReturn(self)
                .when(data)
                .getUser(self.getUserName());
        doReturn(other)
                .when(data)
                .getUser(other.getUserName());
        doReturn(different)
                .when(data)
                .getUser(different.getUserName());
        doReturn(someOfflineUser)
                .when(data)
                .getUser(someOfflineUser.getUserName());
        doReturn(true)
                .when(data)
                .isUserOnline(different.getUserName());
        doReturn(true)
                .when(data)
                .isUserOnline(other.getUserName());
        doReturn(false)
                .when(data)
                .isUserOnline(someOfflineUser.getUserName());
        doReturn(conversation)
                .when(data)
                .createConversation(anyList());
        doReturn(self.getUserName())
                .when(inputAgent)
                .getUsername();
        doReturn(otherOutputAgent)
                .when(data)
                .getOutputAgent(other.getUserName());
        doReturn(otherOutputAgent)
                .when(data)
                .getOutputAgent(different.getUserName());

        inputHandler.handleInput(new CreateConversationRequest(Arrays.asList(
                self.getUserName(),
                other.getUserName(),
                different.getUserName(),
                someOfflineUser.getUserName()
        )));

        var argumentCaptor = ArgumentCaptor.forClass(Conversation.class);
        Mockito
                .verify(
                        otherOutputAgent,
                        times(2)
                )
                .addResponse(argumentCaptor.capture());

        List<Conversation> conversations = argumentCaptor.getAllValues();
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        2,
                        conversations.size()
                ),
                () -> Assertions.assertEquals(
                        conversation,
                        conversations.get(0)
                ),
                () -> Assertions.assertEquals(
                        conversation,
                        conversations.get(1)
                )
        );
    }

    @Test
    void creatingConversation_ShouldResultIn_ConversationBeingStored() throws Exception {
        User self = new User(
                "Me",
                "Some password"
        );
        User other = new User(
                "Someone else",
                "Their password"
        );
        Conversation conversation = ConversationMocks.createConversation(
                self.getUserName(),
                other.getUserName()
        );

        doReturn(self)
                .when(data)
                .getUser(self.getUserName());
        doReturn(other)
                .when(data)
                .getUser(other.getUserName());
        doReturn(false)
                .when(data)
                .isUserOnline(anyString());
        doReturn(conversation)
                .when(data)
                .createConversation(anyList());

        inputHandler.handleInput(new CreateConversationRequest(Arrays.asList(
                self.getUserName(),
                other.getUserName()
        )));

        var argumentCaptor = ArgumentCaptor.forClass(Conversation.class);
        Mockito
                .verify(
                        data,
                        times(1)
                )
                .storeConversation(argumentCaptor.capture());

        Assertions.assertEquals(
                conversation,
                argumentCaptor.getValue()
        );
    }

    @Test
    void creatingConversation_ShouldResultIn_AllIsWellResponse() throws Exception {
        User self = new User(
                "Me",
                "Some password"
        );
        User other = new User(
                "Someone else",
                "Their password"
        );
        Conversation conversation = ConversationMocks.createConversation(
                self.getUserName(),
                other.getUserName()
        );

        doReturn(self)
                .when(data)
                .getUser(self.getUserName());
        doReturn(other)
                .when(data)
                .getUser(other.getUserName());
        doReturn(false)
                .when(data)
                .isUserOnline(anyString());
        doReturn(conversation)
                .when(data)
                .createConversation(anyList());

        inputHandler.handleInput(new CreateConversationRequest(Arrays.asList(
                self.getUserName(),
                other.getUserName()
        )));

        var argumentCaptor = ArgumentCaptor.forClass(AllIsWell.class);
        Mockito
                .verify(
                        outputAgent,
                        times(2)
                )
                .addResponse(argumentCaptor.capture());

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        2,
                        argumentCaptor
                                .getAllValues()
                                .size()
                ),
                () -> Assertions.assertNotNull(argumentCaptor
                                                       .getAllValues()
                                                       .get(0))
        );
    }

    @Test
    void creatingConversation_ShouldResultIn_ConversationResponse() throws Exception {
        User self = new User(
                "Me",
                "Some password"
        );
        User other = new User(
                "Someone else",
                "Their password"
        );
        Conversation conversation = ConversationMocks.createConversation(
                self.getUserName(),
                other.getUserName()
        );

        doReturn(self)
                .when(data)
                .getUser(self.getUserName());
        doReturn(other)
                .when(data)
                .getUser(other.getUserName());
        doReturn(false)
                .when(data)
                .isUserOnline(anyString());
        doReturn(conversation)
                .when(data)
                .createConversation(anyList());

        inputHandler.handleInput(new CreateConversationRequest(Arrays.asList(
                self.getUserName(),
                other.getUserName()
        )));

        var argumentCaptor = ArgumentCaptor.forClass(Conversation.class);
        Mockito
                .verify(
                        outputAgent,
                        times(2)
                )
                .addResponse(argumentCaptor.capture());

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        2,
                        argumentCaptor
                                .getAllValues()
                                .size()
                ),
                () -> Assertions.assertEquals(
                        conversation,
                        argumentCaptor
                                .getAllValues()
                                .get(1)
                )
        );
    }
}
