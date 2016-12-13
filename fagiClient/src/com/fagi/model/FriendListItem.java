package com.fagi.model;

import com.fagi.controller.contentList.ContactItemController;
import javafx.scene.layout.Pane;

/**
 * Created by costa on 13-12-2016.
 */
public class FriendListItem {
    private final ContactItemController controller;
    private final Pane pane;

    public FriendListItem(ContactItemController controller, Pane pane) {
        this.controller = controller;
        this.pane = pane;
    }

    public ContactItemController getController() {
        return controller;
    }

    public Pane getPane() {
        return pane;
    }
}
