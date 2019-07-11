import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.Login;
import com.fagi.responses.UserOnline;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class LoginServerTests {

    @BeforeEach
    void setup() {

    }

    @Test
    void alreadyOnlineUserLoginAgain_ShouldGetUserOnlineResponse() {
        Data data = Mockito.mock(Data.class);
        InputAgent inputAgent = Mockito.mock(InputAgent.class);
        OutputAgent outputAgent = Mockito.mock(OutputAgent.class);
        var conversationHandler = new ConversationHandler(data);
        InputHandler inputhandler = new InputHandler(inputAgent, outputAgent, conversationHandler, data);
        var loginRequest = new Login("bob", "123");
        when(data.isUserOnline(Mockito.anyString())).thenReturn(true);

        inputhandler.handleInput(loginRequest);

        var argumentCaptor = ArgumentCaptor.forClass(UserOnline.class);
        Mockito.verify(outputAgent, times(1)).addResponse(argumentCaptor.capture());
    }
}
