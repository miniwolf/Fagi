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
import com.fagi.model.Data;
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
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

import java.util.HashMap;

/**
 * A factory that makes sure that all handlers are registered in {@link InputHandler}s
 *
 * @author zargess
 * @see InputHandler
 * @see RequestHandler
 */
public class InputHandlerFactory {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException if called via reflection
     */
    private InputHandlerFactory() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    /**
     * Creates an {@link InputHandler} with all request handlers registered
     * @param inputAgent the agent that handles inputs for the user
     * @param outputAgent the agent that handles outputs for the user
     * @param conversationHandler a handler that adds messages to conversations and sends them to all online participants
     * @param data the object to read data
     * @return an {@link InputHandler} with all request handlers
     */
    public static InputHandler createInputHandler(
            InputAgent inputAgent,
            OutputAgent outputAgent,
            ConversationHandler conversationHandler,
            Data data) {
        var requestHandlers = new HashMap<Class<?>, RequestHandler<?>>();

        requestHandlers.put(
                CreateUser.class,
                new CreateUserRequestHandler(
                        outputAgent,
                        data
                )
        );
        requestHandlers.put(
                TextMessage.class,
                new TextMessageRequestHandler(
                        outputAgent,
                        conversationHandler,
                        data
                )
        );
        requestHandlers.put(
                Login.class,
                new LoginRequestHandler(
                        inputAgent,
                        outputAgent,
                        data
                )
        );
        requestHandlers.put(
                Logout.class,
                new LogoutRequestHandler(
                        inputAgent,
                        outputAgent,
                        data
                )
        );
        requestHandlers.put(
                FriendRequest.class,
                new FriendRequestRequestHandler(
                        data,
                        inputAgent,
                        outputAgent
                )
        );
        requestHandlers.put(
                GetFriendListRequest.class,
                new GetFriendListRequestHandler(
                        data,
                        inputAgent,
                        outputAgent
                )
        );
        requestHandlers.put(
                DeleteFriendRequest.class,
                new DeleteFriendRequestRequestHandler(
                        data,
                        inputAgent,
                        outputAgent
                )
        );
        requestHandlers.put(
                DeleteFriend.class,
                new DeleteFriendRequestHandler(
                        data,
                        inputAgent,
                        outputAgent
                )
        );
        requestHandlers.put(
                Session.class,
                new SessionRequestHandler(
                        data,
                        inputAgent,
                        outputAgent
                )
        );
        requestHandlers.put(
                AddParticipantRequest.class,
                new AddParticipantRequestHandler(
                        data,
                        outputAgent
                )
        );
        requestHandlers.put(
                CreateConversationRequest.class,
                new CreateConversationRequestHandler(
                        data,
                        inputAgent,
                        outputAgent
                )
        );
        requestHandlers.put(
                RemoveParticipantRequest.class,
                new RemoveParticipantRequestHandler(
                        data,
                        inputAgent,
                        outputAgent
                )
        );
        requestHandlers.put(
                UpdateHistoryRequest.class,
                new UpdateHistoryRequestHandler(
                        data,
                        outputAgent
                )
        );
        requestHandlers.put(
                GetConversationsRequest.class,
                new GetConversationsRequestHandler(
                        data,
                        outputAgent
                )
        );
        requestHandlers.put(
                GetAllConversationDataRequest.class,
                new GetAllConversationDataRequestHandler(
                        data,
                        outputAgent
                )
        );
        requestHandlers.put(
                SearchUsersRequest.class,
                new SearchUsersRequestHandler(
                        data,
                        inputAgent,
                        outputAgent
                )
        );
        requestHandlers.put(
                UserNameAvailableRequest.class,
                new UserNameAvailableRequestHandler(
                        data,
                        outputAgent
                )
        );

        return new InputHandler(requestHandlers);
    }
}
