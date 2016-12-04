package com.fagi.handler;

import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * @author miniwolf
 */
public class SearchHandler {
    private TextField searchBox;
    private Pane searchHeader;

    public SearchHandler(TextField searchBox, Pane searchHeader) {
        this.searchBox = searchBox;
        this.searchHeader = searchHeader;
    }

    public void ToggleFocus(Boolean focusValue) {
        if ( focusValue ) {
            searchHeader.getStyleClass().remove("focused");
            searchBox.setPromptText("New conversation");
        } else {
            searchHeader.getStyleClass().add("focused");
            searchBox.setPromptText("Enter username");
        }
    }
}
