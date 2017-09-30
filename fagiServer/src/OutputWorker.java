/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.model.FriendRequest;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendRequestList;
import com.fagi.model.messages.lists.ListAccess;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author miniwolf
 */
public class OutputWorker extends Worker {
    private final Queue<InGoingMessages> messages = new ConcurrentLinkedQueue<>();
    private final Queue<Object> respondObjects = new ConcurrentLinkedQueue<>();
    private ObjectOutputStream objOut;
    private EncryptionAlgorithm<AESKey> aes;

    private String myUserName = null;
    private ListAccess<String> currentFriends = new DefaultListAccess<>(new ArrayList<>());
    private ListAccess<FriendRequest> currentRequests = new DefaultListAccess<>(new ArrayList<>());

    public OutputWorker(Socket socket) throws IOException {
        objOut = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (running) {
            System.out.println("Running");
            try {
                sendIncMessages();
                sendResponses();
                objOut.reset();
                while (messages.isEmpty() && respondObjects.isEmpty() && running) {
                    if (myUserName != null) {
                        checkForLists();
                    }
                    Thread.sleep(100);
                }
            } catch (IOException | InterruptedException ioe) {
                running = false;
                System.out.println(ioe.toString());
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            }
        }
        if (!respondObjects.isEmpty()) {
            try {
                sendResponses();
            } catch (IOException ignored) { // User logged off we didn't manage to send response
            }
        }
        System.out.println("Closing output");
    }

    private void checkForLists() throws IOException {
        DefaultListAccess<FriendRequest> friendRequests = getFriendRequests();
        checkList(new FriendRequestList(friendRequests), currentRequests);
    }

    private <T extends Comparable> void checkList(InGoingMessages<List<T>> responseObj, ListAccess<T> currentList) throws IOException {
        ListAccess<T> responseList = (ListAccess<T>) responseObj.getAccess();

        if (equalLists(responseList.getData(), currentList.getData())) {
            return;
        }

        currentList.updateData(responseList.getData());
        send(responseObj);
    }

    private void sendResponses() throws IOException {
        while (respondObjects.size() > 0) {
            send(respondObjects.remove());
        }
    }

    private void sendIncMessages() throws IOException {
        while (messages.size() > 0) {
            send(messages.remove());
        }
    }

    private void send(Object object) throws IOException {
        System.out.println("so: " + object.toString());
        objOut.writeObject(aes.encrypt(Conversion.convertToBytes(object)));
        objOut.flush();
    }

    private DefaultListAccess<String> getOnlineFriends() {
        User me = Data.getUser(myUserName);
        return new DefaultListAccess<>(me.getFriends().stream().filter(Data::isUserOnline)
                .collect(Collectors.toList()));
    }

    private DefaultListAccess<FriendRequest> getFriendRequests() {
        User me = Data.getUser(myUserName);
        return new DefaultListAccess<>(me.getFriendReq());
    }

    public void setAes(EncryptionAlgorithm<AESKey> aes) {
        this.aes = aes;
    }

    synchronized void addMessage(InGoingMessages message) {
        messages.add(message);
    }

    synchronized void addResponse(Object responseObj) {
        respondObjects.add(responseObj);
    }

    public void setUserName(String userName) {
        this.myUserName = userName;
    }

    private <T extends Comparable> boolean equalLists(List<T> one, List<T> two) {
        if (one == null && two == null) {
            return true;
        }

        if ((one == null && two != null) || one != null && two == null
            || one.size() != two.size()) {
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<>(one);
        two = new ArrayList<>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }
}
