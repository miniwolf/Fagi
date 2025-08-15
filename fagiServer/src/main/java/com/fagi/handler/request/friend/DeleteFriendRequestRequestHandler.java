package com.fagi.handler.request.friend;

import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.DeleteFriendRequest;
import com.fagi.responses.Response;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

public class DeleteFriendRequestRequestHandler implements RequestHandler<DeleteFriendRequest> {
    private final Data data;
    private final InputAgent inputAgent;
    private final OutputAgent outputAgent;

    public DeleteFriendRequestRequestHandler(
            Data data,
            InputAgent inputAgent,
            OutputAgent outputAgent) {
        this.data = data;
        this.inputAgent = inputAgent;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<DeleteFriendRequest> getRequestClass() {
        return DeleteFriendRequest.class;
    }

    @Override
    public void handleRequest(DeleteFriendRequest request) {
        outputAgent.addResponse(handleDeleteFriendRequest(request));
    }

    private Response handleDeleteFriendRequest(DeleteFriendRequest request) {
        return data
                .getUser(inputAgent.getUsername())
                .removeFriendRequest(
                        data,
                        request.friendUsername()
                );
    }
}
