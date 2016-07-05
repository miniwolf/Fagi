import com.fagi.conversation.Conversation;
import com.fagi.encryption.*;
import com.fagi.exceptions.AllIsWellException;
import com.fagi.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PublicKey;

/**
 * Created by Marcus on 05-07-2016.
 */
public class Communication {
    private ObjectOutputStream out;
    private final String host;
    private final int port;
    private InputHandler inputHandler;
    private Socket socket;
    private Thread inputThread;
    private EncryptionAlgorithm encryption;

    public Communication(String host, int port, EncryptionAlgorithm encryption, PublicKey serverKey) throws IOException {
        this.encryption = encryption;
        this.host = host;
        this.port = port;
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());

            inputHandler = new InputHandler(new ObjectInputStream(socket.getInputStream()), encryption);
            inputThread = new Thread(inputHandler);
            inputThread.setDaemon(true);
            inputThread.start();

            createSession(encryption, serverKey);
        } catch (UnknownHostException Uhe) {
            System.err.println("c Uhe: " + Uhe);
        } catch (IOException ioe) {
            throw new IOException("c ioe: " + ioe.toString());
        }
    }

    private void createSession(EncryptionAlgorithm encryption, PublicKey serverKey) throws IOException {
        RSA rsa = new RSA();
        rsa.setEncryptionKey(new RSAKey(new KeyPair(serverKey, null)));
        out.writeObject(rsa.encrypt(Conversion.convertToBytes(new Session((AESKey) encryption.getKey()))));
        out.flush();

        Exception obj;
        while ((obj = inputHandler.containsException()) == null) {}
        if (obj instanceof AllIsWellException) {

        }
    }

    public Message receiveMessage() {
        Message message = inputHandler.containsMessage();
        if ( message == null ) {
            return inputHandler.containsVoice();
        }
        return message;
    }

    public void sendObject(Object obj) {
        try {
            out.writeObject(encryption.encrypt(Conversion.convertToBytes(obj)));
            out.flush();
        } catch (IOException e) {
            System.err.println("cso ioe: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Do not use this method anymore.
     * This might give you the wrong object or even none
     * if it has not reached the input yet.
     *
     * @return Current object in the bottom of queue. (Newest Object)
     * @deprecated use contains"Object" methods instead.
     */
    public Object handleObjects() {
        Object object = null;
        while ( object == null ) {
            object = inputHandler.getLast();
        }
        return object;
    }

    /**
     * Getting the friend/friend request list from the server.
     * Using object to pass the correct get requests to the server.
     *
     * @return FriendRequestList which contains all of our requests as strings.
     */
    public FriendRequestList getRequests() {
        sendObject(new GetFriendRequests());
        return inputHandler.containsRequests();
    }

    /**
     * Getting the friend list from the server.
     *
     * @return FriendList which contains all of our friends as strings.
     */
    public FriendList getFriends() {
        sendObject(new GetFriends());
        return inputHandler.containsFriends();
    }

    // TODO: Need to close correctly?
    public void close() {
        inputHandler.close();
        inputThread.interrupt();
        try {
            socket.close();
        } catch (IOException ioe) {
            System.err.println("cc ioe: " + ioe.toString());
        }
    }

    public Exception getNextException() {
        return inputHandler.containsException();
    }

    public Conversation getConversation() {
        return inputHandler.containsConversation();
    }

    public HistoryUpdates getHistoryUpdates() {
        return inputHandler.containsHistoryUpdates();
    }
}