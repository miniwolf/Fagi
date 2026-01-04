package com.fagi.http.server.util.converter;

import com.fagi.http.annotation.param.BodyParam;
import com.fagi.http.annotation.param.HeaderParam;
import com.fagi.http.annotation.param.PathParam;
import com.fagi.http.annotation.param.QueryParam;
import com.fagi.http.server.exception.RequestParameterConversionException;
import com.fagi.http.server.model.RestMethod;
import com.fagi.http.server.model.RestMethodMatch;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: zargess - Clean this mess

/**
 * @author Marcus Haagh
 */
public class ParameterConverter {
    public static Object[] convertParameters(
            RestMethodMatch restMethodMatch,
            Map<String, String> queryParams,
            Map<String, List<String>> headers,
            String body) throws RequestParameterConversionException {
        var parameterValues = new ArrayList<>();

        Parameter[] endpointParameters = restMethodMatch
                .restMethod()
                .method()
                .getParameters();

        for (Parameter param : endpointParameters) {
            parameterValues.add(convertParameter(
                    param,
                    restMethodMatch.restMethod(),
                    restMethodMatch.pathParams(),
                    queryParams,
                    headers,
                    body
            ));
        }

        return parameterValues.toArray();
    }

    private static Object convertParameter(
            Parameter param,
            RestMethod restMethod,
            Map<String, String> pathParams,
            Map<String, String> queryParams,
            Map<String, List<String>> headers,
            String body) throws RequestParameterConversionException {

        if (param.isAnnotationPresent(PathParam.class)) {
            return convertPathParam(
                    param,
                    pathParams
            );
        }
        if (param.isAnnotationPresent(QueryParam.class)) {
            return convertQueryParam(
                    param,
                    queryParams
            );
        }
        if (param.isAnnotationPresent(HeaderParam.class)) {
            return convertHeaderParam(
                    param,
                    headers
            );
        }
        if (param.isAnnotationPresent(BodyParam.class)) {
            return switch (restMethod.consumesMimeType()) {
                case TEXT -> convertStringToType(
                        body,
                        param.getType()
                );
                case JSON -> deserializeBodyParam(
                        param,
                        body
                );
            };
        }
        return null; // or throw if parameters without annotations aren't allowed
    }

    private static Object convertPathParam(
            Parameter param,
            Map<String, String> pathParams) throws RequestParameterConversionException {
        String paramName = param
                .getAnnotation(PathParam.class)
                .value();
        return convertStringToType(
                pathParams.get(paramName),
                param.getType()
        );
    }

    private static Object convertQueryParam(
            Parameter param,
            Map<String, String> queryParams) throws RequestParameterConversionException {
        String paramName = param
                .getAnnotation(QueryParam.class)
                .value();
        return convertStringToType(
                queryParams.get(paramName),
                param.getType()
        );
    }

    private static Object convertHeaderParam(
            Parameter param,
            Map<String, List<String>> headers) throws RequestParameterConversionException {
        String headerName = param
                .getAnnotation(HeaderParam.class)
                .value();
        String headerValue = headers
                .get(headerName)
                .getFirst();
        return convertStringToType(
                headerValue,
                param.getType()
        );
    }

    private static Object deserializeBodyParam(
            Parameter param,
            String bodyJson) throws RequestParameterConversionException {
        try {
            return new Gson().fromJson(
                    bodyJson,
                    param.getType()
            );
        } catch (JsonSyntaxException e) {
            throw new RequestParameterConversionException(
                    param.getType(),
                    e
            );
        }
    }

    private static <T> Object convertStringToType(
            String value,
            Class<T> type) throws RequestParameterConversionException {
        if (value == null) {
            return null;
        }

        if (type == String.class) {
            return value;
        }
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }
        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }
        if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        }
        if (type == short.class || type == Short.class) {
            return Short.parseShort(value);
        }
        if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(value);
        }
        if (type == char.class || type == Character.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException("Expected a single character, got: " + value);
            }
            return value.charAt(0);
        }
        if (type.isEnum()) {
            return convertStringToEnum(
                    value,
                    type
            );
        }

        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }

    private static <T> T convertStringToEnum(
            String value,
            Class<T> type) throws RequestParameterConversionException {
        try {
            return type.cast(Enum.valueOf(
                    (Class<? extends Enum>) type,
                    value
            ));
        } catch (IllegalArgumentException e) {
            throw new RequestParameterConversionException(
                    type,
                    e
            );
        }
    }
}
