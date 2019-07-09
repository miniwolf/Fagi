package com.fagi.network.handlers;

import com.fagi.action.items.OpenReceivedFriendRequest;
import com.fagi.controller.MainScreen;
import com.fagi.controller.contentList.ContentController;
import com.fagi.controller.contentList.MessageItemController;
import com.fagi.conversation.Conversation;
import com.fagi.model.FriendRequest;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.lists.FriendRequestList;
import javafx.application.Platform;

/**
 * Created by costa on 11-12-2016.
 */
public class FriendRequestHandler implements Handler {
    private final MainScreen mainScreen;

    public FriendRequestHandler(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(InGoingMessages object) {
        FriendRequestList friendRequestList = (FriendRequestList) object;
        mainScreen.setFriendRequestList(friendRequestList);

        ContentController contentController =
            new ContentController("/view/content/ContentList.fxml");

        for (FriendRequest request : friendRequestList.getAccess().getData()) {
            MessageItemController messageItemController =
                new MessageItemController(request.getFriendUsername(), request);
            messageItemController.getActionable().assign(
                new OpenReceivedFriendRequest(mainScreen, request));
            contentController.addToContentList(messageItemController);
        }

        for (Conversation conversation : mainScreen.getConversations()) {
            contentController.addToContentList(mainScreen.createMessageItem(conversation));
        }

        mainScreen.setConversationContentController(contentController);
        Platform.runLater(() -> mainScreen.setScrollPaneContent(MainScreen.PaneContent.Messages,
                                                                contentController));
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
        };
    }
}
