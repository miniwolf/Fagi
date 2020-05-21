package com.fagi.handler.inputhandler;

import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.CreateUser;
import com.fagi.model.Data;
import com.fagi.model.InviteCode;
import com.fagi.model.InviteCodeContainer;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.IllegalInviteCode;
import com.fagi.responses.UserExists;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;

class CreateUserServerTests {
    private OutputAgent outputAgent;
    private InputHandler inputHandler;
    private InviteCodeContainer inviteCodeContainer;
    private Data data;

    @BeforeEach
    void setup() {
        data = Mockito.mock(Data.class);
        var inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.mock(OutputAgent.class);
        ConversationHandler conversationHandler = new ConversationHandler(data);

        inputHandler = new InputHandler(inputAgent, outputAgent, conversationHandler, data);
        inviteCodeContainer = new InviteCodeContainer(new ArrayList<>(Collections.singletonList(new InviteCode(42))));
        doReturn(inviteCodeContainer)
                .when(data)
                .loadInviteCodes();
    }

    @Test
    void creatingUserWithIllegalInviteCode_ShouldResultInIllegalInviteCodeResponse() {
        var createUser = new CreateUser("bob", "123");
        createUser.setInviteCode(new InviteCode(23));

        inputHandler.handleInput(createUser);

        var argumentCaptor = ArgumentCaptor.forClass(IllegalInviteCode.class);
        Mockito
                .verify(outputAgent, times(1))
                .addResponse(argumentCaptor.capture());

        Assertions.assertTrue(argumentCaptor.getValue() instanceof IllegalInviteCode);
    }

    @Test
    void creatingUserFails_ShouldResultInInviteCodeNotBeingDeleted() {
        var inviteCode = inviteCodeContainer
                .getCodes()
                .get(0);
        var createUser = new CreateUser("bob", "123");
        createUser.setInviteCode(inviteCode);

        doReturn(new UserExists())
                .when(data)
                .createUser(createUser.getUsername(), createUser.getPassword());

        inputHandler.handleInput(createUser);

        Assertions.assertTrue(inviteCodeContainer.contains(inviteCode));
    }

    @Test
    void createUserSucceeds_ShouldDeleteInviteCode() {
        var inviteCode = inviteCodeContainer
                .getCodes()
                .get(0);
        var createUser = new CreateUser("bob", "123");
        createUser.setInviteCode(inviteCode);

        doReturn(new AllIsWell())
                .when(data)
                .createUser(createUser.getUsername(), createUser.getPassword());

        inputHandler.handleInput(createUser);

        Assertions.assertFalse(inviteCodeContainer.contains(inviteCode));
    }

    @Test
    void createUserSucceeds_ShouldResultInAllIsWellResponse() {
        var inviteCode = inviteCodeContainer
                .getCodes()
                .get(0);
        var createUser = new CreateUser("bob", "123");
        createUser.setInviteCode(inviteCode);

        doReturn(new AllIsWell())
                .when(data)
                .createUser(createUser.getUsername(), createUser.getPassword());

        inputHandler.handleInput(createUser);

        var argumentCaptor = ArgumentCaptor.forClass(AllIsWell.class);
        Mockito
                .verify(outputAgent, times(1))
                .addResponse(argumentCaptor.capture());

        Assertions.assertTrue(argumentCaptor.getValue() instanceof AllIsWell);
    }
}
