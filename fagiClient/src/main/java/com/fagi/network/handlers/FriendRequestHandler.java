package com.fagi.network.handlers;

import com.fagi.action.items.OpenReceivedFriendRequest;
import com.fagi.controller.MainScreen;
import com.fagi.controller.contentList.ContentController;
import com.fagi.controller.contentList.InviteItemController;
import com.fagi.conversation.Conversation;
import com.fagi.model.FriendRequest;
import com.fagi.model.messages.lists.FriendRequestList;
import javafx.application.Platform;

import java.util.Date;

/**
 * Created by costa on 11-12-2016.
 */
public record FriendRequestHandler(MainScreen mainScreen) implements Handler<FriendRequestList> {

    @Override
    public void handle(FriendRequestList friendRequestList) {
        mainScreen.setFriendRequestList(friendRequestList);

        ContentController contentController = new ContentController("/view/content/ContentList.fxml");

        for (FriendRequest request : friendRequestList
                .access()
                .data()) {
            InviteItemController messageItemController = new InviteItemController(mainScreen.getUsername(),
                    new OpenReceivedFriendRequest(
                            mainScreen),
                    new Date(),
                    request
            );
            contentController.addToContentList(messageItemController);
        }

        for (Conversation conversation : mainScreen.getConversations()) {
            contentController.addToContentList(mainScreen.createMessageItem(conversation));
        }

        mainScreen.setConversationContentController(contentController);
        Platform.runLater(() -> mainScreen.setScrollPaneContent(MainScreen.PaneContent.Messages, contentController));
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
        };
    }
}
