/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.encryption.AES;
import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.model.*;
import com.fagi.model.conversation.*;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author miniwolf
 */
public class InputWorker extends Worker {
    private final ConversationHandler handler;
    private ObjectInputStream objIn;
    private OutputWorker out;
    private String myUserName = null;

    private EncryptionAlgorithm<AESKey> aes;
    private boolean sessionCreated = false;

    public InputWorker(Socket socket, OutputWorker out, ConversationHandler handler) throws IOException {
        this.handler = handler;
        System.out.println("Starting an input thread");
        objIn = new ObjectInputStream(socket.getInputStream());
        this.out = out;
    }

    @Override
    public void run() {
        while ( running ) {
            System.out.println("Running");
            try {
                Object input = objIn.readObject();
                if ( input instanceof byte[] ) {
                    input = decryptAndConvertToObject((byte[]) input);
                }
                handleInput(input);
            } catch (EOFException | SocketException eof) {
                running = false;
                System.out.println("Logging out user " + myUserName);
                out.running = false;
                Data.userLogout(myUserName);
            } catch (Exception e) {
                running = false;
                out.running = false;
                System.out.println("Something went wrong in a input worker while loop " + e);
                e.printStackTrace();
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            }
        }
        System.out.println("Closing input");
    }

    private Object decryptAndConvertToObject(byte[] input) {
        input = sessionCreated ? aes.decrypt(input) :
                Encryption.getInstance().getRSA().decrypt(input);
        try {
            return Conversion.convertFromBytes(input);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleInput(Object input) {
        if ( input instanceof TextMessage ) {
            TextMessage arg = (TextMessage) input;
            arg.getMessageInfo().setTimestamp(new Timestamp(System.currentTimeMillis()));
            out.addResponse(handleTextMessage(arg));
        } else if ( input instanceof Login ) {
            Login arg = (Login) input;
            out.addResponse(handleLogin(arg));
        } else if ( input instanceof Logout ) {
            out.addResponse(handleLogout());
            out.running = false;
        } else if ( input instanceof CreateUser ) {
            CreateUser arg = (CreateUser) input;
            out.addResponse(handleCreateUser(arg));
        } else if ( input instanceof FriendRequest ) {
            FriendRequest arg = (FriendRequest) input;
            out.addResponse(handleFriendRequest(arg));
        } else if ( input instanceof GetFriendListRequest ) {
            GetFriendListRequest arg = (GetFriendListRequest)input;
            out.addResponse(handleGetFriendRequest(arg));
        } else if ( input instanceof DeleteFriendRequest ) {
            DeleteFriendRequest arg = (DeleteFriendRequest) input;
            out.addResponse(handleDeleteFriendRequest(arg));
        } else if ( input instanceof DeleteFriend ) {
            DeleteFriend arg = (DeleteFriend) input;
            out.addResponse(handleDeleteFriend(arg));
        } else if ( input instanceof Session ) {
            Session arg = (Session) input;
            out.addResponse(handleSession(arg));
        } else if ( input instanceof AddParticipantRequest) {
            AddParticipantRequest request = (AddParticipantRequest)input;
            out.addResponse(handleAddParticipant(request));
        } else if ( input instanceof CreateConversationRequest) {
            CreateConversationRequest request = (CreateConversationRequest)input;
            Object response = handleCreateConversation(request);
            if (!(response instanceof NoSuchUser)) {
                out.addResponse(new AllIsWell());
            }
            out.addResponse(response);
        } else if ( input instanceof RemoveParticipantRequest) {
            RemoveParticipantRequest request = (RemoveParticipantRequest)input;
            out.addResponse(handleRemoveParticipant(request));
        } else if ( input instanceof UpdateHistoryRequest) {
            UpdateHistoryRequest request = (UpdateHistoryRequest)input;

            Object response = handleUpdateHistory(request);

            if ( (response instanceof HistoryUpdates) ) {
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
        } else if ( input instanceof SearchUsersRequest) {
            SearchUsersRequest request = (SearchUsersRequest)input;

            out.addResponse(handleSearchUsersRequest(request));
        } else if ( input instanceof UserNameAvailableRequest) {
            UserNameAvailableRequest request = (UserNameAvailableRequest) input;
            out.addResponse(handleUserNameAvailableRequest(request));
        } else {
            System.out.println("Unknown handle: " + input.getClass().toString());
        }
    }

    private Object handleGetFriendRequest(GetFriendListRequest arg) {
        User user = Data.getUser(myUserName);
        List<String> friendUsernames = user.getFriends();
        List<Friend> friends = new ArrayList<>();

        for (String friendUsername : friendUsernames) {
            friends.add(new Friend(friendUsername, Data.isUserOnline(friendUsername)));
        }

        return new FriendList(new DefaultListAccess<>(friends));
    }

    private Object handleUserNameAvailableRequest(UserNameAvailableRequest request) {
        if (Data.getUser(request.getUsername()) == null) {
            return new AllIsWell();
        } else {
            return new UserExists();
        }
    }

    private Object handleSearchUsersRequest(SearchUsersRequest request) {
        User user = Data.getUser(this.myUserName);
        out.addResponse(new AllIsWell());

        List<String> usernames = Data
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

        SearchUsersResult result = new SearchUsersResult(nonFriends, friends);

        return result;
    }

    private Object handleGetAllConversationDataRequest(GetAllConversationDataRequest request) {
        User user = Data.getUser(request.getSender());

        if (!user.getConversationIDs().contains(request.getId())) {
            return new Unauthorized();
        }
        Conversation conversation = Data.getConversation(request.getId());
        Timestamp lastMessageReceived = new Timestamp(conversation.getLastMessageDate().getTime());
        return new ConversationDataUpdate(request.getId(), conversation.getMessages(), lastMessageReceived, conversation.getLastMessage());
    }

    private void handleGetConversations(GetConversationsRequest request) {
        User user = Data.getUser(request.getUserName());

        user.getConversationIDs().stream().filter(x -> request.getFilters().stream().filter(y -> y.getId() == x).count() == 0).forEach(x -> {
            out.addResponse(Data.getConversation(x).getPlaceholder());
        });

        request.getFilters().stream().filter(x -> user.getConversationIDs().contains(x.getId())).forEach(x -> {
            Conversation conversation = Data.getConversation(x.getId());
            Timestamp time = new Timestamp(x.getLastMessageDate().getTime());
            Timestamp lastMessageReceived = new Timestamp(conversation.getLastMessageDate().getTime());
            ConversationDataUpdate res = new ConversationDataUpdate(x.getId(), conversation.getMessagesFromDate(time), lastMessageReceived, conversation.getLastMessage());

            out.addResponse(res);
        });
    }

    private Object handleUpdateHistory(UpdateHistoryRequest request) {
        User user = Data.getUser(request.getSender());

        if (!user.getConversationIDs().contains(request.getId())) {
            return new Unauthorized();
        }

        Conversation con = Data.getConversation(request.getId());
        if (con == null) {
            return new NoSuchConversation();
        }

        List<TextMessage> res = con.getMessagesFromDate(new Timestamp(request.getDateLastMessageReceived().getTime()));

        return new HistoryUpdates(res, request.getId());
    }

    private Object handleRemoveParticipant(RemoveParticipantRequest request) {
        Conversation con = Data.getConversation(request.getId());
        if (con == null) {
            return new NoSuchConversation();
        }

        if (!con.getParticipants().contains(request.getSender())) {
            return new Unauthorized();
        }

        User user = Data.getUser(request.getParticipant());
        if (user == null) {
            return new NoSuchUser();
        }

        user.removeConversationID(request.getId());
        con.removeUser(request.getParticipant());
        Data.storeConversation(con);
        Data.storeUser(user);

        return new AllIsWell();
    }

    private Object handleCreateConversation(CreateConversationRequest request) {
        List<User> users = new ArrayList<>();
        for (String username : request.getParticipants()) {
            User u = Data.getUser(username);
            if (u == null) {
                return new NoSuchUser();
            }

            users.add(u);
        }

        Conversation con = Data.createConversation(request.getParticipants());

        for (User user : users) {
            user.addConversationID(con.getId());
            Data.storeUser(user);
            if (!user.getUserName().equals(this.myUserName)) {
                Data.getWorker(user.getUserName()).addResponse(con);
            }
        }

        Data.storeConversation(con);

        return con;
    }

    private Object handleAddParticipant(AddParticipantRequest request) {
        Conversation con = Data.getConversation(request.getId());
        if (con == null) {
            return new NoSuchConversation();
        }

        if (!con.getParticipants().contains(request.getSender())) {
            return new Unauthorized();
        }

        if (con.getParticipants().contains(request.getParticipant())) {
            return new UserExists();
        }

        User user = Data.getUser(request.getParticipant());
        if (user == null) {
            return new NoSuchUser();
        }

        con.addUser(user.getUserName());
        user.addConversationID(con.getId());

        Data.getWorker(user.getUserName()).addResponse(con);

        Data.storeConversation(con);
        Data.storeUser(user);

        return new AllIsWell();
    }

    private Object handleSession(Session arg) {
        aes = new AES(arg.getKey());
        out.setAes(aes);
        sessionCreated = true;
        return new AllIsWell();
    }

    private Object handleDeleteFriend(DeleteFriend arg) {
        System.out.println("Delete Friend");
        return Data.getUser(myUserName).removeFriend(arg.getFriendUsername());
    }

    private Object handleDeleteFriendRequest(DeleteFriendRequest arg) {
        System.out.println("DeleteFriendRequest");
        return Data.getUser(myUserName).removeFriendRequest(arg.getFriendUsername());
    }

    private Object handleTextMessage(TextMessage arg) {
        System.out.println("MessageInfo");

        Conversation con = Data.getConversation(arg.getMessageInfo().getConversationID());
        if (con == null) {
            return new NoSuchConversation();
        }

        if (!con.getParticipants().contains(arg.getMessageInfo().getSender())) {
            return new Unauthorized();
        }

        handler.addMessage(arg);

        return new AllIsWell();
    }

    private Object handleLogin(Login arg) {
        System.out.println("Login");
        myUserName = arg.getUsername();
        out.setUserName(arg.getUsername());

        Response response = Data.userLogin(arg.getUsername(), arg.getPassword(), out);

        if (response instanceof AllIsWell) {
            for (String user : Data.getUser(myUserName).getFriends()) {
                if (Data.isUserOnline(user)) {
                    Data.getWorker(user).addMessage(new UserLoggedIn(myUserName));
                }
            }
        }

        return response;
    }

    private Object handleLogout() {
        System.out.println("Logout");
        Data.userLogout(myUserName);
        running = false;

        for (String user : Data.getUser(myUserName).getFriends()) {
            if (Data.isUserOnline(user)) {
                Data.getWorker(user).addMessage(new UserLoggedOut(myUserName));
            }
        }

        return new AllIsWell();
    }

    private Object handleCreateUser(CreateUser arg) {
        System.out.println("CreateUser");
        try {
            return Data.createUser(arg.getUsername(), arg.getPassword());
        } catch (Exception e) {
            return e;
        }
    }

    private Object handleFriendRequest(FriendRequest arg) {
        System.out.println("FriendRequest");
        return Data.getUser(myUserName).requestFriend(arg);
    }
}
