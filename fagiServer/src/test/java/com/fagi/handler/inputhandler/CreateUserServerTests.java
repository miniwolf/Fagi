package com.fagi.handler.inputhandler;

import com.fagi.model.CreateUser;
import com.fagi.model.InviteCode;
import com.fagi.model.InviteCodeContainer;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.IllegalInviteCode;
import com.fagi.responses.UserExists;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class CreateUserServerTests extends BaseInputHandlerTest {
    private InviteCodeContainer inviteCodeContainer;

    void beforeEach() {
        inviteCodeContainer = new InviteCodeContainer(new ArrayList<>(Collections.singletonList(new InviteCode("42"))));
        doReturn(inviteCodeContainer)
                .when(data)
                .loadInviteCodes();
    }

    @Test
    void creatingUserWithIllegalInviteCode_ShouldResultInIllegalInviteCodeResponse() {
        var createUser = new CreateUser(
                "bob",
                "123",
                new InviteCode("23")
        );

        inputHandler.handleInput(createUser);

        var argumentCaptor = ArgumentCaptor.forClass(IllegalInviteCode.class);
        Mockito
                .verify(
                        outputAgent,
                        times(1)
                )
                .addResponse(argumentCaptor.capture());

        Assertions.assertNotNull(argumentCaptor.getValue());
    }

    @Test
    void creatingUserFails_ShouldResultInInviteCodeNotBeingDeleted() {
        var inviteCode = inviteCodeContainer
                .codes()
                .getFirst();
        var createUser = new CreateUser(
                "bob",
                "123",
                inviteCode
        );

        doReturn(new UserExists())
                .when(data)
                .createUser(
                        createUser.username(),
                        createUser.password()
                );

        inputHandler.handleInput(createUser);

        Assertions.assertTrue(inviteCodeContainer.contains(inviteCode));
    }

    @Test
    void createUserSucceeds_ShouldDeleteInviteCode() {
        var inviteCode = inviteCodeContainer
                .codes()
                .getFirst();
        var createUser = new CreateUser(
                "bob",
                "123",
                inviteCode
        );

        doReturn(new AllIsWell())
                .when(data)
                .createUser(
                        createUser.username(),
                        createUser.password()
                );

        inputHandler.handleInput(createUser);

        var argumentCaptor = ArgumentCaptor.forClass(InviteCodeContainer.class);
        Mockito
                .verify(data,
                        times(1))
                .storeInviteCodes(argumentCaptor.capture());

        Assertions.assertAll(
                () -> Assertions.assertFalse(inviteCodeContainer.contains(inviteCode)),
                () -> Assertions.assertEquals(inviteCodeContainer, argumentCaptor.getValue())
        );
    }

    @Test
    void createUserSucceeds_ShouldResultInAllIsWellResponse() {
        var inviteCode = inviteCodeContainer
                .codes()
                .getFirst();
        var createUser = new CreateUser(
                "bob",
                "123",
                inviteCode
        );

        doReturn(new AllIsWell())
                .when(data)
                .createUser(
                        createUser.username(),
                        createUser.password()
                );

        inputHandler.handleInput(createUser);

        var argumentCaptor = ArgumentCaptor.forClass(AllIsWell.class);
        Mockito
                .verify(
                        outputAgent,
                        times(1)
                )
                .addResponse(argumentCaptor.capture());

        Assertions.assertNotNull(argumentCaptor.getValue());
    }

    @Test
    void whenStoreUserFailsWithException_ShouldReturnThatException() {
        var exception = new IllegalStateException("Fisk");

        when(data.createUser(
                any(),
                any()
        )).thenThrow(exception);

        var inviteCode = inviteCodeContainer
                .codes()
                .getFirst();
        var createUser = new CreateUser(
                "bob",
                "123",
                inviteCode
        );

        inputHandler.handleInput(createUser);

        var response = OutputAgentTestUtil.captureResponse(
                outputAgent,
                IllegalStateException.class
        );

        Assertions.assertEquals(
                exception,
                response
        );
    }
}
