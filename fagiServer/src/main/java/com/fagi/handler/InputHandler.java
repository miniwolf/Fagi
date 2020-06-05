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
import com.fagi.model.InviteCode;
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

public class InputHandler {
    private InputAgent inputAgent;
    private OutputAgent out;
    private ConversationHandler conversationHandler;
    private Data data;

    public InputHandler(
            InputAgent inputAgent,
            OutputAgent out,
            ConversationHandler conversationHandler,
            Data data) {
        this.inputAgent = inputAgent;
        this.out = out;
        this.conversationHandler = conversationHandler;
        this.data = data;
    }

    public void handleInput(Object input) {
        if (input instanceof TextMessage) {
            TextMessage arg = (TextMessage) input;
            MessageInfo messageInfo = arg.getMessageInfo();
            messageInfo.setTimestamp(new Timestamp(System.currentTimeMillis()));
            out.addResponse(handleTextMessage(arg));
        } else if (input instanceof Login) {
            Login arg = (Login) input;
            out.addResponse(handleLogin(arg));
        } else if (input instanceof Logout) {
            out.addResponse(handleLogout());
            out.setRunning(false);
        } else if (input instanceof CreateUser) {
            CreateUser arg = (CreateUser) input;
            out.addResponse(handleCreateUser(arg));
        } else if (input instanceof FriendRequest) {
            FriendRequest arg = (FriendRequest) input;
            out.addResponse(handleFriendRequest(arg));
        } else if (input instanceof GetFriendListRequest) {
            GetFriendListRequest arg = (GetFriendListRequest) input;
            out.addResponse(handleGetFriendRequest(arg));
        } else if (input instanceof DeleteFriendRequest) {
            DeleteFriendRequest arg = (DeleteFriendRequest) input;
            out.addResponse(handleDeleteFriendRequest(arg));
        } else if (input instanceof DeleteFriend) {
            DeleteFriend arg = (DeleteFriend) input;
            out.addResponse(handleDeleteFriend(arg));
        } else if (input instanceof Session) {
            Session arg = (Session) input;
            out.addResponse(handleSession(arg));
        } else if (input instanceof AddParticipantRequest) {
            AddParticipantRequest request = (AddParticipantRequest) input;
            out.addResponse(handleAddParticipant(request));
        } else if (input instanceof CreateConversationRequest) {
            CreateConversationRequest request = (CreateConversationRequest) input;
            Object response = handleCreateConversation(request);
            if (!(response instanceof NoSuchUser)) {
                out.addResponse(new AllIsWell());
            }
            out.addResponse(response);
        } else if (input instanceof RemoveParticipantRequest) {
            RemoveParticipantRequest request = (RemoveParticipantRequest) input;
            out.addResponse(handleRemoveParticipant(request));
        } else if (input instanceof UpdateHistoryRequest) {
            UpdateHistoryRequest request = (UpdateHistoryRequest) input;

            Object response = handleUpdateHistory(request);

            if ((response instanceof HistoryUpdates)) {
                out.addResponse(new AllIsWell());
            }
            out.addResponse(response);
        } else if (input instanceof GetConversationsRequest) {
            GetConversationsRequest request = (GetConversationsRequest) input;

            handleGetConversations(request);
        } else if (input instanceof GetAllConversationDataRequest) {
            GetAllConversationDataRequest request = (GetAllConversationDataRequest) input;

            Object result = handleGetAllConversationDataRequest(request);

            out.addResponse(result);
        } else if (input instanceof SearchUsersRequest) {
            SearchUsersRequest request = (SearchUsersRequest) input;

            out.addResponse(handleSearchUsersRequest(request));
        } else if (input instanceof UserNameAvailableRequest) {
            UserNameAvailableRequest request = (UserNameAvailableRequest) input;
            out.addResponse(handleUserNameAvailableRequest(request));
        } else {
            System.out.println("Unknown handle: " + input
                    .getClass()
                    .toString());
        }
    }

