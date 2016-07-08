import com.fagi.conversation.Conversation;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.model.messages.message.VideoMessage;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Sidheag on 2016-07-07.
 */
public class VideoConversationHandler implements Runnable {
    private LinkedBlockingQueue<VideoMessage> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while ( !Thread.currentThread().isInterrupted() ) {
            try {
                VideoMessage message = queue.take();
                Conversation conversation = Data.getConversation(message.getMessage().getConversationID());
                conversation.getParticipants().stream().filter(Data::isUserOnline)
                        .forEach(participant -> Data.getWorker(participant).addMessage(message));

                conversation.addMessage(message);
                Data.storeConversation(conversation);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addMessage(VideoMessage message) {
        queue.add(message);
    }
}
