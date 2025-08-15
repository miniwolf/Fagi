package com.fagi.handler.request.user;

import com.fagi.handler.request.RequestHandler;
import com.fagi.model.CreateUser;
import com.fagi.model.Data;
import com.fagi.model.InviteCode;
import com.fagi.model.InviteCodeContainer;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.IllegalInviteCode;
import com.fagi.responses.Response;
import com.fagi.worker.OutputAgent;

/**
 * // TODO: zargess - javadoc
 */
public class CreateUserRequestHandler implements RequestHandler<CreateUser> {
    private final OutputAgent outputAgent;
    private final Data data;

    public CreateUserRequestHandler(
            OutputAgent outputAgent,
            Data data) {
        this.outputAgent = outputAgent;
        this.data = data;
    }

    @Override
    public Class<CreateUser> getRequestClass() {
        return CreateUser.class;
    }

    @Override
    public void handleRequest(CreateUser request) {
        outputAgent.addResponse(handleCreateUser(request));
    }

    private Object handleCreateUser(CreateUser request) {
        InviteCodeContainer inviteCodes = data.loadInviteCodes();
        InviteCode inviteCode = request.inviteCode();
        if (!inviteCodes.contains(inviteCode)) {
            return new IllegalInviteCode();
        }

        try {
            Response response = data.createUser(
                    request.username(),
                    request.password()
            );
            if (response instanceof AllIsWell) {
                inviteCodes.remove(inviteCode);
                data.storeInviteCodes(inviteCodes);
            }
            return response;
        } catch (Exception e) {
            return e;
        }
    }
}
