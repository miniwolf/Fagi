package com.fagi.mockhelpers;

import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.worker.OutputAgent;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.doReturn;

public class UserMocks {
    public static Optional<OutputAgent> mockOnlineStatusOfUser(Data data, User user, boolean isOnline) {
        doReturn(user)
                .when(data)
                .getUser(user.getUserName());

        doReturn(isOnline)
                .when(data)
                .isUserOnline(user.getUserName());

        if (isOnline) {
            OutputAgent newParticipantOutputAgent = Mockito.mock(OutputAgent.class);
            doReturn(newParticipantOutputAgent)
                    .when(data)
                    .getOutputAgent(user.getUserName());
            return Optional.of(newParticipantOutputAgent);
        }

        return Optional.empty();
    }
}
