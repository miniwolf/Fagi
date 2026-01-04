package com.fagi.http.server.handler;

import com.fagi.http.HttpCode;
import com.fagi.http.HttpMethodType;
import com.fagi.http.MimeType;
import com.fagi.http.server.exception.InvalidApiException;
import com.fagi.http.server.exception.RequestParameterConversionException;
import com.fagi.http.server.factory.RestApiFactory;
import com.fagi.http.server.model.RestMethod;
import com.fagi.http.server.model.RestMethodMatch;
import com.fagi.http.server.util.converter.ParameterConverter;
import com.fagi.http.server.util.match.RestMethodMatcher;
import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>This class handles all requests that target the given {@link RestApiHandler#apiContract}.</p>
 * <p>The {@link RestApiHandler#apiContract} is validated in the constructor to make sure REST API is valid and can be used to handle requests.</p>
 * <p>The class should be agnostic about the underlying HTTP server framework used, to allow painless change of framework.</p>
 *
 * @param <RESTAPI> the interface of the API the handler should manage
 * @param <SERVICE> the service that implements the API
 * @author Marcus Haagh
 */
public class RestApiHandler<RESTAPI, SERVICE extends RESTAPI> {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(RestApiHandler.class);
    private static final Gson GSON = new Gson();
    private final Class<RESTAPI> apiContract;
    private final SERVICE apiService;
    private final List<RestMethod> restMethods;
    private final FilterChain filterChain;

    /**
     * <p>Constructor for the RestApiHandler.</p>
     * <p>Performs validation of the given {@link RestApiHandler#apiContract} to make sure it is a valid REST API.</p>
     *
     * @param apiContract the contract of the REST API. Is validated to make sure it is valid.
     * @param apiService  a stateless instance of a class that implements the {@link RestApiHandler#apiContract}
     * @param filterChain a filter chain to perform filtering before the invocation of the rest method and after to post process the response body
     * @throws InvalidApiException when {@link RestApiHandler#apiContract} is not valid. Contains a list of validation errors.
     */
    public RestApiHandler(
            Class<RESTAPI> apiContract,
            SERVICE apiService,
            FilterChain filterChain) {
        this.apiContract = apiContract;
        this.apiService = apiService;
        this.restMethods = RestApiFactory.createRestEndpointsFromInterface(apiContract);
        this.filterChain = filterChain;
    }

    public Class<RESTAPI> getApiContract() {
        return apiContract;
    }

    /**
     * <p>Handles the requests the server receives from clients.</p>
     * <p>Uses reflection to attempt to find a method in the REST API that matches the request, prioritizing the method that matches the most path, query and header parameters</p>
     *
     * @param request         a generalized representation of the request to support multiple HTTP server frameworks.
     * @param responseHandler an object to facilitate the sending of a response to the client.
     * @throws IOException when failing to send response to the client.
     */
    public void handle(
            HttpRequest request,
            HttpResponseHandler responseHandler) throws IOException {
        var requestStartTime = System.currentTimeMillis();
        try {
            String path = request.getPath();
            Map<String, String> queryParams = request.getQueryParams();
            HttpMethodType requestHttpMethod = request.getMethod();

            var beforeFindingCandidates = System.currentTimeMillis();

            var methodCandidates = RestMethodMatcher.findCandidates(
                    requestHttpMethod,
                    path,
                    restMethods
            );

            LOGGER.debug(() -> String.format(
                    "Request %s - Found %s request method candidates in %d ms",
                    request.getRequestId(),
                    methodCandidates.size(),
                    System.currentTimeMillis() - beforeFindingCandidates
            ));

            if (methodCandidates.isEmpty()) {
                LOGGER.debug(() -> String.format(
                        "Request %s - No method matched path: %s",
                        request.getRequestId(),
                        path
                ));
                LOGGER.info(() -> String.format(
                        "Request %s - Request could not be matched to any method",
                        request.getRequestId()
                ));
                sendResponse(
                        request,
                        responseHandler,
                        HttpCode.NOT_FOUND
                );
                return;
            }

            var matchFoundTime = System.currentTimeMillis();

            var match = RestMethodMatcher.findMatching(
                    methodCandidates,
                    queryParams,
                    request.getHeaders(),
                    request.hasBody()
            );

            if (match == null) {
                LOGGER.info(() -> String.format(
                        "Request %s - Request did not contain the required parameters",
                        request.getRequestId()
                ));
                sendResponse(
                        request,
                        responseHandler,
                        HttpCode.BAD_REQUEST
                );
                return;
            }

            LOGGER.debug(() -> String.format(
                    "Request %s - Request method %s#%s(%s) match found in %d ms",
                    request.getRequestId(),
                    apiContract.getSimpleName(),
                    match
                            .restMethod()
                            .method()
                            .getName(),
                    Arrays.toString(match
                                            .restMethod()
                                            .method()
                                            .getParameterTypes()),
                    System.currentTimeMillis() - matchFoundTime
            ));

            var timeBeforeFilters = System.currentTimeMillis();
            if (!filterChain.doFilterRequest(
                    request,
                    responseHandler,
                    match.restMethod()
            )) {
                sendResponse(
                        request,
                        responseHandler,
                        HttpCode.UNAUTHORIZED
                );
                return; // Rejected by a filter
            }
            LOGGER.debug(() -> String.format(
                    "Request %s - Filter chain execution took %d ms",
                    request.getRequestId(),
                    System.currentTimeMillis() - timeBeforeFilters
            ));

            Object responseValue = invokeRestEndpoint(
                    request,
                    match,
                    queryParams
            );

            sendResult(
                    request,
                    responseHandler,
                    match,
                    responseValue
            );
        } catch (RequestParameterConversionException e) {
            LOGGER.info(
                    e,
                    () -> String.format(
                            "Request %s - Failed to convert parameter of request.",
                            request.getRequestId()
                    )
            );
            sendResponse(
                    request,
                    responseHandler,
                    HttpCode.BAD_REQUEST
            );
        } catch (Exception e) {
            LOGGER.error(
                    e,
                    () -> String.format(
                            "Request %s - Server failed to handle request",
                            request.getRequestId()
                    )
            );
            sendResponse(
                    request,
                    responseHandler,
                    HttpCode.INTERNAL_SERVER_ERROR
            );
        } finally {
            LOGGER.debug(() -> String.format(
                    "Request %s - Processing request took %d ms",
                    request.getRequestId(),
                    System.currentTimeMillis() - requestStartTime
            ));
        }
    }

    private Object invokeRestEndpoint(
            HttpRequest request,
            RestMethodMatch methodMatch,
            Map<String, String> queryParams) throws Exception {
        var timeBeforeParameterConversion = System.currentTimeMillis();
        Object[] args = ParameterConverter.convertParameters(
                methodMatch,
                queryParams,
                request.getHeaders(),
                request.getBody()
        );
        LOGGER.debug(() -> String.format(
                "Request %s - Parameter conversion took %d ms",
                request.getRequestId(),
                System.currentTimeMillis() - timeBeforeParameterConversion
        ));

        var timeBeforeInvokingRestMethod = System.currentTimeMillis();

        var result = methodMatch
                .restMethod()
                .method()
                .invoke(
                        apiService,
                        args
                );

        LOGGER.debug(() -> String.format(
                "Request %s - Invocation of rest method %s#%s(%s) took %d ms",
                request.getRequestId(),
                apiContract.getSimpleName(),
                methodMatch
                        .restMethod()
                        .method()
                        .getName(),
                Arrays.toString(methodMatch
                                        .restMethod()
                                        .method()
                                        .getParameterTypes()),
                System.currentTimeMillis() - timeBeforeInvokingRestMethod
        ));

        return result;
    }

    private void sendResult(
            HttpRequest request,
            HttpResponseHandler responseHandler,
            RestMethodMatch methodMatch,
            Object responseValue) throws IOException {
        MimeType endpointProducesMimeType = methodMatch
                .restMethod()
                .producesMimeType();

        var httpCode = methodMatch
                .restMethod()
                .creates() ? HttpCode.CREATED : HttpCode.OK;

        if (endpointProducesMimeType != null) {
            var timeBeforeSerialization = System.currentTimeMillis();
            String response = switch (endpointProducesMimeType) {
                case JSON -> GSON.toJson(responseValue);
                case TEXT -> responseValue != null ? responseValue.toString() : "";
            };
            LOGGER.debug(() -> String.format(
                    "Request %s - Response serialization took %d ms",
                    request.getRequestId(),
                    System.currentTimeMillis() - timeBeforeSerialization
            ));
            sendResponse(
                    request,
                    responseHandler,
                    httpCode,
                    endpointProducesMimeType,
                    response
            );
        } else {
            sendResponse(
                    request,
                    responseHandler,
                    httpCode
            );
        }
    }

    private void sendResponse(
            HttpRequest request,
            HttpResponseHandler responseHandler,
            HttpCode httpCode) throws IOException {
        sendResponse(
                request,
                responseHandler,
                httpCode,
                MimeType.TEXT,
                httpCode.getResponseText()
        );
    }

    private void sendResponse(
            HttpRequest request,
            HttpResponseHandler responseHandler,
            HttpCode httpCode,
            MimeType mimeType,
            String body) throws IOException {
        responseHandler.addHeader(
                "Content-Type",
                mimeType != null ? mimeType.getValue() : MimeType.TEXT.getValue()
        );

        if (body != null && !body.isEmpty()) {
            responseHandler.setBody(body);

            byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

            responseHandler.addHeader(
                    "Content-Length",
                    Integer.toString(bodyBytes.length)
            );
        }

        var timeBeforeSend = System.currentTimeMillis();
        responseHandler.sendResponse(
                httpCode,
                mimeType
        );
        LOGGER.debug(() -> String.format(
                "Request %s - Sending response took %d ms",
                request.getRequestId(),
                System.currentTimeMillis() - timeBeforeSend
        ));
    }
}
