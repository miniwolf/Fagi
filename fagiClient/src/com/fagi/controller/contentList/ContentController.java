/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.contentList;

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
public class ContentController {
    @FXML private VBox contentList;

    public void addToContentList(Parent parent) {
        contentList.getChildren().add(parent);
    }

    public void addAllToContentList(List<Parent> parents) {
        contentList.getChildren().addAll(parents);
    }

    public void updateAndRedraw(List<FriendListItem> sortedFriendItems) {
        Platform.runLater(() -> {
            contentList.getChildren().clear();
            contentList.getChildren().addAll(sortedFriendItems.stream().map(FriendListItem::getPane).collect(Collectors.toList()));
        });
    }
}
