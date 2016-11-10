package com.fagi.controller.conversation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author miniwolf
 */
public class MessageController {
	@FXML private Label message;

	private final String s;

	public MessageController(String s) {
		this.s = s;
	}

	@FXML
	public void initialize() {
		message.setText(s);
	}
}
