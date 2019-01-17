package com.fagi.action.items;

import com.fagi.controller.MainScreen;
import com.fagi.controller.conversation.ConversationController;
import com.fagi.conversation.Conversation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import rules.JavaFXThreadingExtension;

/**
 * Created by miniwolf on 01-04-2017.
 */
@ExtendWith(JavaFXThreadingExtension.class)
public class LoadFXMLTest {
    private LoadFXML loadFXML;
    private ConversationController mock;

    @BeforeEach
    public void init() {
        System.out.println("Starting LoadFXMLtests");
        var mainScreen = Mockito.mock(MainScreen.class);
        this.mock = new ConversationController(mainScreen, new Conversation(), "");
        loadFXML = new LoadFXML(this.mock, "/com/fagi/view/conversation/Conversation.fxml");
    }

    @Test
    public void test() {
        loadFXML.execute();
        Assertions.assertNotNull(mock.getCenter(), "Should contain center element");
    }
}
