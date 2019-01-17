/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * User.java
 *
 * User data object.
 */

import com.fagi.model.FriendRequest;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TODO: Add description, password protection OTR:
 * https://otr.cypherpunks.ca/otr-wpes.pdf
 */
public class User implements Serializable {
    private final String pass;
    private final String userName;
    private List<String> friends;
    private List<Long> conversationIDs;
    private volatile List<FriendRequest> incFriendReq;

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

    public List<FriendRequest> getFriendReq() {
        return incFriendReq;
    }

    public void addFriend(User friend) {
        friends.add(friend.getUserName());
    }

    public Response requestFriend(FriendRequest arg) {
        String otherUser = arg.getFriendUsername();
        if ( friends.contains(otherUser) ) {
            return new UserExists();
        }
        User other = Data.getUser(otherUser);
        if ( other == null ) {
            return new NoSuchUser();
        }

        if (incFriendReq.stream().anyMatch(x -> x.getFriendUsername().equals(userName))) {
            Data.makeFriends(this, other);
            other.removeFriendRequest(userName);
            return removeFriendRequest(otherUser);
        }
        return other.addFriendReq(arg);
    }

    public Response removeFriendRequest(String userName) {
        Optional<FriendRequest> opt = incFriendReq.stream().filter(x -> x.getMessage().getMessageInfo().getSender().equals(userName)).findFirst();

        if (!opt.isPresent()) {
            return new NoSuchUser();
        }

        FriendRequest request = opt.get();

        if ( !incFriendReq.contains(request) ) {
            return new UserExists();
        }
        incFriendReq.remove(request);
        return Data.storeUser(this);
    }

    private Response addFriendReq(FriendRequest request) {
        if ( incFriendReq.contains(userName) ) {
            return new UserExists();
        }
        incFriendReq.add(request);
        return Data.storeUser(this);
    }

    public Response removeFriend(String otherUser) {
        if ( !friends.contains(otherUser)) {
            return new UserExists();
        }
        friends.remove(otherUser);
        return Data.storeUser(this);
    }

    public void addConversationID(long id) {
        conversationIDs.add(id);
    }

    public void removeConversationID(long id) {
        conversationIDs.remove(id);
    }

    public List<Long> getConversationIDs() {
        return conversationIDs;
    }
}