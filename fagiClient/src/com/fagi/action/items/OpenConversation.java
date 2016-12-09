package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.model.conversation.CreateConversationRequest;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author miniwolf
 */
public class OpenConversation implements Action {
    private MainScreen mainScreen;
    private Label username;

    public OpenConversation(MainScreen mainScreen, Label username) {
        this.mainScreen = mainScreen;
        this.username = username;
    }

    @Override
    public void execute() {
        Conversation conversation;
        Optional<Conversation> optConversation = mainScreen.getConversations().stream().filter(con -> con.getParticipants().size() > 0 && con.getParticipants().contains(username.getText())).findFirst();
        if ( optConversation.isPresent() ) {
            conversation = optConversation.get();
        } else {
            List<String> participants = new ArrayList<>();
            participants.add(mainScreen.getUsername());
            participants.add(username.getText());
            mainScreen.getCommunication().sendObject(new CreateConversationRequest(participants));
            conversation = waitForConversation();
        }
        new OpenConversationFromID(mainScreen, conversation.getId()).execute();
    }

    private Conversation waitForConversation() {
        Optional<Conversation> optConversation;
        while ( !(optConversation = mainScreen.getConversations().stream()
                                              .filter(con -> con.getParticipants().size() > 0
                                                             && con.getParticipants().contains(username.getText()))
                                              .findFirst()).isPresent() ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return optConversation.get();
    }
}
