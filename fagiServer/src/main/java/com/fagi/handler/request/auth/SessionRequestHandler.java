package com.fagi.handler.request.auth;

import com.fagi.encryption.AES;
import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.Session;
import com.fagi.responses.AllIsWell;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

public class SessionRequestHandler implements RequestHandler<Session> {
    private final Data data;
    private final InputAgent inputAgent;
    private final OutputAgent outputAgent;

    public SessionRequestHandler(
            Data data,
            InputAgent inputAgent,
            OutputAgent outputAgent) {
        this.data = data;
        this.inputAgent = inputAgent;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<Session> getRequestClass() {
        return Session.class;
    }

    @Override
    public void handleRequest(Session request) {
        outputAgent.addResponse(handleSession(request));
    }

    private Object handleSession(Session request) {
        AES aes = new AES(request.key());
        inputAgent.setAes(aes);
        outputAgent.setAes(aes);
        inputAgent.setSessionCreated(true);
        return new AllIsWell();
    }
}
