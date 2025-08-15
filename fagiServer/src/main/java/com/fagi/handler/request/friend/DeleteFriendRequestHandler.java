package com.fagi.handler.request.friend;

import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.DeleteFriend;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

public class DeleteFriendRequestHandler implements RequestHandler<DeleteFriend> {
    private final Data data;
    private final InputAgent inputAgent;
    private final OutputAgent outputAgent;

    public DeleteFriendRequestHandler(
            Data data,
            InputAgent inputAgent,
            OutputAgent outputAgent) {
        this.data = data;
        this.inputAgent = inputAgent;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<DeleteFriend> getRequestClass() {
        return DeleteFriend.class;
    }

    @Override
    public void handleRequest(DeleteFriend request) {
        outputAgent.addResponse(handleDeleteFriend(request));
    }

    private Object handleDeleteFriend(DeleteFriend request) {
        return data
                .getUser(inputAgent.getUsername())
                .removeFriend(
                        data,
                        request.friendUsername()
                );
    }
}
