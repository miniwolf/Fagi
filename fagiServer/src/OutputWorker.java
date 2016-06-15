/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

import com.fagi.model.FriendList;
import com.fagi.model.FriendRequestList;
import com.fagi.model.Message;
import com.fagi.model.ResponseList;
import com.fagi.responses.NoSuchUser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author miniwolf
 */
public class OutputWorker extends Worker {
    private final Queue<Message> messages = new ConcurrentLinkedQueue<>();
    private final Queue<Object> respondObjects = new ConcurrentLinkedQueue<>();
    private ObjectOutputStream objOut;

    private FriendList currentFriends = new FriendList(new ArrayList<>());
    private FriendRequestList currentRequests = new FriendRequestList(new ArrayList<>());

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
                while ( messages.isEmpty() && respondObjects.isEmpty() ) {
                    checkForLists(getOnlineFriends(), currentFriends);
                    checkForLists(getFriendRequests(), currentRequests);
                    Thread.sleep(100);
                }
            } catch (IOException | InterruptedException ioe) {
                running = false;
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            }
        }
        System.out.println("Closing");
    }

    private void sendIncMessages() throws IOException {
        while ( messages.size() > 0 ) {
            objOut.writeObject(messages.remove());
        }
    }

    private void checkForLists(Object responseObj, ResponseList currentList) throws IOException {
        if ( responseObj instanceof NoSuchUser ) {
            return;
        }
        ResponseList responseList = (ResponseList) responseObj;
        if ( responseList.getData().isEmpty()
             || responseList.getData().size() == currentList.getData().size() ) {
            return;
        }
        currentList.updateData(responseList.getData());
        objOut.writeObject(responseList);
    }

    private void sendResponses() throws IOException {
        while ( respondObjects.size() > 0 ) {
            objOut.writeObject(respondObjects.remove());
        }
    }

    private Object getOnlineFriends() {
        User me = Data.getUser(myUserName);
        if ( me == null ) {
            return new NoSuchUser();
        }
        return new FriendList(me.getFriends().stream().filter(Data::isUserOnline)
                                .collect(Collectors.toList()));
    }

    private Object getFriendRequests() {
        User me = Data.getUser(myUserName);
        if ( me == null ) {
            return new NoSuchUser();
        }
        return new FriendRequestList(me.getFriendReq());
    }

    synchronized void addMessage(Message message) {
        messages.add(message);
    }

    synchronized void addResponse(Object responseObj) {
        respondObjects.add(responseObj);
    }
}
