package com.fagi.http.server.handler.filter;

import com.fagi.http.annotation.NoAuth;
import com.fagi.http.server.handler.FilterChain;
import com.fagi.http.server.handler.HttpRequest;
import com.fagi.http.server.handler.HttpResponseHandler;
import com.fagi.http.server.handler.RequestFilter;
import com.fagi.http.server.model.RestMethod;

public class AuthRequestFilter implements RequestFilter {
    @Override
    public boolean doFilterRequest(
            HttpRequest request,
            HttpResponseHandler responseHandler,
            RestMethod restMethod) {
        if (restMethod.method().isAnnotationPresent(NoAuth.class)) {
            return true;
        }
        // Do some check of authentication
        return true;
    }
}
