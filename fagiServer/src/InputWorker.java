/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

import com.fagi.encryption.AES;
import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.model.CreateUser;
import com.fagi.model.DeleteFriend;
import com.fagi.model.DeleteFriendRequest;
import com.fagi.model.FriendRequest;
import com.fagi.model.Login;
import com.fagi.model.Logout;
import com.fagi.model.Session;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.model.messages.message.VideoMessage;
import com.fagi.responses.AllIsWell;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author miniwolf
 */
public class InputWorker extends Worker {
    private final ConversationHandler handler;
    private final VideoConversationHandler videoHandler;
    private ObjectInputStream objIn;
    private OutputWorker out;
    private String myUserName = null;

    private EncryptionAlgorithm<AESKey> aes;
    private boolean sessionCreated = false;

    public InputWorker(Socket socket, OutputWorker out, ConversationHandler handler, VideoConversationHandler videoHandler) throws IOException {
        this.handler = handler;
        this.videoHandler = videoHandler;
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
                Data.userLogout(myUserName);
            } catch (Exception e) {
                running = false;
                System.out.println("Something went wrong in a input worker while loop " + e);
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            }
        }
        System.out.println("Closing");
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
        } else if ( input instanceof DeleteFriendRequest ) {
            DeleteFriendRequest arg = (DeleteFriendRequest) input;
            out.addResponse(handleDeleteFriendRequest(arg));
        } else if ( input instanceof DeleteFriend ) {
            DeleteFriend arg = (DeleteFriend) input;
            out.addResponse(handleDeleteFriend(arg));
        } else if ( input instanceof Session ) {
            Session arg = (Session) input;
            out.addResponse(handleSession(arg));
        } else if (input instanceof VideoMessage){
            VideoMessage arg = (VideoMessage) input;
            out.addResponse(handleVideoMessage(arg));
        } else {
            System.out.println("Unknown handle: " + input.getClass().toString());
        }
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
        System.out.println("Message");

        handler.addMessage(arg);

        return new AllIsWell();
    }

    private Object handleVideoMessage(VideoMessage arg){
        System.out.println("Video-transmission chunk");
        videoHandler.addMessage(arg);

        return new AllIsWell();
    }

    private Object handleLogin(Login arg) {
        System.out.println("Login");
        myUserName = arg.getUsername();
        out.setUserName(arg.getUsername());
        return Data.userLogin(arg.getUsername(), arg.getPassword(), out);
    }

    private Object handleLogout() {
        System.out.println("Logout");
        Data.userLogout(myUserName);
        running = false;
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
        return Data.getUser(myUserName).requestFriend(arg.getFriendUsername());
    }
}
