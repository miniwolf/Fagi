/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.contentList;

import com.fagi.action.items.LoadFXML;
import com.fagi.model.FriendListItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author miniwolf
 */
public class ContentController extends VBox {
    public ContentController(String resource) {
        new LoadFXML(this, resource).execute();
    }

    public synchronized void addToContentList(Parent parent) {
        Platform.runLater(() -> getChildren().add(parent));
    }

    public void addAllToContentList(List<Parent> parents) {
        getChildren().addAll(parents);
    }

    public void updateAndRedraw(List<FriendListItem> sortedFriendItems) {
        Platform.runLater(() -> {
            getChildren().clear();
            getChildren().addAll(sortedFriendItems.stream()
                                                  .map(FriendListItem::getPane)
                                                  .collect(Collectors.toList()));
        });
    }
}
