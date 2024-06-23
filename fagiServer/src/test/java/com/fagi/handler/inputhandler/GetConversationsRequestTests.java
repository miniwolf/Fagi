package com.fagi.handler.inputhandler;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.conversation.ConversationFilter;
import com.fagi.conversation.ConversationType;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.mockhelpers.ConversationMocks;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.conversation.GetConversationsRequest;
import com.fagi.util.OutputAgentTestUtil;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static org.mockito.Mockito.when;

public class GetConversationsRequestTests {
    private OutputAgent outputAgent;
    private Data data;
    private InputHandler inputHandler;

    @BeforeEach
    void setup() {
        data = Mockito.mock(Data.class);
        InputAgent inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.spy(OutputAgent.class);
        ConversationHandler conversationHandler = new ConversationHandler(data);
        inputHandler = new InputHandler(
                inputAgent,
                outputAgent,
                conversationHandler,
                data
        );

        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);
    }

    @Test
    void userWithTwoConversations_ShouldGetTwoPlaceholderConversationsWhenNoConversationInRequest() {
        var user = new User(
                "fisk",
                "1234"
        );
        var con1 = ConversationMocks.createConversationWithRandomDataForUserAndRegisterInData(
                data,
                user,
                1
        );
        var con2 = ConversationMocks.createConversationWithRandomDataForUserAndRegisterInData(
                data,
                user,
                1
        );

        when(data.getUser(user.getUserName())).thenReturn(user);

        inputHandler.handleInput(new GetConversationsRequest(
                "fisk",
                Collections.emptyList()
        ));

        List<Conversation> conversations = OutputAgentTestUtil.captureResponses(
                outputAgent,
                Conversation.class,
                2
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        2,
                        conversations.size()
                ),
                () -> Assertions.assertEquals(
                        ConversationType.Placeholder,
                        conversations
                                .getFirst()
                                .getType()
                ),
                () -> Assertions.assertEquals(
                        ConversationType.Placeholder,
                        conversations
                                .getLast()
                                .getType()
                ),
                () -> Assertions.assertEquals(
                        1,
                        conversations
                                .getFirst()
                                .getParticipants()
                                .size()
                ),
                () -> Assertions.assertEquals(
                        user.getUserName(),
                        conversations
                                .getFirst()
                                .getParticipants()
                                .getFirst()
                ),
                () -> Assertions.assertEquals(
                        user.getUserName(),
                        conversations
                                .getLast()
                                .getParticipants()
                                .getFirst()
                ),
                () -> Assertions.assertEquals(
                        con1.getLastMessage(),
                        conversations
                                .getFirst()
                                .getLastMessage()
                ),
                () -> Assertions.assertEquals(
                        con2.getLastMessage(),
                        conversations
                                .getLast()
                                .getLastMessage()
                ),
                () -> Assertions.assertEquals(
                        con1.getLastMessageDate(),
                        conversations
                                .getFirst()
                                .getLastMessageDate()
                ),
                () -> Assertions.assertEquals(
                        con2.getLastMessageDate(),
                        conversations
                                .getLast()
                                .getLastMessageDate()
                )
        );
    }

    @Test
    void userWithOneConversation_ShouldGetNewMessagesSentSinceLastMessageWhenConversationInRequest() {
        var user = new User(
                "fisk",
                "1234"
        );
        var con = ConversationMocks.createConversationWithRandomDataForUserAndRegisterInData(
                data,
                user,
                10
        );

        when(data.getUser(user.getUserName())).thenReturn(user);

        inputHandler.handleInput(new GetConversationsRequest(
                user.getUserName(),
                List.of(new ConversationFilter(
                        con.getId(),
                        con
                                .getMessages()
                                .get(4)
                                .getMessageInfo()
                                .getTimestamp()
                ))
        ));

        ConversationDataUpdate dataUpdate = OutputAgentTestUtil.captureResponse(
                outputAgent,
                ConversationDataUpdate.class
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        con.getId(),
                        dataUpdate.id()
                ),
                () -> Assertions.assertEquals(
                        5,
                        dataUpdate
                                .conversationData()
                                .size()
                ),
                () -> Assertions.assertEquals(
                        con
                                .getMessages()
                                .subList(
                                        5,
                                        10
                                ),
                        dataUpdate.conversationData()
                ),
                () -> Assertions.assertEquals(
                        con.getLastMessage(),
                        dataUpdate.lastMessage()
                ),
                () -> Assertions.assertEquals(
                        new Timestamp(con
                                              .getLastMessageDate()
                                              .getTime()),
                        dataUpdate.lastMessageDate()
                )
        );
    }

    @Test
    void userWithNoConversations_ShouldGetNoResponse() {
        var user = new User(
                "fisk",
                "1234"
        );

        when(data.getUser(user.getUserName())).thenReturn(user);

        inputHandler.handleInput(new GetConversationsRequest(
                user.getUserName(),
                List.of(new ConversationFilter(
                        42,
                        new GregorianCalendar(
                                2020,
                                Calendar.MARCH,
                                2
                        ).getTime()
                ))
        ));

        OutputAgentTestUtil.verifyNoResponseOfType(
                outputAgent,
                Conversation.class
        );
        OutputAgentTestUtil.verifyNoResponseOfType(
                outputAgent,
                ConversationDataUpdate.class
        );
    }

    @Test
    void userHasTwoConversationsWithOneInFilter_ShouldReturnOnePlaceholderAndOneConversationUpdate() {
        var user = new User(
                "fisk",
                "1234"
        );
        var con1 = ConversationMocks.createConversationWithRandomDataForUserAndRegisterInData(
                data,
                user,
                2
        );
        var con2 = ConversationMocks.createConversationWithRandomDataForUserAndRegisterInData(
                data,
                user,
                2
        );

        when(data.getUser(user.getUserName())).thenReturn(user);

        inputHandler.handleInput(new GetConversationsRequest(
                user.getUserName(),
                List.of(new ConversationFilter(
                        con1.getId(),
                        con1.getLastMessageDate()
                ))
        ));

        ConversationDataUpdate dataUpdateConversation1 = OutputAgentTestUtil.captureResponse(
                outputAgent,
                ConversationDataUpdate.class
        );
        Conversation placeholderConversation2 = OutputAgentTestUtil.captureResponse(
                outputAgent,
                Conversation.class
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(con1.getId(), dataUpdateConversation1.id()),
                () -> Assertions.assertEquals(con2.getId(), placeholderConversation2.getId())
        );
    }
}
