/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller;

import com.fagi.conversation.Conversation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.Date;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author miniwolf
 */
public class ConversationController {
	@FXML private Label name;
	@FXML private Label date;

	private Conversation conversation;

	public ConversationController(Conversation conversation) {
		this.conversation = conversation;
	}

	@FXML
	public void initialize() {
		String titleNames = conversation.getParticipants().stream().collect(Collectors.joining(", "));
		name.setText(titleNames);

		String dateString = dateToString(conversation.getLastMessageDate());
		if ( "".equals(dateString) ) {
			date.setMinHeight(Region.USE_PREF_SIZE);
		}
		date.setText(dateString);
	}

	private String dateToString(Date s) {
		if ( s == null ) {
			return "";
		} else {
			// Convert into something like "active 3 mo ago" "active 1 w ago"
			return "active 3 mo ago";
		}
	}
}
