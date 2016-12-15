import com.fagi.conversation.Conversation;
import com.fagi.model.messages.message.TextMessage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Marcus on 04-07-2016.
 */
public class ConversationHandler implements Runnable {
    private LinkedBlockingQueue<TextMessage> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TextMessage message = queue.take();
                Conversation conversation =
                    Data.getConversation(message.getMessageInfo().getConversationID());
                conversation.getParticipants().stream().filter(Data::isUserOnline)
                            .forEach(
                                participant -> Data.getWorker(participant).addMessage(message));

                conversation.addMessage(message);
                Data.storeConversation(conversation);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public void addMessage(TextMessage message) {
        queue.add(message);
    }
}
