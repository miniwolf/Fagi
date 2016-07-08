/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

/**
 * @author miniwolf
 */
public class SearchContentController {
    @FXML private VBox contentList;

    public void addToContentList(Parent parent) {
        contentList.getChildren().add(parent);
    }
}
