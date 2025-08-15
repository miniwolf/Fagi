package com.fagi.handler.request.friend;

import com.fagi.model.Data;
import com.fagi.model.Friend;
import com.fagi.model.User;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.worker.InputAgent;

import java.util.ArrayList;
import java.util.List;

public class FriendListHelper {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException if called via reflection
     */
    private FriendListHelper() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    public static FriendList getFriendList(
            InputAgent inputAgent,
            Data data) {
        User user = data.getUser(inputAgent.getUsername());
        List<String> friendUsernames = user.getFriends();
        List<Friend> friends = new ArrayList<>();

        for (String friendUsername : friendUsernames) {
            friends.add(new Friend(
                    friendUsername,
                    data.isUserOnline(friendUsername)
            ));
        }

        return new FriendList(new DefaultListAccess<>(friends));
    }
}
