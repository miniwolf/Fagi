package com.fagi.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Created by MFH on 05-10-2014.
 */
public class ErrorBoxController {
    @FXML
    private Button okBtn;
    @FXML
    private Label messageArea;

    private Stage stage;

    public void handleButton() {
        stage.close();
    }

    public void setText(String text) {
        messageArea.setText(text);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
