/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * User.java
 *
 * User data object.
 */

import com.fagi.exceptions.AllIsWellException;
import com.fagi.exceptions.NoSuchUserException;
import com.fagi.exceptions.UserExistsException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TODO: Add description, password protection OTR:
 * https://otr.cypherpunks.ca/otr-wpes.pdf
 */
public class User {
    private final String pass;
    private final String userName;
    private List<String> friends;
    private List<String> incFriendReq;

    public User(String name, String pass) {
        this.pass = pass;
        this.userName = name;
        friends = new CopyOnWriteArrayList<>();
        incFriendReq = new CopyOnWriteArrayList<>();
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

    public Exception requestFriend(String otherUser) {
        if ( Data.findInUserFile(userName, otherUser, 0) ) {
            return new UserExistsException();
        }
        User other = Data.getUser(otherUser);
        if ( other == null ) {
            return new NoSuchUserException();
        }

        if ( incFriendReq.contains(otherUser) ) {
            Exception exception = other.removeFriendRequest(userName);
            if ( exception instanceof AllIsWellException ) {
                Data.makeFriends(this, other);
                return removeFriendRequest(otherUser);
            } else {
                return exception;
            }
        }
        return other.addFriendReq(userName);
    }

    public Exception removeFriendRequest(String otherUser) {
        if ( !Data.findInUserFile(userName, otherUser, 1) ) {
            return new UserExistsException();
        }
        incFriendReq.remove(otherUser);
        return new AllIsWellException();
    }

    private Exception addFriendReq(String userName) {
        if ( incFriendReq.contains(userName) ) {
            return new UserExistsException();
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
}