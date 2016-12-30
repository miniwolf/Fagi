package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.controller.conversation.ConversationController;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.network.Communication;

import java.util.Optional;

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
            return; // NOTE: This is never reached
        }

        Conversation conversation = optional.get();
        if (!optional.isPresent() || (mainScreen.getCurrentConversation() != null
                                      && conversation.getParticipants()
                                                     .equals(mainScreen.getCurrentConversation()
                                                                       .getParticipants()))) {
            return;
        }

        Communication communication = mainScreen.getCommunication();
        String username = mainScreen.getUsername();
        if (conversation.getType() == ConversationType.Placeholder) {
            ConversationType type =
                conversation.getParticipants().size() > 2 ? ConversationType.Multi
                                                          : ConversationType.Single;
            conversation.setType(type);
            communication
                .sendObject(new GetAllConversationDataRequest(username, conversation.getId()));
        }

        ConversationController controller =
            new ConversationController(mainScreen, conversation, username);
        mainScreen.addElement(controller);
        mainScreen.setConversation(conversation);
        mainScreen.setConversationController(controller);
    }

    private void errorHandling() {
        System.err.println("OpenConversationFromID: Couldn't find conversation on ID <" + id + ">");
        throw new RuntimeException();
    }
}
