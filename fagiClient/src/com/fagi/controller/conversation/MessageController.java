package com.fagi.controller.conversation;

import com.fagi.action.items.LoadFXML;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

/**
 * @author miniwolf
 */
public class MessageController extends HBox {
    @FXML private TextArea message;

    private final String stringMessage;
    private static final int MAX_COLUMN_LENGTH = 25;

    public MessageController(String stringMessage, String resource) {
        this.stringMessage = stringMessage;
        new LoadFXML(this, resource).execute();
    }

    @FXML
    private void initialize() {
        message.setText(stringMessage);
        setupMessageSize();
    }

    private void setupMessageSize() {
        int columnLength = stringMessage.length() / 2 + 2; // Magic computation
        message.setPrefColumnCount(
                columnLength > MAX_COLUMN_LENGTH ? MAX_COLUMN_LENGTH : columnLength);
        int rowCount = 1;
        int count = 0;
        char[] chars = stringMessage.toCharArray();
        for (char c : chars) {
            if ((count %= MAX_COLUMN_LENGTH) == 0) {
                rowCount++;
            }
            if (c == '\n') {
                rowCount++;
                count = 0;
                continue;
            }
            count++;
        }
        while (columnLength > MAX_COLUMN_LENGTH) {
            rowCount++;
            columnLength -= MAX_COLUMN_LENGTH;
        }
        message.setPrefRowCount(rowCount);
    }
}
