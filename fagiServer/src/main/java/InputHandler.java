import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.encryption.AES;
import com.fagi.model.*;
import com.fagi.model.conversation.*;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InputHandler {
    private InputAgent inputAgent;
    private OutputAgent out;
    private ConversationHandler conversationHandler;
    private Data data;

    public InputHandler(InputAgent inputAgent, OutputAgent out, ConversationHandler conversationHandler, Data data) {
        this.inputAgent = inputAgent;
        this.out = out;
        this.conversationHandler = conversationHandler;
        this.data = data;
    }

    public void handleInput(Object input) {
        if (input instanceof TextMessage) {
            TextMessage arg = (TextMessage) input;
            arg.getMessageInfo().setTimestamp(new Timestamp(System.currentTimeMillis()));
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
            System.out.println("Unknown handle: " + input.getClass().toString());
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
                .filter(username -> user.getFriends().contains(username))
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

        if (!user.getConversationIDs().contains(request.getId())) {
            return new Unauthorized();
        }
        Conversation conversation = data.getConversation(request.getId());
        Timestamp lastMessageReceived = new Timestamp(conversation.getLastMessageDate().getTime());
        return new ConversationDataUpdate(request.getId(), conversation.getMessages(),
                lastMessageReceived, conversation.getLastMessage());
    }

    private void handleGetConversations(GetConversationsRequest request) {
        User user = data.getUser(request.getUserName());

        user.getConversationIDs().stream()
                .filter(x -> request.getFilters().stream().noneMatch(y -> y.getId() == x))
                .forEach(x -> out.addResponse(data.getConversation(x).getPlaceholder()));

        request.getFilters().stream().filter(x -> user.getConversationIDs().contains(x.getId()))
                .forEach(x -> {
                    Conversation conversation = data.getConversation(x.getId());
                    Timestamp time = new Timestamp(x.getLastMessageDate().getTime());
                    Timestamp lastMessageReceived = new Timestamp(
                            conversation.getLastMessageDate().getTime());
                    ConversationDataUpdate res = new ConversationDataUpdate(x.getId(), conversation
                            .getMessagesFromDate(time), lastMessageReceived, conversation
                            .getLastMessage());

                    out.addResponse(res);
                });
    }

    private Object handleUpdateHistory(UpdateHistoryRequest request) {
        User user = data.getUser(request.getSender());

        if (!user.getConversationIDs().contains(request.getId())) {
            return new Unauthorized();
        }

        Conversation con = data.getConversation(request.getId());
        if (con == null) {
            return new NoSuchConversation();
        }

        List<TextMessage> res = con
                .getMessagesFromDate(new Timestamp(request.getDateLastMessageReceived().getTime()));

        return new HistoryUpdates(res, request.getId());
    }

    private Object handleRemoveParticipant(RemoveParticipantRequest request) {
        Conversation con = data.getConversation(request.getId());
        if (con == null) {
            return new NoSuchConversation();
        }

        if (!con.getParticipants().contains(request.getSender())) {
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
            if (!user.getUserName().equals(inputAgent.getUsername()) && data
                    .isUserOnline(user.getUserName())) {
                data.getOutputWorker(user.getUserName()).addResponse(con);
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

        if (!con.getParticipants().contains(request.getSender())) {
            return new Unauthorized();
        }

        if (con.getParticipants().contains(request.getParticipant())) {
            return new UserExists();
        }

        User user = data.getUser(request.getParticipant());
        if (user == null) {
            return new NoSuchUser();
        }

        con.addUser(user.getUserName());
        user.addConversationID(con.getId());

        data.getOutputWorker(user.getUserName()).addResponse(con);

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
        return data.getUser(inputAgent.getUsername()).removeFriend(data, arg.getFriendUsername());
    }

    private Object handleDeleteFriendRequest(DeleteFriendRequest arg) {
        System.out.println("DeleteFriendRequest");
        return data.getUser(inputAgent.getUsername()).removeFriendRequest(data, arg.getFriendUsername());
    }

    private Object handleTextMessage(TextMessage arg) {
        Conversation con = data.getConversation(arg.getMessageInfo().getConversationID());
        if (con == null) {
            return new NoSuchConversation();
        }

        if (!con.getParticipants().contains(arg.getMessageInfo().getSender())) {
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
        for (String user : data.getUser(inputAgent.getUsername()).getFriends()) {
            if (data.isUserOnline(user)) {
                data.getOutputWorker(user).addMessage(new UserLoggedIn(inputAgent.getUsername()));
            }
        }

        return response;
    }

    private Object handleLogout() {
        System.out.println("Logout");
        data.userLogout(inputAgent.getUsername());
        inputAgent.setRunning(false);

        for (String user : data.getUser(inputAgent.getUsername()).getFriends()) {
            if (data.isUserOnline(user)) {
                data.getOutputWorker(user).addMessage(new UserLoggedOut(inputAgent.getUsername()));
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
        inviteCodes.remove(inviteCode);
        data.storeInviteCodes(inviteCodes);

        try {
            return data.createUser(arg.getUsername(), arg.getPassword());
        } catch (Exception e) {
            return e;
        }
    }

    private Object handleFriendRequest(FriendRequest arg) {
        System.out.println("FriendRequest");
        Response response = data.getUser(inputAgent.getUsername()).requestFriend(data, arg);
        if (!(response instanceof AllIsWell)) {
            return response;
        }

        if (data.isUserOnline(arg.getFriendUsername())) {
            data.getInputWorker(arg.getFriendUsername())
                    .getInputHandler().handleInput(new GetFriendListRequest(arg.getFriendUsername()));
        }
        return getFriendList();
    }
}
