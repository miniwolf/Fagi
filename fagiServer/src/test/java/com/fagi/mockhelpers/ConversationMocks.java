package com.fagi.mockhelpers;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.messages.message.TextMessage;
import org.apache.commons.text.RandomStringGenerator;

import java.util.Arrays;
import java.util.Random;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Helper functions to create {@link Conversation}s in test
 */
public class ConversationMocks {
    private ConversationMocks() {
        // Disallows newing the class
    }

    /**
     * Creates an empty {@link Conversation} with the given participants
     *
     * @param participants the participants of the new {@link Conversation}
     * @return a {@link Conversation} with the id of 42
     */
    public static Conversation createConversation(
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
     * @param data         the data object to register the {@link Conversation} in
     * @param participants the participants of the new {@link Conversation}
     * @return a {@link Conversation} with the id of 42
     */
    public static Conversation mockConversationAndRegisterInData(
            Data data,
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
     * @param data the data object to register the {@link Conversation} in
     * @param user a {@link User}
     * @param numberOfMessages the number of {@link TextMessage}s to be in the {@link Conversation}
     * @return a {@link Conversation}
     */
    public static Conversation createConversationWithRandomDataForUserAndRegisterInData(
            Data data,
            User user,
            int numberOfMessages) {
        Random random = new Random();
        RandomStringGenerator randomString = new RandomStringGenerator.Builder().get();
        var con = new Conversation(
                random.nextInt(100),
                randomString.generate(10),
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
                    randomString.generate(10),
                    randomString.generate(10),
                    con.getId()
            ));
        }
        con.addUser(user.getUserName());
        user.addConversationID(con.getId());
        when(data.getConversation(con.getId())).thenReturn(con);
        return con;
    }
}
