package com.fagi.server;

import com.fagi.model.InviteCodeContainer;
import com.fagi.utility.JsonFileOperations;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ServerInviteCodesTests extends ServerTests {
    @Test
    void givenNoInviteCodesFileExists_WhenNewServerObjectIsCreated_ThenInviteCodesAreStored() {
        new Server(
                serverPort,
                data
        );

        Mockito
                .verify(
                        data,
                        Mockito.times(1)
                )
                .storeInviteCodes(new InviteCodeContainer(new ArrayList<>()));
    }

    @Test
    void givenInviteCodesFileExists_WhenNewServerObjectIsCreated_ThenInviteCodesAreNotStored() throws IOException {
        var inviteCodesFile = new File(JsonFileOperations.INVITE_CODES_FILE_PATH);

        if (!inviteCodesFile.exists()) {
            inviteCodesFile.createNewFile();
        }

        new Server(
                serverPort,
                data
        );

        Mockito
                .verify(
                        data,
                        Mockito.never()
                )
                .storeInviteCodes(Mockito.any());
    }

}
