package com.fagi.handler;

import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.handler.request.RequestHandler;
import com.fagi.handler.request.auth.LoginRequestHandler;
import com.fagi.handler.request.auth.LogoutRequestHandler;
import com.fagi.handler.request.auth.SessionRequestHandler;
import com.fagi.handler.request.conversation.AddParticipantRequestHandler;
import com.fagi.handler.request.conversation.CreateConversationRequestHandler;
import com.fagi.handler.request.conversation.GetAllConversationDataRequestHandler;
import com.fagi.handler.request.conversation.GetConversationsRequestHandler;
import com.fagi.handler.request.conversation.RemoveParticipantRequestHandler;
import com.fagi.handler.request.conversation.TextMessageRequestHandler;
import com.fagi.handler.request.conversation.UpdateHistoryRequestHandler;
import com.fagi.handler.request.friend.DeleteFriendRequestHandler;
import com.fagi.handler.request.friend.DeleteFriendRequestRequestHandler;
import com.fagi.handler.request.friend.FriendRequestRequestHandler;
import com.fagi.handler.request.friend.GetFriendListRequestHandler;
import com.fagi.handler.request.user.CreateUserRequestHandler;
import com.fagi.handler.request.user.SearchUsersRequestHandler;
import com.fagi.handler.request.user.UserNameAvailableRequestHandler;
import com.fagi.model.CreateUser;
import com.fagi.model.DeleteFriend;
import com.fagi.model.DeleteFriendRequest;
import com.fagi.model.FriendRequest;
import com.fagi.model.GetFriendListRequest;
import com.fagi.model.Login;
import com.fagi.model.Logout;
import com.fagi.model.SearchUsersRequest;
import com.fagi.model.Session;
import com.fagi.model.UserNameAvailableRequest;
import com.fagi.model.conversation.AddParticipantRequest;
import com.fagi.model.conversation.CreateConversationRequest;
import com.fagi.model.conversation.GetConversationsRequest;
import com.fagi.model.conversation.RemoveParticipantRequest;
import com.fagi.model.conversation.UpdateHistoryRequest;
import com.fagi.model.messages.message.TextMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

class InputHandlerFactoryTest {
    @Test
    void testUnsupportedOperationExceptionThrownWhenNewingClass() {
        var constructors = InputHandlerFactory.class.getDeclaredConstructors();

        Assertions.assertEquals(
                1,
                constructors.length
        );

        var constructor = constructors[0];

        constructor.setAccessible(true);

        InvocationTargetException invocationTargetException = Assertions.assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        Assertions.assertInstanceOf(
                UnsupportedOperationException.class,
                invocationTargetException.getCause()
        );

        Assertions.assertEquals(
                "Utility class - do not instantiate",
                invocationTargetException
                        .getCause()
                        .getMessage()
        );
    }

    @Test
    void testAllEntriesInCreatedInputHandlerMatchesRequestClass() {
        var inputHandler = InputHandlerFactory.createInputHandler(
                null,
                null,
                null,
                null
        );

        for (Map.Entry<Class<?>, RequestHandler<?>> entry : inputHandler
                .handlers()
                .entrySet()) {
            Assertions.assertEquals(
                    entry.getKey(),
                    entry
                            .getValue()
                            .getRequestClass()
            );
        }
    }

    @Test
    void testCreatedInputHandlerContainsAllExpectedRequestToRequestHandlerPairs() {
        var inputHandler = InputHandlerFactory.createInputHandler(
                null,
                null,
                null,
                null
        );

        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(CreateUserRequestHandler.class, inputHandler.handlers().get(CreateUser.class)),
                () -> Assertions.assertInstanceOf(TextMessageRequestHandler.class, inputHandler.handlers().get(TextMessage.class)),
                () -> Assertions.assertInstanceOf(LoginRequestHandler.class, inputHandler.handlers().get(Login.class)),
                () -> Assertions.assertInstanceOf(LogoutRequestHandler.class, inputHandler.handlers().get(Logout.class)),
                () -> Assertions.assertInstanceOf(FriendRequestRequestHandler.class, inputHandler.handlers().get(FriendRequest.class)),
                () -> Assertions.assertInstanceOf(GetFriendListRequestHandler.class, inputHandler.handlers().get(GetFriendListRequest.class)),
                () -> Assertions.assertInstanceOf(DeleteFriendRequestRequestHandler.class, inputHandler.handlers().get(DeleteFriendRequest.class)),
                () -> Assertions.assertInstanceOf(DeleteFriendRequestHandler.class, inputHandler.handlers().get(DeleteFriend.class)),
                () -> Assertions.assertInstanceOf(SessionRequestHandler.class, inputHandler.handlers().get(Session.class)),
                () -> Assertions.assertInstanceOf(AddParticipantRequestHandler.class, inputHandler.handlers().get(AddParticipantRequest.class)),
                () -> Assertions.assertInstanceOf(CreateConversationRequestHandler.class, inputHandler.handlers().get(CreateConversationRequest.class)),
                () -> Assertions.assertInstanceOf(RemoveParticipantRequestHandler.class, inputHandler.handlers().get(RemoveParticipantRequest.class)),
                () -> Assertions.assertInstanceOf(UpdateHistoryRequestHandler.class, inputHandler.handlers().get(UpdateHistoryRequest.class)),
                () -> Assertions.assertInstanceOf(GetConversationsRequestHandler.class, inputHandler.handlers().get(GetConversationsRequest.class)),
                () -> Assertions.assertInstanceOf(GetAllConversationDataRequestHandler.class, inputHandler.handlers().get(GetAllConversationDataRequest.class)),
                () -> Assertions.assertInstanceOf(SearchUsersRequestHandler.class, inputHandler.handlers().get(SearchUsersRequest.class)),
                () -> Assertions.assertInstanceOf(UserNameAvailableRequestHandler.class, inputHandler.handlers().get(UserNameAvailableRequest.class))
        );
    }
}