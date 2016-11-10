/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.network.ChatManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;

/**
 * @author miniwolf
 */
public class CreatePasswordController implements LoginController {
	@FXML private PasswordField password;
	@FXML private PasswordField passwordRepeat;
	@FXML Button loginBtn;
	@FXML private Label messageLabel;

	private MasterLogin masterLogin;

	public CreatePasswordController(MasterLogin masterLogin) {
		this.masterLogin = masterLogin;
	}

	@Override
	public void next() {
		if ( !password.getText().equals(passwordRepeat.getText()) ) {
			messageLabel.setText("Passwords does not match");
			return;
		}
		masterLogin.setPassword(password.getText());
		if ( createUser() ) {
			masterLogin.next();
		}
	}

	@FXML
	public void initialize() {
		Platform.runLater(() -> password.getParent().requestFocus());
	}

	private boolean createUser() {
		return ChatManager.handleCreateUser(masterLogin.getUsername(), password.getText(),
				passwordRepeat.getText(), messageLabel);
	}

	@Override
	public void handleQuit() {
		masterLogin.handleQuit();
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		masterLogin.mousePressed(mouseEvent);
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
		masterLogin.mouseDragged(mouseEvent);
	}

	@Override
	public void setMessage(String message) {
		messageLabel.setText(message);
	}

	@Override
	public String getMessageLabel() {
		return messageLabel.getText();
	}
}
