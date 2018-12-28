package com.fagi.action.items;

import com.fagi.controller.MainScreen;
import com.fagi.controller.conversation.ConversationController;
import com.fagi.conversation.Conversation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import rules.JavaFXThreadingRule;

/**
 * Created by miniwolf on 01-04-2017.
 */
public class LoadFXMLTest {
    @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    private LoadFXML loadFXML;
    private ConversationController mock;

    @Before
    public void init() {
        System.out.println("Starting LoadFXMLtests");
        MainScreen mainScreen = Mockito.mock(MainScreen.class);
        this.mock = new ConversationController(mainScreen, new Conversation(), "");
        loadFXML = new LoadFXML(this.mock, "/com/fagi/view/conversation/Conversation.fxml");
    }

    @Test
    public void test() {
        loadFXML.execute();
        Assert.assertNotNull("Should contain center element", mock.getCenter());
    }
}
