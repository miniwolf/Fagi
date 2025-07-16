package com.fagi.test;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class TestIssue extends BorderPane {
    @FXML private TextArea conversationTextarea;

    @FXML
    private void initialize() {
        conversationTextarea.setOnKeyPressed(this::handleEnterBehaviour);
    }

    private void handleEnterBehaviour(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER) {
            return;
        }

        sendMessage();
        conversationTextarea.clear();
        event.consume();
    }

    private void sendMessage() {
        System.out.println("Message: " + conversationTextarea.getText());
    }
}
