/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.InputHandler;
import com.fagi.network.handlers.container.Container;
import com.fagi.network.handlers.container.DefaultContainer;

import java.util.Optional;

/**
 * @author miniwolf
 */
public class TextMessageHandler implements Handler {
	private Container container = new DefaultContainer();
	private Runnable runnable = new DefaultThreadHandler(container, this);
	private final MainScreen mainScreen;

	public TextMessageHandler(MainScreen mainScreen) {
		container.setThread(runnable);
		InputHandler.register(TextMessage.class, container);
		this.mainScreen = mainScreen;
	}

	@Override
	public void handle(InGoingMessages inMessage) {
		TextMessage message = (TextMessage) inMessage;
		Optional<Conversation> first = mainScreen.getConversations().stream().filter(c -> c.getId() == message.getMessage().getConversationID()).findFirst();
		if ( !first.isPresent() ) {
			System.err.println("Server sent a message before it sent the conversation of ID '" + message.getMessage().getConversationID() + "'to the profile.");
			return;
		}
		Conversation conversation = first.get();
		if ( mainScreen.getCurrentConversation().getId() == conversation.getId() ) {
			mainScreen.getConversationController().addMessage(message);
		}
		conversation.getData().addMessage(message);
	}

	@Override
	public Runnable getRunnable() {
		return runnable;
	}
}
