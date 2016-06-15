/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * User.java
 *
 * User data object.
 */

import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add description, password protection OTR:
 * https://otr.cypherpunks.ca/otr-wpes.pdf
 */
public class User {
    private final String pass;
    private final String userName;
    private List<String> friends;
    private List<Long> conversationIDs;
    private volatile List<String> incFriendReq;

    public User(String name, String pass) {
        this.pass = pass;
        this.userName = name;
        conversationIDs = new ArrayList<>();
        friends = new ArrayList<>();
        incFriendReq = new ArrayList<>();
    }

    public String getPass() {
        return pass;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getFriends() {
        return friends;
    }

    public List<String> getFriendReq() {
        return incFriendReq;
    }

    public void addFriend(User friend) {
        friends.add(friend.getUserName());
    }

    public Response requestFriend(String otherUser) {
        if ( Data.findInUserFile(userName, otherUser, 0) ) {
            return new UserExists();
        }
        User other = Data.getUser(otherUser);
        if ( other == null ) {
            return new NoSuchUser();
        }

        if ( incFriendReq.contains(otherUser) ) {
            Data.makeFriends(this, other);
            return removeFriendRequest(otherUser);
        }
        return other.addFriendReq(userName);
    }

    public Response removeFriendRequest(String otherUser) {
        if ( !Data.findInUserFile(userName, otherUser, 1) ) {
            return new UserExists();
        }
        incFriendReq.remove(otherUser);
        return Data.removeFriendRequestFromUserFile(this.userName, otherUser);
    }

    private Response addFriendReq(String userName) {
        if ( incFriendReq.contains(userName) ) {
            return new UserExists();
        }
        incFriendReq.add(userName);
        return Data.appendFriendReqToUserFile(this.userName, userName);
    }

    public void addFriendReqs(List<String> friendReqs) {
        incFriendReq = friendReqs;
    }

    public void addFriends(List<String> friends) {
        this.friends = friends;
    }

    public Response removeFriend(String otherUser) {
        if ( !Data.findInUserFile(userName, otherUser, 0) ) {
            return new UserExists();
        }
        friends.remove(otherUser);
        return Data.removeFriendFromUserFile(this.userName, otherUser);
    }

    public void addConversationID(long id) {
        conversationIDs.add(id);
    }

    public void removeConversationID(long id) {
        conversationIDs.remove(id);
    }
}