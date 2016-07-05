import com.fagi.conversation.Conversation;
import com.fagi.model.Message;
import com.fagi.model.TextMessage;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Marcus on 04-07-2016.
 */
public class ConversationHandler implements Runnable {
    private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Message message = queue.take();
                Conversation conversation = Data.getConversation(message.getConversationID());
                conversation.getParticipants().stream().filter(Data::isUserOnline)
                                                       .forEach(participant -> Data.getWorker(participant).addMessage(message));

                if(message instanceof TextMessage) {
                    TextMessage txtMsg = (TextMessage)message;
                    conversation.addMessage(txtMsg);
                    Data.storeConversation(conversation);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addMessage(Message message) {
        queue.add(message);
    }
}
