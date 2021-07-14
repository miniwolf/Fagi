package com.fagi.handler;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.encryption.AES;
import com.fagi.model.CreateUser;
import com.fagi.model.Data;
import com.fagi.model.DeleteFriend;
import com.fagi.model.DeleteFriendRequest;
import com.fagi.model.Friend;
import com.fagi.model.FriendRequest;
import com.fagi.model.GetFriendListRequest;
import com.fagi.model.HistoryUpdates;
import com.fagi.model.InviteCodeContainer;
import com.fagi.model.Login;
import com.fagi.model.Logout;
import com.fagi.model.SearchUsersRequest;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.Session;
import com.fagi.model.User;
import com.fagi.model.UserLoggedIn;
import com.fagi.model.UserLoggedOut;
import com.fagi.model.UserNameAvailableRequest;
import com.fagi.model.conversation.AddParticipantRequest;
import com.fagi.model.conversation.CreateConversationRequest;
import com.fagi.model.conversation.GetConversationsRequest;
import com.fagi.model.conversation.RemoveParticipantRequest;
import com.fagi.model.conversation.UpdateHistoryRequest;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.message.MessageInfo;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.IllegalInviteCode;
import com.fagi.responses.NoSuchConversation;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Response;
import com.fagi.responses.Unauthorized;
import com.fagi.responses.UserExists;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record InputHandler(InputAgent inputAgent, OutputAgent out,
                           ConversationHandler conversationHandler, Data data) {
    public void handleInput(Object input) {
        if (input instanceof TextMessage arg) {
            MessageInfo messageInfo = arg.getMessageInfo();
            messageInfo.setTimestamp(new Timestamp(System.currentTimeMillis()));
            out.addResponse(handleTextMessage(arg));
        } else if (input instanceof Login arg) {
            out.addResponse(handleLogin(arg));
        } else if (input instanceof Logout) {
            out.addResponse(handleLogout());
            out.setRunning(false);
        } else if (input instanceof CreateUser arg) {
            out.addResponse(handleCreateUser(arg));
        } else if (input instanceof FriendRequest arg) {
            out.addResponse(handleFriendRequest(arg));
        } else if (input instanceof GetFriendListRequest) {
            out.addResponse(handleGetFriendRequest());
        } else if (input instanceof DeleteFriendRequest arg) {
            out.addResponse(handleDeleteFriendRequest(arg));
        } else if (input instanceof DeleteFriend arg) {
            out.addResponse(handleDeleteFriend(arg));
        } else if (input instanceof Session arg) {
            out.addResponse(handleSession(arg));
        } else if (input instanceof AddParticipantRequest request) {
            out.addResponse(handleAddParticipant(request));
        } else if (input instanceof CreateConversationRequest request) {
            Object response = handleCreateConversation(request);
            if (!(response instanceof NoSuchUser)) {
                out.addResponse(new AllIsWell());
            }
            out.addResponse(response);
        } else if (input instanceof RemoveParticipantRequest request) {
            out.addResponse(handleRemoveParticipant(request));
        } else if (input instanceof UpdateHistoryRequest request) {

            Object response = handleUpdateHistory(request);

            if ((response instanceof HistoryUpdates)) {
                out.addResponse(new AllIsWell());
            }
            out.addResponse(response);
        } else if (input instanceof GetConversationsRequest request) {

            handleGetConversations(request);
        } else if (input instanceof GetAllConversationDataRequest request) {

            Object result = handleGetAllConversationDataRequest(request);

            out.addResponse(result);
        } else if (input instanceof SearchUsersRequest request) {

            out.addResponse(handleSearchUsersRequest(request));
        } else if (input instanceof UserNameAvailableRequest request) {
            out.addResponse(handleUserNameAvailableRequest(request));
        } else {
            System.out.println("Unknown handle: " + input
                    .getClass()
                    .toString());
        }
    }

    private Object handleGetFriendRequest() {
        return getFriendList();
    }

    private FriendList getFriendList() {
        User user = data.getUser(inputAgent.getUsername());
        List<String> friendUsernames = user.getFriends();
        List<Friend> friends = new ArrayList<>();

        for (String friendUsername : friendUsernames) {
            friends.add(new Friend(friendUsername, data.isUserOnline(friendUsername)));
        }

        return new FriendList(new DefaultListAccess<>(friends));
    }

    private Object handleUserNameAvailableRequest(UserNameAvailableRequest request) {
        if (data.getUser(request.username()) == null) {
            return new AllIsWell();
        } else {
            return new UserExists();
        }
    }

    private Object handleSearchUsersRequest(SearchUsersRequest request) {
        User user = data.getUser(inputAgent.getUsername());
        out.addResponse(new AllIsWell());

        List<String> usernames = data
                .getUserNames()
                .stream()
                .filter(username -> username.startsWith(request.searchString()))
                .filter(username -> !username.equals(request.sender()))
                .sorted()
                .collect(Collectors.toList());

        List<String> friends = usernames
                .stream()
                .filter(username -> user
                        .getFriends()
                        .contains(username))
                .sorted()
                .collect(Collectors.toList());

        List<String> nonFriends = usernames
                .stream()
                .filter(username -> !friends.contains(username))
                .collect(Collectors.toList());

        return new SearchUsersResult(nonFriends);
    }

    private Object handleGetAllConversationDataRequest(GetAllConversationDataRequest request) {
        User user = data.getUser(request.sender());

        List<Long> conversationIDs = user.getConversationIDs();
        if (!conversationIDs.contains(request.id())) {
            return new Unauthorized();
        }
        Conversation conversation = data.getConversation(request.id());
        Date lastMessageDate = conversation.getLastMessageDate();
        Timestamp lastMessageReceived = new Timestamp(lastMessageDate.getTime());
        return new ConversationDataUpdate(request.id(),
                conversation.getMessages(),
                lastMessageReceived,
                conversation.getLastMessage()
        );
    }

    private void handleGetConversations(GetConversationsRequest request) {
        List<Long> conversationIDs = data
                .getUser(request.userName())
                .getConversationIDs();

        conversationIDs
                .stream()
                .filter(x -> request
                        .filters()
                        .stream()
                        .noneMatch(y -> y.id() == x))
                .forEach(x -> out.addResponse(data
                        .getConversation(x)
                        .getPlaceholder()));

        request
                .filters()
                .stream()
                .filter(x -> conversationIDs.contains(x.id()))
                .forEach(x -> {
                    Conversation conversation = data.getConversation(x.id());
                    Timestamp time = new Timestamp(x
                            .lastMessageDate()
                            .getTime());
                    Timestamp lastMessageReceived = new Timestamp(conversation
                            .getLastMessageDate()
                            .getTime());
                    ConversationDataUpdate res = new ConversationDataUpdate(x.id(),
                            conversation.getMessagesFromDate(time),
                            lastMessageReceived,
                            conversation.getLastMessage()
                    );

                    out.addResponse(res);
                });
    }

    private Object handleUpdateHistory(UpdateHistoryRequest request) {
        User user = data.getUser(request.sender());

        if (!user
                .getConversationIDs()
                .contains(request.conversationID())) {
            return new Unauthorized();
        }

        Conversation con = data.getConversation(request.conversationID());
        if (con == null) {
            return new NoSuchConversation();
        }

        List<TextMessage> res = con.getMessagesFromDate(new Timestamp(request
                .dateLastMessageReceived()
                .getTime()));

        return new HistoryUpdates(res, request.conversationID());
    }

    private Object handleRemoveParticipant(RemoveParticipantRequest request) {
        Conversation con = data.getConversation(request.id());
        if (con == null) {
            return new NoSuchConversation();
        }

        if (!con
                .getParticipants()
                .contains(request.sender())) {
            return new Unauthorized();
        }

        User user = data.getUser(request.participant());
        if (user == null) {
            return new NoSuchUser();
        }

        user.removeConversationID(request.id());
        con.removeUser(request.participant());
        data.storeConversation(con);
        data.storeUser(user);

        return new AllIsWell();
    }

    private Object handleCreateConversation(CreateConversationRequest request) {
        List<User> users = new ArrayList<>();
        for (String username : request.participants()) {
            User u = data.getUser(username);
            if (u == null) {
                return new NoSuchUser();
            }

            users.add(u);
        }

        Conversation con = data.createConversation(request.participants());

        for (User user : users) {
            user.addConversationID(con.getId());
            data.storeUser(user);
            boolean notCurrentUser = !Objects.equals(user.getUserName(), inputAgent.getUsername());
            if (notCurrentUser && data.isUserOnline(user.getUserName())) {
                OutputAgent outputAgent = data.getOutputAgent(user.getUserName());
                outputAgent.addResponse(con);
            }
        }

        data.storeConversation(con);

        return con;
    }

    private Object handleAddParticipant(AddParticipantRequest request) {
        Conversation con = data.getConversation(request.id());
        if (con == null) {
            return new NoSuchConversation();
        }

        List<String> conversationPariticipants = con.getParticipants();
        if (!conversationPariticipants.contains(request.sender())) {
            return new Unauthorized();
        }

        if (conversationPariticipants.contains(request.participant())) {
            return new UserExists();
        }

        User user = data.getUser(request.participant());
        if (user == null) {
            return new NoSuchUser();
        }

        con.addUser(user.getUserName());
        user.addConversationID(con.getId());

        if (data.isUserOnline(user.getUserName())) {
            OutputAgent outputAgent = data.getOutputAgent(user.getUserName());
            outputAgent.addResponse(con);
        }

        data.storeConversation(con);
        data.storeUser(user);

        return new AllIsWell();
    }

    private Object handleSession(Session arg) {
        AES aes = new AES(arg.key());
        inputAgent.setAes(aes);
        out.setAes(aes);
        inputAgent.setSessionCreated(true);
        return new AllIsWell();
    }

    private Object handleDeleteFriend(DeleteFriend arg) {
        System.out.println("Delete Friend");
        return data
                .getUser(inputAgent.getUsername())
                .removeFriend(data, arg.friendUsername());
    }

    private Object handleDeleteFriendRequest(DeleteFriendRequest arg) {
        System.out.println("DeleteFriendRequest");
        return data
                .getUser(inputAgent.getUsername())
                .removeFriendRequest(data, arg.friendUsername());
    }

    private Object handleTextMessage(TextMessage arg) {
        MessageInfo messageInfo = arg.getMessageInfo();
        Conversation con = data.getConversation(messageInfo.getConversationID());
        if (con == null) {
            return new NoSuchConversation();
        }

        List<String> conversationParticipants = con.getParticipants();
        if (!conversationParticipants.contains(messageInfo.getSender())) {
            return new Unauthorized();
        }

        conversationHandler.addMessage(arg);

        return new AllIsWell();
    }

    private Object handleLogin(Login arg) {
        System.out.println("Login");

        Response response = data.userLogin(arg.username(), arg.password(), out, inputAgent);

        if (!(response instanceof AllIsWell)) {
            return response;
        }

        out.setUserName(arg.username());
        inputAgent.setUsername(arg.username());
        List<String> friends = data
                .getUser(inputAgent.getUsername())
                .getFriends();
        for (String user : friends) {
            if (data.isUserOnline(user)) {
                OutputAgent outputAgent = data.getOutputAgent(user);
                outputAgent.addMessage(new UserLoggedIn(inputAgent.getUsername()));
            }
        }

        return response;
    }

    private Object handleLogout() {
        System.out.println("Logout");
        data.userLogout(inputAgent.getUsername());
        inputAgent.setRunning(false);

        List<String> friends = data
                .getUser(inputAgent.getUsername())
                .getFriends();
        for (String user : friends) {
            if (data.isUserOnline(user)) {
                OutputAgent outputAgent = data.getOutputAgent(user);
                outputAgent.addMessage(new UserLoggedOut(inputAgent.getUsername()));
            }
        }

        return new AllIsWell();
    }

    private Object handleCreateUser(CreateUser arg) {
        System.out.println("CreateUser");

        InviteCodeContainer inviteCodes = data.loadInviteCodes();
        int inviteCode = arg.inviteCode();
        if (!inviteCodes.contains(inviteCode)) {
            return new IllegalInviteCode();
        }

        try {
            Response response = data.createUser(arg.username(), arg.password());
            if (response instanceof AllIsWell) {
                inviteCodes.remove(inviteCode);
                data.storeInviteCodes(inviteCodes);
            }
            return response;
        } catch (Exception e) {
            return e;
        }
    }

    private Object handleFriendRequest(FriendRequest arg) {
        System.out.println("FriendRequest");
        User user = data.getUser(inputAgent.getUsername());
        Response response = user.requestFriend(data, arg);
        if (!(response instanceof AllIsWell)) {
            return response;
        }

        if (data.isUserOnline(arg.friendUsername())) {
            InputHandler inputHandler = data
                    .getInputAgent(arg.friendUsername())
                    .getInputHandler();
            inputHandler.handleInput(new GetFriendListRequest(arg.friendUsername()));
        }
        return getFriendList();
    }
}
