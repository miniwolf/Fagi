package com.fagi.controller.contentList;

import com.fagi.action.ActionHandler;
import com.fagi.action.Handler;
import com.fagi.action.items.OpenConversation;
import com.fagi.action.items.OpenConversationFromID;
import com.fagi.controller.MainScreen;
import javafx.scene.control.Label;

/**
 * @author miniwolf
 */
public enum ItemActions {
    OpenConversation;

    public static Handler OpenConversationFromID(MainScreen mainScreen, long id) {
        Handler actionHandler = new ActionHandler();
        actionHandler.addAction(new OpenConversationFromID(mainScreen, id));
        return actionHandler;
    }
}
