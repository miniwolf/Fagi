package com.fagi.http.server.validation;

import com.fagi.http.HttpMethodType;
import com.fagi.http.MimeType;
import com.fagi.http.annotation.Consumes;
import com.fagi.http.annotation.HttpMethod;
import com.fagi.http.annotation.Path;
import com.fagi.http.annotation.Produces;
import com.fagi.http.annotation.param.BodyParam;
import com.fagi.http.annotation.param.HeaderParam;
import com.fagi.http.annotation.param.PathParam;
import com.fagi.http.annotation.param.QueryParam;
import com.fagi.http.server.exception.RestApiPathDuplicatedException;
import com.fagi.http.server.handler.RestApiHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: zargess - javadoc

/**
 * @author Marcus Haagh
 */
public class RestApiValidator {
    private static final Set<Class<?>> SIMPLE_TYPES = Set.of(
            String.class,
            int.class,
            Integer.class,
            boolean.class,
            Boolean.class,
            long.class,
            Long.class,
            double.class,
            Double.class,
            float.class,
            Float.class,
            short.class,
            Short.class,
            byte.class,
            Byte.class,
            char.class,
            Character.class
    );

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException if called via reflection
     */
    private RestApiValidator() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    /**
     * <p>Validates a list of rest api handlers to verify that paths are not duplicated.</p>
     *
     * @param handlers a set of {@link RestApiHandler}s to be validated
     * @throws RestApiPathDuplicatedException when the api contract of handlers duplicate the path of another
     */
    public static void validateNoDuplicatePaths(Set<RestApiHandler<?, ?>> handlers) {
        List<String> paths = handlers
                .stream()
                .map(handler -> handler
                        .getApiContract()
                        .getAnnotation(Path.class)
                        .value())
                .toList();

        Set<String> duplicates = findDuplicates(paths);
        if (!duplicates.isEmpty()) {
            throw new RestApiPathDuplicatedException(duplicates);
        }
    }

    /**
     * <p>Validates a rest api interface. This should be called early in the creation of rest api endpoints to make sure the interface is a valid rest api.</p>
     * <p>This finds as many errors as possible.</p>
     *
     * @param apiInterfaceClass the interface to be validated
     * @return a list of validation errors.
     */
    public static List<RestEndpointValidationError> validateRestApiClass(Class<?> apiInterfaceClass) {
        var errors = new ArrayList<RestEndpointValidationError>();

        if (!apiInterfaceClass.isAnnotationPresent(Path.class)) {
            errors.add(RestEndpointValidationError.PATH_MISSING_ON_API_CLASS);
        }

        return errors;
    }

    /**
     * <p>Validates a REST method. This should be called early in the creation of rest api endpoints to make sure the interface is a valid rest api.</p>
     * <p>This finds as many errors as possible.</p>
     *
     * @param method the method to be validated
     * @return a list of validation errors.
     */
    public static List<RestEndpointValidationError> validateRestEndpoint(
            Method method) {
        var errors = new ArrayList<RestEndpointValidationError>();

        if (method.isAnnotationPresent(Path.class)) {
            errors.addAll(validatePathAndPathParams(method));
        } else {
            errors.add(RestEndpointValidationError.PATH_MISSING_ON_METHOD);
        }

        errors.addAll(validateQueryParams(method));

        errors.addAll(validateHeaderParams(method));

        if (!method.isAnnotationPresent(HttpMethod.class)) {
            errors.add(RestEndpointValidationError.HTTP_METHOD_ANNOTATION_MISSING);
            return errors;
        }

        HttpMethodType httpMethodType = method
                .getAnnotation(HttpMethod.class)
                .value();

        switch (httpMethodType) {
            case GET -> errors.addAll(validateGetMethod(method));
            case POST -> errors.addAll(validatePostMethod(method));
            case PUT -> errors.addAll(validatePutMethod(method));
            case DELETE -> errors.addAll(validateDeleteMethod(method));
        }

        return errors;
    }

