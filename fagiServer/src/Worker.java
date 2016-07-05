/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Worker.java
 *
 * Worker thread for each client.
 */

import com.fagi.conversation.Conversation;
import com.fagi.encryption.AES;
import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.exceptions.AllIsWellException;
import com.fagi.exceptions.NoSuchUserException;
import com.fagi.exceptions.UserExistsException;
import com.fagi.model.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

class Worker implements Runnable {
    private final Queue<Message> incMessages = new ConcurrentLinkedQueue<>();
    private final ConversationHandler handler;
    private ObjectInputStream oIn;
    private ObjectOutputStream oOut;
    private boolean running = true;
    private boolean sessionCreated = false;
    private String myUserName;
    private EncryptionAlgorithm<AESKey> aes;

    public Worker(Socket socket, ConversationHandler handler) throws IOException {
        this.handler = handler;
        System.out.println("Starting a worker thread");
        oIn = new ObjectInputStream(socket.getInputStream());
        oOut = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while ( running ) {
            System.out.println("Running");
            try {
                sendIncMessages();
                Object input = oIn.readObject();

                if (input instanceof byte[]) {
                    input = decryptAndConvertToObject((byte[])input);
                }

                Object result = handleInput(input);

                oOut.writeObject(aes.encrypt(Conversion.convertToBytes(result)));

                oOut.reset();
            } catch (EOFException eof) {
                running = false;
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            } catch (Exception e) {
                running = false;
                System.out.println("Something went wrong in a worker while loop " + e);
                e.printStackTrace();
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            }
        }
        System.out.println("Closing");
    }

    private Object decryptAndConvertToObject(byte[] input) {
        if (sessionCreated) {
            input = aes.decrypt(input);
        } else {
            input = Encryption.getInstance().getRSA().decrypt(input);
        }
        try {
            return Conversion.convertFromBytes(input);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendIncMessages() throws Exception {
        while ( incMessages.size() > 0 ) {
            oOut.writeObject(aes.encrypt(Conversion.convertToBytes(incMessages.remove())));
        }
    }

    private Object handleInput(Object input) {
        if ( input instanceof Message ) {
            Message arg = (Message) input;
            return handleMessage(arg);
        } else if ( input instanceof Login ) {
            Login arg = (Login) input;
            return handleLogin(arg);
        } else if ( input instanceof Logout ) {
            Logout arg = (Logout) input;
            return handleLogout();
        } else if ( input instanceof GetFriends ) {
            return handleGetFriends();
        } else if ( input instanceof GetFriendRequests) {
            return handleGetRequests();
        } else if ( input instanceof CreateUser ) {
            CreateUser arg = (CreateUser) input;
            return handleCreateUser(arg);
        } else if ( input instanceof FriendRequest ) {
            FriendRequest arg = (FriendRequest) input;
            return handleFriendRequest(arg);
        } else if ( input instanceof DeleteFriendRequest ) {
            DeleteFriendRequest arg = (DeleteFriendRequest) input;
            return handleDeleteFriendRequest(arg);
        } else if ( input instanceof DeleteFriend ) {
            DeleteFriend arg = (DeleteFriend) input;
            return handleDeleteFriend(arg);
        } else if(input instanceof Session) {
            Session s = (Session)input;
            aes = new AES(s.getKey());
            sessionCreated = true;
            return new AllIsWellException();
        } else if(input instanceof CreateConversationRequest) {
            CreateConversationRequest request = (CreateConversationRequest)input;

            return handleCreateConversationRequest(request);
        } else if(input instanceof AddParticipantRequest) {
            AddParticipantRequest request = (AddParticipantRequest)input;
            return handleAddParticipantRequest(request);
        } else if(input instanceof RemoveParticipantRequest) {
            RemoveParticipantRequest request = (RemoveParticipantRequest)input;
            return handleRemoveParticipantRequest(request);
        } else if(input instanceof UpdateHistoryRequest) {
            UpdateHistoryRequest request = (UpdateHistoryRequest)input;
            return handleUpdateHistoryRequest(request);
        } else {
            return handleUnknownObject(input);
        }
        // else if ( input instanceof GetSound) {
        // TODO: Implement or at least think about it
        //}
    }

    private Object handleDeleteFriend(DeleteFriend arg) {
        System.out.println("Delete Friend");
        return Data.getUser(myUserName).removeFriend(arg.getFriendUsername());
    }

    private Object handleDeleteFriendRequest(DeleteFriendRequest arg) {
        System.out.println("DeleteFriendRequest");
        return Data.getUser(myUserName).removeFriendRequest(arg.getFriendUsername());
    }

    private Object handleGetFriends() {
        return getOnlineFriends();
    }

    private Object handleGetRequests() {
        return getFriendRequests();
    }

    private Object handleMessage(Message arg) {
        System.out.println("Message");

        handler.addMessage(arg);

        return new AllIsWellException();
    }

    private Object handleLogin(Login arg) {
        System.out.println("Login");
        myUserName = arg.getUsername();
        return Data.userLogin(arg.getUsername(), arg.getPassword(), this);
    }

    private Object handleLogout() {
        System.out.println("Logout");
        Data.userLogout(myUserName);
        running = false;
        return new AllIsWellException();
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
        return Data.getUser(myUserName).requestFriend(arg.getFriendUsername());
    }

    private Object handleCreateConversationRequest(CreateConversationRequest request) {
        Conversation con = Data.createConversation(request.getParticipants());

        for (String participant : request.getParticipants()) {
            User user = Data.getUser(participant);

            if (user == null) {
                return new NoSuchUserException();
            }

            user.addConversationID(con.getId());
        }

        return con;
    }

    private Object handleAddParticipantRequest(AddParticipantRequest request) {
        Conversation con = Data.getConversation(request.getId());
        con.addUser(request.getParticipant());

        User user = Data.getUser(request.getParticipant());

        if (user == null) {
            return new NoSuchUserException();
        }

        user.addConversationID(request.getId());

        return new AllIsWellException();
    }

    private Object handleRemoveParticipantRequest(RemoveParticipantRequest request) {
        Conversation con = Data.getConversation(request.getId());
        con.removeUser(request.getParticipant());

        User user = Data.getUser(request.getParticipant());

        if (user == null) {
            return new NoSuchUserException();
        }

        user.removeConversationID(request.getId());

        return new AllIsWellException();
    }

    private Object handleUpdateHistoryRequest(UpdateHistoryRequest request) {
        Conversation con = Data.getConversation(request.getId());
        // TODO : Handle con == null

        List<TextMessage> res = new ArrayList<>();

        for (int i = request.getIndex() + 1; i < con.getMessages().size(); i++) {
            res.add(con.getMessages().get(i));
        }

        return new HistoryUpdates(res, request.getId());
    }

    private Object handleUnknownObject(Object input) {
        System.out.println(input.getClass().toString());
        return new Exception();
    }

    private Object getOnlineFriends() {
        User me = Data.getUser(myUserName);
        if ( me == null ) {
            return new NoSuchUserException();
        }
        return new FriendList(me.getFriends().stream().filter(Data::isUserOnline)
                                .collect(Collectors.toList()));
    }

    private Object getFriendRequests() {
        User me = Data.getUser(myUserName);
        if ( me == null ) {
            return new NoSuchUserException();
        }
        //return new FriendRequestList(me.getFriendReq().stream().collect(Collectors.toList()));
        return new FriendRequestList(me.getFriendReq());
    }

    synchronized void addMessage(Message message) {
        incMessages.add(message);
    }
}