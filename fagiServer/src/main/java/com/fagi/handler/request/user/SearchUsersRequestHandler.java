package com.fagi.handler.request.user;

import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.SearchUsersRequest;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.User;
import com.fagi.responses.AllIsWell;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

import java.util.List;
import java.util.stream.Collectors;

public class SearchUsersRequestHandler implements RequestHandler<SearchUsersRequest> {
    private final Data data;
    private final InputAgent inputAgent;
    private final OutputAgent outputAgent;

    public SearchUsersRequestHandler(
            Data data,
            InputAgent inputAgent,
            OutputAgent outputAgent) {
        this.data = data;
        this.inputAgent = inputAgent;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<SearchUsersRequest> getRequestClass() {
        return SearchUsersRequest.class;
    }

    @Override
    public void handleRequest(SearchUsersRequest request) {
        outputAgent.addResponse(handleSearchUsersRequest(request));
    }

    private Object handleSearchUsersRequest(SearchUsersRequest request) {
        User user = data.getUser(inputAgent.getUsername());
        outputAgent.addResponse(new AllIsWell());

        List<String> usernames = data
                .getUserNames()
                .stream()
                .filter(username -> username.startsWith(request.searchString()))
                .filter(username -> !username.equals(request.sender()))
                .sorted()
                .toList();

        List<String> friends = usernames
                .stream()
                .filter(username -> user
                        .getFriends()
                        .contains(username))
                .sorted()
                .toList();

        List<String> nonFriends = usernames
                .stream()
                .filter(username -> !friends.contains(username))
                .collect(Collectors.toList());

        return new SearchUsersResult(nonFriends);
    }
}
