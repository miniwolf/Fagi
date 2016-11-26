package com.fagi.controller.contentList;

import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.model.CreateConversationRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author miniwolf and zargess
 */
public class ContactItemController {
    private final MainScreen mainScreen;
    @FXML private Label userName;
    @FXML private Label date;
    @FXML private Label lastMessage;

    public ContactItemController(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    @FXML
    public void openConversation() {
        Conversation conversation;
        Optional<Conversation> optConversation = mainScreen.getConversations().stream().filter(con -> con.getParticipants().size() > 0 && con.getParticipants().contains(userName.getText())).findFirst();
        if ( optConversation.isPresent() ) {
            conversation = optConversation.get();
        } else {
            List<String> participants = new ArrayList<>();
            participants.add(mainScreen.getUsername());
            participants.add(userName.getText());
            mainScreen.getCommunication().sendObject(new CreateConversationRequest(participants));
            conversation = waitForConversation();
        }
        mainScreen.setConversation(conversation);
    }

    private Conversation waitForConversation() {
        Optional<Conversation> optConversation;
        while ( !(optConversation = mainScreen.getConversations().stream().filter(con -> con.getParticipants().size() > 0 && con.getParticipants().contains(userName.getText())).findFirst()).isPresent() ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return optConversation.get();
    }
}
