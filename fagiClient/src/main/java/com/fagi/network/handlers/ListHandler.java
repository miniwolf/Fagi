/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.lists.FriendRequestList;
import com.fagi.model.messages.lists.ListAccess;
import com.fagi.network.InputDistributor;
import com.fagi.network.InputHandler;

import com.fagi.network.handlers.container.Container;
import com.fagi.network.handlers.container.DefaultContainer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * The contact- and request list is updated from the server. This handler will manage the two
 * list objects.
 *
 * @author miniwolf
 */
public class ListHandler implements Handler {
    private final ListView<String> contactList;
    private final ListView<String> requestList;
    private Container friendContainer = new DefaultContainer();
    private Container friendRequestContainer = new DefaultContainer();
    private Runnable runnable = new DoubleContainerThreadHandler(friendContainer,
                                                                 friendRequestContainer, this);

    /**
     * Default constructor.
     *
     * @param contactList ListView referencing the contact list on the UI
     * @param requestList ListView referencing the request list on the UI
     */
    public ListHandler(ListView<String> contactList, ListView<String> requestList) {
        this.contactList = contactList;
        this.requestList = requestList;
        friendContainer.setThread(runnable);
        friendRequestContainer.setThread(runnable);
        InputDistributor.register(FriendList.class, friendContainer);
        InputDistributor.register(FriendRequestList.class, friendRequestContainer);
    }

    @Override
    public void handle(InGoingMessages object) {
        ListAccess access = (ListAccess) object.getAccess();
        if ( object instanceof FriendList ) {
            update(contactList, access);
        } else if ( object instanceof FriendRequestList ) {
            update(requestList, access);
        }
    }

    private void update(ListView<String> list, ListAccess input) {
        if ( input.getData().isEmpty() ) {
            Platform.runLater(() -> list.getItems().setAll(""));
            return;
        }

        ObservableList<String> observableList = list.getItems();
        Platform.runLater(() -> observableList.setAll(input.getData()));
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }
}
