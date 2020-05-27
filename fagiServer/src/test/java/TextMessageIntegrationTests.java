import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class TextMessageIntegrationTests {
    private OutputAgent outputAgent;
    private ConversationHandler conversationHandler;
    private Data data;

    private InputHandler inputHandler;
    private Conversation conversation;
    private TextMessage message;

    @BeforeEach
    void setup() {
        data = Mockito.mock(Data.class);
        InputAgent inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.spy(OutputAgent.class);
        conversationHandler = new ConversationHandler(data);
        inputHandler = new InputHandler(inputAgent, outputAgent, conversationHandler, data);

        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);

        conversation = new Conversation(42, "Some conversation", ConversationType.Single);
        conversation.addUser("sender");
        conversation.addUser("receiver");

        message = new TextMessage("Hullo", "sender", 42);
    }

    @Test
    void sendingAMessageToConversationWithOnlineParticipant_ShouldSendMessageToThatUser() {
        when(data.getConversation(Mockito.anyLong())).thenReturn(conversation);
        when(data.isUserOnline("receiver")).thenReturn(true);

        inputHandler.handleInput(message);
        conversationHandler.tick();

        Mockito
                .verify(outputAgent, times(1))
                .addMessage(message);
    }

    @Test
    void sendingAMessageToConversation_ShouldAddMessageToConversation() {
        when(data.getConversation(Mockito.anyLong())).thenReturn(conversation);

        inputHandler.handleInput(message);
        conversationHandler.tick();

        Assertions.assertEquals(1,
                                conversation
                                        .getMessages()
                                        .size()
        );
    }

    @Test
    void sendingAMessageToConversation_ShouldResultInConversationBeingStored() {
        when(data.getConversation(Mockito.anyLong())).thenReturn(conversation);

        inputHandler.handleInput(message);
        conversationHandler.tick();

        Mockito
                .verify(data, times(1))
                .storeConversation(conversation);
    }
}
