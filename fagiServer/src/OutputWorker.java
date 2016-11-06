/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.lists.FriendRequestList;
import com.fagi.model.messages.lists.ListAccess;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.NoSuchUser;

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
    private ListAccess currentFriends = new DefaultListAccess(new ArrayList<>());
    private ListAccess currentRequests = new DefaultListAccess(new ArrayList<>());

    public OutputWorker(Socket socket) throws IOException {
        objOut = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while ( running ) {
            System.out.println("Running");
            try {
                sendIncMessages();
                sendResponses();
                objOut.reset();
                while ( messages.isEmpty() && respondObjects.isEmpty() && running ) {
                    checkForLists();
                    Thread.sleep(100);
                }
            } catch (IOException | InterruptedException ioe) {
                running = false;
                System.out.println(ioe.toString());
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            }
        }
        if ( !respondObjects.isEmpty() ) {
            try {
                sendResponses();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Closing output");
    }

    private void checkForLists() throws IOException {
        Object onlineFriends = getOnlineFriends();
        if ( !(onlineFriends instanceof NoSuchUser) ) {
            checkList(new FriendList((ListAccess) onlineFriends), currentFriends);
        }
        Object friendRequests = getFriendRequests();
        if ( !(friendRequests instanceof NoSuchUser) ) {
            checkList(new FriendRequestList((ListAccess) friendRequests), currentRequests);
        }
    }

    private void checkList(InGoingMessages responseObj, ListAccess currentList) throws IOException {
        ListAccess responseList = (ListAccess) responseObj.getAccess();

        if ( equalLists(responseList.getData(), currentList.getData()) ) {
            return;
        }

        currentList.updateData(responseList.getData());
        send(responseObj);
    }

    private void sendResponses() throws IOException {
        while ( respondObjects.size() > 0 ) {
            send(respondObjects.remove());
        }
    }

    private void sendIncMessages() throws IOException {
        while ( messages.size() > 0 ) {
            send(messages.remove());
        }
    }

    private void send(Object object) throws IOException {
        System.out.println("so: " + object.toString());
        objOut.writeObject(aes.encrypt(Conversion.convertToBytes(object)));
        objOut.flush();
    }

    private Object getOnlineFriends() {
        User me = Data.getUser(myUserName);
        if ( me == null ) {
            return new NoSuchUser();
        }
        return new DefaultListAccess(me.getFriends().stream().filter(Data::isUserOnline)
                                       .collect(Collectors.toList()));
    }

    private Object getFriendRequests() {
        User me = Data.getUser(myUserName);
        if ( me == null ) {
            return new NoSuchUser();
        }
        return new DefaultListAccess(me.getFriendReq());
    }

    public void setAes(EncryptionAlgorithm<AESKey> aes) {
        this.aes = aes;
    }

    synchronized void addMessage(TextMessage message) {
        messages.add(message);
    }

    synchronized void addResponse(Object responseObj) {
        respondObjects.add(responseObj);
    }

    public void setUserName(String userName) {
        this.myUserName = userName;
    }

    private boolean equalLists(List<String> one, List<String> two){
        if (one == null && two == null){
            return true;
        }

        if((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()){
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }
}
