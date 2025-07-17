package com.fagi.handler;

import com.fagi.conversation.Conversation;
import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;
import com.fagi.model.Data;
import com.fagi.model.messages.message.TextMessage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Marcus on 04-07-2016.
 */
public class ConversationHandler implements Runnable {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(ConversationHandler.class);
    private final Data data;
    private final LinkedBlockingQueue<TextMessage> queue = new LinkedBlockingQueue<>();

    public ConversationHandler(Data data) {
        this.data = data;
    }

    @Override
    public void run() {
        while (!Thread
                .currentThread()
                .isInterrupted()) {
            tick();
        }
    }

    public void tick() {
        try {
            TextMessage message = queue.take();
            Conversation conversation = data.getConversation(message
                                                                     .getMessageInfo()
                                                                     .getConversationID());
            conversation
                    .getParticipants()
                    .stream()
                    .filter(data::isUserOnline)
                    .forEach(participant -> data
                            .getOutputAgent(participant)
                            .addMessage(message));

            conversation.addMessage(message);
            data.storeConversation(conversation);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOGGER.debug(
                    ie,
                    () -> "Interrupted ConversationHandler thread."
            );
        }
    }

    public void addMessage(TextMessage message) {
        queue.add(message);
    }

    public int queueSize() {
        return queue.size();
    }
}
