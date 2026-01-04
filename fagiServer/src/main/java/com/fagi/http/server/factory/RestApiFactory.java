package com.fagi.http.server.factory;

import com.fagi.http.MimeType;
import com.fagi.http.annotation.Consumes;
import com.fagi.http.annotation.Creates;
import com.fagi.http.annotation.HttpMethod;
import com.fagi.http.annotation.Path;
import com.fagi.http.annotation.Produces;
import com.fagi.http.annotation.param.BodyParam;
import com.fagi.http.annotation.param.HeaderParam;
import com.fagi.http.annotation.param.PathParam;
import com.fagi.http.annotation.param.QueryParam;
import com.fagi.http.server.exception.InvalidApiException;
import com.fagi.http.server.model.RestMethod;
import com.fagi.http.server.validation.RestEndpointValidationError;
import com.fagi.http.server.validation.RestApiValidator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

// TODO: zargess - javadoc

/**
 * @author Marcus Haagh
 */
public class RestApiFactory {
    private RestApiFactory() {
        throw new UnsupportedOperationException("Factory class - do not instantiate");
    }

    public static <API> List<RestMethod> createRestEndpointsFromInterface(Class<API> apiInterface) {
        List<RestEndpointValidationError> validationErrors = RestApiValidator.validateRestApiClass(apiInterface);

        if (!validationErrors.isEmpty()) {
            throw new InvalidApiException(
                    apiInterface,
                    validationErrors
            );
        }

        String apiRootPath = apiInterface
                .getAnnotation(Path.class)
                .value();

        return Arrays
                .stream(apiInterface.getMethods())
                .map(method -> createRestEndpointFromMethod(
                        apiInterface,
                        method,
                        apiRootPath
                ))
                .toList();
    }

    private static <API> RestMethod createRestEndpointFromMethod(
            Class<API> apiInterface,
            Method method,
            String apiRootPath) {

        List<RestEndpointValidationError> validationErrors = RestApiValidator.validateRestEndpoint(method);

        if (!validationErrors.isEmpty()) {
            throw new InvalidApiException(
                    apiInterface,
                    method,
                    validationErrors
            );
        }

        var pathParamTypes = new HashMap<String, Class<?>>();
        var requiredQueryParams = new HashSet<String>();
        var requiredHeaderParams = new HashSet<String>();
        boolean requiresBodyParam = false;

        for (Parameter param : method.getParameters()) {
            if (param.isAnnotationPresent(PathParam.class)) {
                PathParam pp = param.getAnnotation(PathParam.class);
                pathParamTypes.put(
                        pp.value(),
                        param.getType()
                );
            } else if (param.isAnnotationPresent(QueryParam.class)) {
                QueryParam qp = param.getAnnotation(QueryParam.class);
                requiredQueryParams.add(qp.value());
            } else if (param.isAnnotationPresent(HeaderParam.class)) {
                HeaderParam hp = param.getAnnotation(HeaderParam.class);
                requiredHeaderParams.add(hp.value());
            } else if (param.isAnnotationPresent(BodyParam.class)) {
                requiresBodyParam = true;
            }
        }

        String pathTemplate = apiRootPath + method
                .getAnnotation(Path.class)
                .value();
        boolean isPathStatic = !pathTemplate.contains("{");

        var httpMethodType = method
                .getAnnotation(HttpMethod.class)
                .value();

        var consumes = method.getAnnotation(Consumes.class);
        MimeType consumesMimeType = consumes != null ? consumes.value() : null;

        var produces = method.getAnnotation(Produces.class);
        MimeType producesMimeType = produces != null ? produces.value() : null;

        return new RestMethod(
                pathTemplate,
                method,
                pathParamTypes,
                requiredQueryParams,
                requiredHeaderParams,
                requiresBodyParam,
                isPathStatic,
                method.isAnnotationPresent(Creates.class),
                httpMethodType,
                consumesMimeType,
                producesMimeType
        );
    }
}
