import com.fagi.config.ServerConfig;
import com.fagi.conversation.Conversation;
import com.fagi.encryption.AES;
import com.fagi.model.*;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.DefaultMessage;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.Communication;
import com.fagi.network.InputHandler;
import com.fagi.network.handlers.container.DefaultContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Marcus on 05-07-2016.
 */


public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        ServerConfig config = ServerConfig.pathToServerConfig("config/serverinfo.config");
        AES aes = new AES();
        aes.generateKey(128);

        DefaultContainer textContainer = new DefaultContainer();
        DefaultContainer conversationContainer = new DefaultContainer();
        DefaultContainer historyContainer = new DefaultContainer();

        textContainer.setThread(() -> {

        });
        conversationContainer.setThread(() -> {

        });
        historyContainer.setThread(() -> {

        });

        InputHandler.register(TextMessage.class, textContainer);
        InputHandler.register(Conversation.class, conversationContainer);
        InputHandler.register(HistoryUpdates.class, historyContainer);

        Communication com = new Communication(config.getIp(), config.getPort(), aes, config.getServerKey());

        ArrayList<String> participants = new ArrayList<>();
        participants.add("humus");

        com.sendObject(new CreateUser("humus", "1234"));

        com.sendObject(new Login("humus", "1234"));

        com.sendObject(new CreateConversationRequest(participants));

        com.sendObject(new TextMessage("humus fucking lumus", "humus", 0));
        com.sendObject(new TextMessage("test", "humus", 0));
        com.sendObject(new TextMessage("asdasd", "humus", 0));
        com.sendObject(new TextMessage("lalala", "humus", 0));
        com.sendObject(new TextMessage("qwerty", "humus", 0));
        com.sendObject(new TextMessage(":D", "humus", 0));

        Thread.sleep(5000);

        com.sendObject(new UpdateHistoryRequest("humus", 0, 2));

        HistoryUpdates updates = (HistoryUpdates) ((LinkedBlockingQueue<InGoingMessages>)historyContainer.getQueue()).take().getAccess().getData();

        for (TextMessage msg : updates.getUpdates()) {
            System.out.println(msg.getData());
        }

        com.sendObject(new Logout());
    }
}
