package com.fagi.handler.request.friend;

import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.GetFriendListRequest;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

public class GetFriendListRequestHandler implements RequestHandler<GetFriendListRequest> {
    private final Data data;
    private final InputAgent inputAgent;
    private final OutputAgent outputAgent;

    public GetFriendListRequestHandler(
            Data data,
            InputAgent inputAgent,
            OutputAgent outputAgent) {
        this.data = data;
        this.inputAgent = inputAgent;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<GetFriendListRequest> getRequestClass() {
        return GetFriendListRequest.class;
    }

    @Override
    public void handleRequest(GetFriendListRequest request) {
        outputAgent.addResponse(FriendListHelper.getFriendList(
                inputAgent,
                data
        ));
    }
}
