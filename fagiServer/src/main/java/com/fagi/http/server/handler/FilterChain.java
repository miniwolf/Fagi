package com.fagi.http.server.handler;

import com.fagi.http.server.model.RestMethod;

import java.util.List;

/**
 * This class facilitates running all filters on the request and the method found to match the request
 *
 * @author Marcus Haagh
 * @see RequestFilter
 */
public class FilterChain {
    private final List<RequestFilter> requestFilters;

    /**
     * <p>Constructor of the filter. The order of the filters in the given list is the order in which they are applied.</p>
     *
     * @param requestFilters the filters to use. Must be stateless.
     */
    public FilterChain(List<RequestFilter> requestFilters) {
        this.requestFilters = requestFilters;
    }

    /**
     * Run all filters on the request in the order they are registered in the list
     *
     * @param request         the request to be filtered
     * @param responseHandler the handler of the response
     * @param method          the rest method found to match the request
     * @return a boolean indicating whether the request is allowed to continue
     */
    public boolean doFilterRequest(
            HttpRequest request,
            HttpResponseHandler responseHandler,
            RestMethod method) {
        for (RequestFilter filter : requestFilters) {
            boolean accepted = filter.doFilterRequest(
                    request,
                    responseHandler,
                    method
            );
            if (!accepted) {
                return false;
            }
        }
        return true;
    }
}
