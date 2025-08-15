package com.fagi.handler.request.friend;

import com.fagi.handler.InputHandler;
import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.FriendRequest;
import com.fagi.model.GetFriendListRequest;
import com.fagi.model.User;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.Response;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

public class FriendRequestRequestHandler implements RequestHandler<FriendRequest> {
    private final Data data;
    private final InputAgent inputAgent;
    private final OutputAgent outputAgent;

    public FriendRequestRequestHandler(
            Data data,
            InputAgent inputAgent,
            OutputAgent outputAgent) {
        this.data = data;
        this.inputAgent = inputAgent;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<FriendRequest> getRequestClass() {
        return FriendRequest.class;
    }

    @Override
    public void handleRequest(FriendRequest request) {
        outputAgent.addResponse(handleFriendRequest(request));
    }

    private Object handleFriendRequest(FriendRequest request) {
        User user = data.getUser(inputAgent.getUsername());
        Response response = user.requestFriend(
                data,
                request
        );
        if (!(response instanceof AllIsWell)) {
            return response;
        }

        if (data.isUserOnline(request.friendUsername())) {
            InputHandler inputHandler = data
                    .getInputAgent(request.friendUsername())
                    .getInputHandler();
            inputHandler.handleInput(new GetFriendListRequest(request.friendUsername()));
        }
        return FriendListHelper.getFriendList(
                inputAgent,
                data
        );
    }
}