    /**
     * Validate GET methods
     * <ul>
     *     <li>Produces result</li>
     *     <li>Does not have body parameter</li>
     * </ul>
     */
    private static List<RestEndpointValidationError> validateGetMethod(Method method) {
        var errors = new ArrayList<>(validateRequiredProduces(method));

        if (!findBodyParameters(method).isEmpty()) {
            errors.add(RestEndpointValidationError.BODY_PARAM_IN_HTTP_GET_ENDPOINT);
        }

        return errors;
    }

    /**
     * Validate POST methods
     * <ul>
     *     <li>Produces result</li>
     *     <li>Consumes exactly one Body parameter</li>
     * </ul>
     */
    private static List<RestEndpointValidationError> validatePostMethod(Method method) {
        var errors = new ArrayList<>(validateRequiredProduces(method));

        validateBodyAndConsumes(method).ifPresent(errors::add);

        return errors;
    }

    /**
     * Validate PUT method
     * <ul>
     *     <li>May produce result</li>
     *     <li>Consumes exactly one Body parameter</li>
     * </ul>
     */
    private static List<RestEndpointValidationError> validatePutMethod(Method method) {
        var errors = new ArrayList<>(validateOptionalProduces(method));

        validateBodyAndConsumes(method).ifPresent(errors::add);

        return errors;
    }

    /**
     * Validates DELETE method
     * <ul>
     *     <li>May produce result</li>
     *     <li>May consume exactly one Body parameter</li>
     * </ul>
     */
    private static List<RestEndpointValidationError> validateDeleteMethod(Method method) {
        var errors = new ArrayList<>(validateOptionalProduces(method));

        if (method.isAnnotationPresent(Consumes.class) || !findBodyParameters(method).isEmpty()) {
            validateBodyAndConsumes(method).ifPresent(errors::add);
        }

        return errors;
    }

    private static ArrayList<RestEndpointValidationError> validateOptionalProduces(Method method) {
        var errors = new ArrayList<RestEndpointValidationError>();

        if (!method.isAnnotationPresent(Produces.class) && !isReturnTypeVoid(method)) {
            errors.add(RestEndpointValidationError.PRODUCES_ANNOTATION_MISSING);
        } else if (method.isAnnotationPresent(Produces.class) && isReturnTypeVoid(method)) {
            errors.add(RestEndpointValidationError.VOID_RETURN_TYPE_WHEN_PRODUCES_DEFINED);
        } else if (method.isAnnotationPresent(Produces.class)) {
            validateProduces(method).ifPresent(errors::add);
        }
        return errors;
    }

    private static List<RestEndpointValidationError> validatePathAndPathParams(Method method) {
        Path methodPath = method.getAnnotation(Path.class);
        var errors = new ArrayList<RestEndpointValidationError>();

        List<String> templateParams = extractTemplateParams(methodPath.value());

        List<String> pathParams = Arrays
                .stream(method.getParameters())
                .filter(param -> param.isAnnotationPresent(PathParam.class))
                .map(param -> param
                        .getAnnotation(PathParam.class)
                        .value())
                .toList();

        if (templateParams.size() != new HashSet<>(templateParams).size()) {
            errors.add(RestEndpointValidationError.PATH_PARAM_DUPLICATE_IN_TEMPLATE);
        }

        if (pathParams.size() != new HashSet<>(pathParams).size()) {
            errors.add(RestEndpointValidationError.PATH_PARAM_DUPLICATE_ANNOTATION);
        }

        for (String templateParam : templateParams) {
            if (!pathParams.contains(templateParam)) {
                errors.add(RestEndpointValidationError.PATH_PARAM_NOT_DEFINED);
            }
        }

        for (String pathParamValue : pathParams) {
            if (!templateParams.contains(pathParamValue)) {
                errors.add(RestEndpointValidationError.PATH_PARAM_NOT_IN_TEMPLATE);
            }
        }

        return errors;
    }

    private static List<RestEndpointValidationError> validateQueryParams(Method method) {
        var errors = new ArrayList<RestEndpointValidationError>();
        List<Parameter> queryParams = Arrays
                .stream(method.getParameters())
                .filter(param -> param.isAnnotationPresent(QueryParam.class))
                .toList();

        for (Parameter param : queryParams) {
            if (isNotSimpleType(param.getType())) {
                errors.add(RestEndpointValidationError.QUERY_PARAM_TYPE_NOT_ALLOWED);
            }
        }
        return errors;
    }

