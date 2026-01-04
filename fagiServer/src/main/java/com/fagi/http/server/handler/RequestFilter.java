package com.fagi.http.server.handler;

import com.fagi.http.server.model.RestMethod;

/**
 * <p>Filters to be applied on requests.</p>
 * <p>These should be stateless to allow them to be applied to all requests without creating new instances.</p>
 *
 * @author Marcus Haagh
 * @see FilterChain
 */
public interface RequestFilter {
    /**
     * <p>Filters the incoming request.</p>
     * <p>Allows for preprocessing of the request, such as verifying the request is allowed by the user.</p>
     *
     * @param request         the request to be filtered
     * @param responseHandler the handler of the response
     * @param method          the rest method found to match the request
     * @return a boolean indicating whether the request is allowed to continue
     */
    boolean doFilterRequest(
            HttpRequest request,
            HttpResponseHandler responseHandler,
            RestMethod method);
}
