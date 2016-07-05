import com.fagi.config.ServerConfig;
import com.fagi.conversation.Conversation;
import com.fagi.encryption.AES;
import com.fagi.model.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Marcus on 05-07-2016.
 */


public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        ServerConfig config = ServerConfig.pathToServerConfig("config/serverinfo.config");
        AES aes = new AES();
        aes.generateKey(128);
        Communication com = new Communication(config.getIp(), config.getPort(), aes, config.getServerKey());

        ArrayList<String> participants = new ArrayList<>();
        participants.add("humus");

        com.sendObject(new CreateConversationRequest(participants));

        com.sendObject(new Login("humus", "1234"));

        com.sendObject(new TextMessage("humus fucking lumus", "humus", 0));
        com.sendObject(new TextMessage("test", "humus", 0));
        com.sendObject(new TextMessage("asdasd", "humus", 0));
        com.sendObject(new TextMessage("lalala", "humus", 0));
        com.sendObject(new TextMessage("qwerty", "humus", 0));
        com.sendObject(new TextMessage(":D", "humus", 0));

        Thread.sleep(5000);

        com.sendObject(new UpdateHistoryRequest(2, 2));

        HistoryUpdates updates = null;

        while (updates == null) {
            updates = com.getHistoryUpdates();
        }

        for (TextMessage msg : updates.getUpdates()) {
            System.out.println(msg.getData());
        }

        com.sendObject(new Logout());
    }
}
