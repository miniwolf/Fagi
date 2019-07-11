package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.model.conversation.CreateConversationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author miniwolf
 */
public class OpenConversation implements Action<String> {
    private MainScreen mainScreen;
    private Action<Long> openConversation;

    public OpenConversation(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.openConversation = new OpenConversationFromID(mainScreen);
    }

    @Override
    public void execute(String username) {
        Optional<Conversation> optConversation = getFirstConversation(username);

        Conversation conversation;
        if (optConversation.isPresent()) {
            conversation = optConversation.get();
        } else {
            List<String> participants = new ArrayList<>() {{
                add(mainScreen.getUsername());
                add(username);
            }};
            mainScreen.getCommunication().sendObject(new CreateConversationRequest(participants));
            conversation = waitForConversation(username);
        }
        openConversation.execute(conversation.getId());
    }

    private Conversation waitForConversation(String username) {
        Optional<Conversation> optConversation;
        while ((optConversation = getFirstConversation(username)).isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        return optConversation.get();
    }

    private Optional<Conversation> getFirstConversation(String username) {
        return mainScreen.getConversations()
                .stream()
                .filter(con -> conversationContainsUsername(username, con))
                .findFirst();
    }

    private boolean conversationContainsUsername(String username, Conversation con) {
        return con.getParticipants().size() > 0 && con.getParticipants().contains(username);
    }
}
