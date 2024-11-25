package com.fagi.handler.inputhandler;

import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.model.User;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Unauthorized;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GetAllConversationDataRequestTests extends BaseInputHandlerTest {
    private final User USER = new User(
            "fisk",
            "1234"
    );

    void beforeEach() {
        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);
    }

    @Test
    void whenNoUserIsFound_ShouldGiveNoSuchUserResponse() {
        when(data.getUser(any())).thenReturn(null);

        inputHandler.handleInput(new GetAllConversationDataRequest(
                "fisk",
                42
        ));

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                NoSuchUser.class
        );
    }

    @Test
    void whenUserIsNotInConversation_ShouldGiveUnauthorizedResponse() {
        when(data.getUser(USER.getUserName())).thenReturn(USER);

        inputHandler.handleInput(new GetAllConversationDataRequest(
                USER.getUserName(),
                42
        ));

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                Unauthorized.class
        );
    }

    @Test
    void whenConversationHasNoMessages_ShouldGiveUpdateNoMessagesAndLasMessageReceivedAsConversationCreationDate() {
        when(data.getUser(USER.getUserName())).thenReturn(USER);

        var con = createConversationWithRandomDataForUserAndRegisterInData(
                USER,
                0
        );

        inputHandler.handleInput(new GetAllConversationDataRequest(
                USER.getUserName(),
                con.getId()
        ));

        var dataUpdate = OutputAgentTestUtil
                .captureResponse(
                        outputAgent,
                        ConversationDataUpdate.class
                )
                .access()
                .data();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        con.getId(),
                        dataUpdate.id()
                ),
                () -> Assertions.assertEquals(
                        con.getLastMessageDate(),
                        dataUpdate.lastMessageDate()
                ),
                () -> Assertions.assertNull(dataUpdate.lastMessage()),
                () -> Assertions.assertEquals(
                        0,
                        dataUpdate
                                .conversationData()
                                .size()
                )
        );
    }

    @Test
    void whenConversationHasMessages_ShouldGiveUpdateWithAllMessagesOfConversationAndCorrectLastMessageInfo() {
        when(data.getUser(USER.getUserName())).thenReturn(USER);

        var con = createConversationWithRandomDataForUserAndRegisterInData(
                USER,
                10
        );

        inputHandler.handleInput(new GetAllConversationDataRequest(
                USER.getUserName(),
                con.getId()
        ));

        var dataUpdate = OutputAgentTestUtil
                .captureResponse(
                        outputAgent,
                        ConversationDataUpdate.class
                )
                .access()
                .data();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        con.getId(),
                        dataUpdate.id()
                ),
                () -> Assertions.assertEquals(
                        con.getLastMessageDate(),
                        dataUpdate.lastMessageDate()
                ),
                () -> Assertions.assertEquals(
                        con.getLastMessage(),
                        dataUpdate.lastMessage()
                ),
                () -> Assertions.assertEquals(
                        con.getMessages(),
                        dataUpdate.conversationData()
                )
        );
    }
}
