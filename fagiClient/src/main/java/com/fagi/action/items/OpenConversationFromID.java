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
public record OpenConversationFromID(MainScreen mainScreen) implements Action<Long> {
    @Override
    public void execute(Long id) {
        Optional<Conversation> optional = mainScreen
                .getConversations()
                .stream()
                .filter(con -> con.getId() == id)
                .findFirst();

        if (optional.isEmpty()) {
            errorHandling(id);
            return; // NOTE: This is never reached
        }

        Conversation conversation = optional.get();
        if (mainScreen.hasCurrentOpenConversation(conversation)) {
            ConversationController controller = mainScreen.getControllerFromConversation(conversation);
            controller.closeConversation();
            addConversationToMain(conversation, controller);
            return;
        }

        Communication communication = mainScreen.getCommunication();
        String username = mainScreen.getUsername();
        if (conversation.getType() == ConversationType.Placeholder) {
            ConversationType type = conversation
                    .getParticipants()
                    .size() > 2 ? ConversationType.Multi : ConversationType.Single;
            conversation.setType(type);
            communication.sendObject(new GetAllConversationDataRequest(username, conversation.getId()));
        }

        ConversationController controller = new ConversationController(mainScreen, conversation, username);
        addConversationToMain(conversation, controller);
    }

    private void addConversationToMain(
            Conversation conversation,
            ConversationController controller) {
        mainScreen.addElement(controller);
        mainScreen.addCurrentConversation(conversation);
        mainScreen.addController(controller);
    }

    private void errorHandling(long id) {
        System.err.println("OpenConversationFromID: Couldn't find conversation on ID <" + id + ">");
        throw new RuntimeException();
    }
}