    private static List<RestEndpointValidationError> validateHeaderParams(Method method) {
        var errors = new ArrayList<RestEndpointValidationError>();
        List<Parameter> headerParams = Arrays
                .stream(method.getParameters())
                .filter(param -> param.isAnnotationPresent(HeaderParam.class))
                .toList();

        for (Parameter param : headerParams) {
            if (isNotSimpleType(param.getType())) {
                errors.add(RestEndpointValidationError.HEADER_PARAM_TYPE_NOT_ALLOWED);
            }
        }
        return errors;
    }

    private static Optional<RestEndpointValidationError> validateBodyAndConsumes(
            Method method) {
        List<Parameter> bodyParameters = findBodyParameters(method);

        if (bodyParameters.isEmpty()) {
            return Optional.of(RestEndpointValidationError.BODY_PARAM_MISSING);
        } else if (bodyParameters.size() > 1) {
            return Optional.of(RestEndpointValidationError.ONLY_ONE_BODY_PARAM_ALLOWED);
        } else if (!method.isAnnotationPresent(Consumes.class)) {
            return Optional.of(RestEndpointValidationError.CONSUMES_ANNOTATION_MISSING);
        } else {
            return validateConsumes(
                    method,
                    bodyParameters.getFirst()
            );
        }
    }

    private static ArrayList<RestEndpointValidationError> validateRequiredProduces(
            Method method) {
        var errors = new ArrayList<RestEndpointValidationError>();
        if (isReturnTypeVoid(method)) {
            errors.add(RestEndpointValidationError.VOID_RETURN_TYPE_NOT_ALLOWED);
        }

        if (!method.isAnnotationPresent(Produces.class)) {
            errors.add(RestEndpointValidationError.PRODUCES_ANNOTATION_MISSING);
        } else {
            validateProduces(method).ifPresent(errors::add);
        }
        return errors;
    }

    private static Optional<RestEndpointValidationError> validateProduces(Method method) {
        MimeType producesMimeType = method
                .getAnnotation(Produces.class)
                .value();

        if (MimeType.TEXT.equals(producesMimeType) && isNotSimpleType(method.getReturnType())) {
            return Optional.of(RestEndpointValidationError.PRODUCES_TEXT_ONLY_ALLOWS_STRING_OR_PRIMITIVE_RETURN_TYPE);
        }

        return Optional.empty();
    }

    private static Optional<RestEndpointValidationError> validateConsumes(
            Method method,
            Parameter bodyParameter) {
        MimeType consumesMimeType = method
                .getAnnotation(Consumes.class)
                .value();
        Class<?> bodyParameterType = bodyParameter.getType();

        if (MimeType.TEXT.equals(consumesMimeType) && isNotSimpleType(bodyParameterType)) {
            return Optional.of(RestEndpointValidationError.CONSUMES_TEXT_ONLY_ALLOWS_STRING_OR_PRIMITIVE_TYPE);
        }

        return Optional.empty();
    }

    private static boolean isReturnTypeVoid(Method method) {
        return method.getReturnType() == Void.class || method.getReturnType() == void.class;
    }

    private static boolean isNotSimpleType(Class<?> type) {
        return !SIMPLE_TYPES.contains(type) && !type.isEnum();
    }

    // Helper method to extract template parameters (including duplicates)
    private static List<String> extractTemplateParams(String pathTemplate) {
        List<String> params = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = pattern.matcher(pathTemplate);
        while (matcher.find()) {
            params.add(matcher.group(1));
        }
        return params;
    }

    private static List<Parameter> findBodyParameters(Method method) {
        return Arrays
                .stream(method.getParameters())
                .filter(param -> param.isAnnotationPresent(BodyParam.class))
                .toList();
    }

    private static <T> Set<T> findDuplicates(List<T> list) {
        Set<T> duplicates = new HashSet<>();
        Set<T> seen = new HashSet<>();
        for (T item : list) {
            if (!seen.add(item)) {
                duplicates.add(item);
            }
        }
        return duplicates;
    }
}
