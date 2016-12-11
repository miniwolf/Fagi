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
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        FriendRequestList friendRequestList = (FriendRequestList)object;
        mainScreen.setFriendRequestList(friendRequestList);

        try {
            ContentController contentController = new ContentController();
            FXMLLoader contentLoader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/content/ContentList.fxml"));
            contentLoader.setController(contentController);
            VBox contactContent = contentLoader.load();

            for (FriendRequest request : friendRequestList.getAccess().getData()) {
                MessageItemController messageItemController = new MessageItemController(request.getFriendUsername(), request.getMessage().getMessageInfo().getConversationID());
                messageItemController.assign(new OpenReceivedFriendRequest(mainScreen, request));

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fagi/view/content/InviteItem.fxml"));
                loader.setController(messageItemController);
                try {
                    Pane pane = loader.load();

                    List<String> usernames = new ArrayList<>();
                    usernames.add(request.getFriendUsername());
                    messageItemController.setUsers(usernames);
                    messageItemController.setDate(request.getMessage().getMessageInfo().getTimestamp());

                    contentController.addToContentList(pane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (Conversation conversation : mainScreen.getConversations()) {
                contentController.addToContentList(mainScreen.createMessageItem(conversation));
            }

            mainScreen.setConversationContentController(contentController);

            Platform.runLater(() -> mainScreen.setScrollPaneContent(MainScreen.PaneContent.messages, contactContent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Runnable getRunnable() {
        return () -> {};
    }
}
