package com.fagi.handler.inputhandler;

import com.fagi.BaseFagiTest;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.handler.InputHandlerFactory;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.util.TestHelper;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Random;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * A base class to make it easy for all {@link InputHandler} tests to have the base set up ready for each test.
 * Also contains helpful methods to simplify st ups and mocks that are used multiple times.
 */
public abstract class BaseInputHandlerTest extends BaseFagiTest {
    protected OutputAgent outputAgent;
    protected Data data;
    protected InputAgent inputAgent;
    protected InputHandler inputHandler;
    protected ConversationHandler conversationHandler;

    /**
     * Creates an {@link InputHandler} and the classes and mocks needed for testing. Calls {@link BaseInputHandlerTest#beforeEach()} at the end.
     */
    @BeforeEach
    protected void setup() {
        data = Mockito.mock(Data.class);
        inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.mock(OutputAgent.class);
        conversationHandler = new ConversationHandler(data);

        inputHandler = InputHandlerFactory.createInputHandler(
                inputAgent,
                outputAgent,
                conversationHandler,
                data
        );

        beforeEach();
    }

    /**
     * A method that allows each test class to prepare its own setup before each test, but after the {@link InputHandler} is created.
     */
    abstract void beforeEach();

    /**
     * Creates an empty {@link Conversation} with the given participants
     *
     * @param participants the participants of the new {@link Conversation}
     * @return a {@link Conversation} with the id of 42
     */
    protected Conversation createConversation(
            String... participants) {
        var conversation = new Conversation(
                42,
                "Some conversation",
                ConversationType.Single
        );

        conversation
                .getParticipants()
                .addAll(Arrays.asList(participants));

        return conversation;
    }

    /**
     * Creates a {@link Conversation} and registers it in a given {@link Data} object
     *
     * @param participants the participants of the new {@link Conversation}
     * @return a {@link Conversation} with the id of 42
     */
    protected Conversation mockConversationAndRegisterInData(
            String... participants) {
        var conversation = createConversation(participants);

        doReturn(conversation)
                .when(data)
                .getConversation(conversation.getId());

        return conversation;
    }

    /**
     * Creates a {@link Conversation} with the given {@link User} as participant randomized id, name and a number of
     * {@link TextMessage}s with random contents and sender. The {@link Conversation} is registered in the given
     * {@link Data} object.
     *
     * @param user             a {@link User}
     * @param numberOfMessages the number of {@link TextMessage}s to be in the {@link Conversation}
     * @return a {@link Conversation}
     */
    protected Conversation createConversationWithRandomDataForUserAndRegisterInData(
            User user,
            int numberOfMessages) {
        Random random = new Random();
        var con = new Conversation(
                random.nextInt(100),
                TestHelper.getSaltString(10),
                ConversationType.Single
        );
        for (var i = 0; i < numberOfMessages; i++) {
            if (i > 0) {
                try {
                    Thread.sleep(1); // To have different timestamps
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            con.addMessage(new TextMessage(
                    TestHelper.getSaltString(10),
                    TestHelper.getSaltString(10),
                    con.getId()
            ));
        }
        con.addUser(user.getUserName());
        user.addConversationID(con.getId());
        when(data.getConversation(con.getId())).thenReturn(con);
        return con;
    }
}
