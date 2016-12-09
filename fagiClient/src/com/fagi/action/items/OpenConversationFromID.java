package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.controller.conversation.ConversationController;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.network.Communication;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author miniwolf
 */
public class OpenConversationFromID implements Action {
    private final MainScreen mainScreen;
    private final long id;

    public OpenConversationFromID(MainScreen mainScreen, long id) {
        this.mainScreen = mainScreen;
        this.id = id;
    }

    @Override
    public void execute() {
        Optional<Conversation> optional = mainScreen
                .getConversations().stream()
                .filter(con -> con.getId() == id)
                .findFirst();

        if (!optional.isPresent()) {
            errorHandling();
        }

        Conversation conversation = optional.get();

        if ( conversation.getParticipants().equals(mainScreen.getCurrentConversation().getParticipants()) ) {
            return;
        }

        Communication communication = mainScreen.getCommunication();
        String username = mainScreen.getUsername();
        if ( conversation.getType() == ConversationType.Placeholder ) {
            ConversationType type = conversation.getParticipants().size() > 2 ? ConversationType.Multi : ConversationType.Single;
            conversation.setType(type);
            communication.sendObject(new GetAllConversationDataRequest(username, conversation.getId()));
        }

        ConversationController controller = new ConversationController(mainScreen.getPrimaryStage(), conversation, communication, username);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fagi/view/conversation/Conversation.fxml"));
        loader.setController(controller);
        try {
            BorderPane conversationBox = loader.load();
            mainScreen.addElement(conversationBox);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainScreen.setConversation(conversation);
        mainScreen.setConversationController(controller);
    }

    private Supplier<? extends Conversation> errorHandling() {
        System.err.println("OpenConversationFromID: Couldn't find conversation on ID <" + id + ">");
        throw new RuntimeException();
    }
}
