package com.fagi.uimodel;

import com.fagi.controller.contentList.ContactItemController;
import javafx.scene.layout.Pane;

/**
 * Created by costa on 13-12-2016.
 */
public record FriendListItem(ContactItemController controller, Pane pane) {
}
