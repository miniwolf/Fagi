package com.fagi.uimodel;

import com.fagi.controller.MainScreen;
import com.fagi.controller.contentList.ContactItemController;
import com.fagi.model.Friend;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by costa on 13-12-2016.
 */
public class FriendMapWrapper {
    private final MainScreen mainScreen;
    private Map<Friend, FriendListItem> map = new HashMap<>();

    public FriendMapWrapper(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    public void register(
            Friend friend,
            ContactItemController contactItemController,
            Pane pane) {
        map.put(friend, new FriendListItem(contactItemController, pane));
    }

    public void toggleUserStatus(String username) {
        Friend f = map
                .keySet()
                .stream()
                .filter(x -> x
                        .username()
                        .equals(username))
                .findFirst()
                .get();
        FriendListItem item = map.get(f);
        var newFriend = new Friend(f.username(), !f.online());
        map.remove(f);
        map.put(newFriend, item);

        item
                .controller()
                .toggleStatus(f.online());

        List<FriendListItem> sortedFriendItems = map
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        mainScreen
                .getContactContentController()
                .updateAndRedraw(sortedFriendItems);
    }
}
