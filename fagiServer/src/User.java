/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * User.java
 *
 * User data object.
 */

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
    private List<String> incFriendReq;

    public User(String name, String pass) {
        this.pass = pass;
        this.userName = name;
        friends = new ArrayList<String>();
        incFriendReq = new ArrayList<String>();
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

    public void addFriend(User friend) {
        friends.add(friend.getUserName());
    }

    public void requestFriend(String otherUser) throws Exception {
        User other = Data.getUser(otherUser);
        if ( other == null )
            throw new NoSuchUserException();

        boolean status = true;
        for ( String s : incFriendReq ) if (s.equals(otherUser)) {
            Data.makeFriends(this, other);
            status = false;
        }
        if ( status )
            other.addFriendReq(userName);
    }

    void addFriendReq(String userName) {
        incFriendReq.add(userName);
        try {
            Data.appendFriendReqToUserFile(this.userName, userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFriendReqs(List<String> friendReqs) {
        incFriendReq = friendReqs;
    }

    public void addFriends(List<String> friends) {
        this.friends = friends;
    }
}