    private Object handleGetFriendRequest(GetFriendListRequest arg) {
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
        if (data.getUser(request.getUsername()) == null) {
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
                .filter(username -> username.startsWith(request.getSearchString()))
                .filter(username -> !username.equals(request.getSender()))
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

        return new SearchUsersResult(nonFriends, friends);
    }

    private Object handleGetAllConversationDataRequest(GetAllConversationDataRequest request) {
        User user = data.getUser(request.getSender());

        List<Long> conversationIDs = user.getConversationIDs();
        if (!conversationIDs.contains(request.getId())) {
            return new Unauthorized();
        }
        Conversation conversation = data.getConversation(request.getId());
        Date lastMessageDate = conversation.getLastMessageDate();
        Timestamp lastMessageReceived = new Timestamp(lastMessageDate.getTime());
        return new ConversationDataUpdate(request.getId(),
                                          conversation.getMessages(),
                                          lastMessageReceived,
                                          conversation.getLastMessage()
        );
    }

    private void handleGetConversations(GetConversationsRequest request) {
        List<Long> conversationIDs = data
                .getUser(request.getUserName())
                .getConversationIDs();

        conversationIDs
                .stream()
                .filter(x -> request
                        .getFilters()
                        .stream()
                        .noneMatch(y -> y.getId() == x))
                .forEach(x -> out.addResponse(data
                                                      .getConversation(x)
                                                      .getPlaceholder()));

        request
                .getFilters()
                .stream()
                .filter(x -> conversationIDs.contains(x.getId()))
                .forEach(x -> {
                    Conversation conversation = data.getConversation(x.getId());
                    Timestamp time = new Timestamp(x
                                                           .getLastMessageDate()
                                                           .getTime());
                    Timestamp lastMessageReceived = new Timestamp(conversation
                                                                          .getLastMessageDate()
                                                                          .getTime());
                    ConversationDataUpdate res = new ConversationDataUpdate(x.getId(),
                                                                            conversation.getMessagesFromDate(time),
                                                                            lastMessageReceived,
                                                                            conversation.getLastMessage()
                    );

                    out.addResponse(res);
                });
    }

    private Object handleUpdateHistory(UpdateHistoryRequest request) {
        User user = data.getUser(request.getSender());

        if (!user
                .getConversationIDs()
                .contains(request.getId())) {
            return new Unauthorized();
        }

        Conversation con = data.getConversation(request.getId());
        if (con == null) {
            return new NoSuchConversation();
        }

        List<TextMessage> res = con.getMessagesFromDate(new Timestamp(request
                                                                              .getDateLastMessageReceived()
                                                                              .getTime()));

        return new HistoryUpdates(res, request.getId());
    }

    private Object handleRemoveParticipant(RemoveParticipantRequest request) {
        Conversation con = data.getConversation(request.getId());
        if (con == null) {
            return new NoSuchConversation();
        }

        if (!con
                .getParticipants()
                .contains(request.getSender())) {
            return new Unauthorized();
        }

        User user = data.getUser(request.getParticipant());
        if (user == null) {
            return new NoSuchUser();
        }

        user.removeConversationID(request.getId());
        con.removeUser(request.getParticipant());
        data.storeConversation(con);
        data.storeUser(user);

        return new AllIsWell();
    }

    private Object handleCreateConversation(CreateConversationRequest request) {
        List<User> users = new ArrayList<>();
        for (String username : request.getParticipants()) {
            User u = data.getUser(username);
            if (u == null) {
                return new NoSuchUser();
            }

            users.add(u);
        }

        Conversation con = data.createConversation(request.getParticipants());

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
        Conversation con = data.getConversation(request.getId());
        if (con == null) {
            return new NoSuchConversation();
        }

        List<String> conversationPariticipants = con.getParticipants();
        if (!conversationPariticipants.contains(request.getSender())) {
            return new Unauthorized();
        }

        if (conversationPariticipants.contains(request.getParticipant())) {
            return new UserExists();
        }

        User user = data.getUser(request.getParticipant());
        if (user == null) {
            return new NoSuchUser();
        }

        con.addUser(user.getUserName());
        user.addConversationID(con.getId());

        OutputAgent outputAgent = data.getOutputAgent(user.getUserName());
        outputAgent.addResponse(con);

        data.storeConversation(con);
        data.storeUser(user);

        return new AllIsWell();
    }

    private Object handleSession(Session arg) {
        AES aes = new AES(arg.getKey());
        inputAgent.setAes(aes);
        out.setAes(aes);
        inputAgent.setSessionCreated(true);
        return new AllIsWell();
    }

    private Object handleDeleteFriend(DeleteFriend arg) {
        System.out.println("Delete Friend");
        return data
                .getUser(inputAgent.getUsername())
                .removeFriend(data, arg.getFriendUsername());
    }

    private Object handleDeleteFriendRequest(DeleteFriendRequest arg) {
        System.out.println("DeleteFriendRequest");
        return data
                .getUser(inputAgent.getUsername())
                .removeFriendRequest(data, arg.getFriendUsername());
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

        Response response = data.userLogin(arg.getUsername(), arg.getPassword(), out, inputAgent);

        if (!(response instanceof AllIsWell)) {
            return response;
        }

        out.setUserName(arg.getUsername());
        inputAgent.setUsername(arg.getUsername());
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
        InviteCode inviteCode = arg.getInviteCode();
        if (!inviteCodes.contains(inviteCode)) {
            return new IllegalInviteCode();
        }

        try {
            Response response = data.createUser(arg.getUsername(), arg.getPassword());
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

        if (data.isUserOnline(arg.getFriendUsername())) {
            InputHandler inputHandler = data
                    .getInputAgent(arg.getFriendUsername())
                    .getInputHandler();
            inputHandler.handleInput(new GetFriendListRequest(arg.getFriendUsername()));
        }
        return getFriendList();
    }
}
