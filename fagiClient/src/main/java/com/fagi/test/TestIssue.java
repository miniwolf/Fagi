package com.fagi.test;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class TestIssue extends BorderPane {
    @FXML private TextArea conversationTextarea;

    @FXML
    private void initialize() {
    }

    // Method to give focus to the TextArea
    public void requestFocusOnTextArea() {
        conversationTextarea.requestFocus();
    }

    public String getMessage() {
        return conversationTextarea.getText();
    }
}
