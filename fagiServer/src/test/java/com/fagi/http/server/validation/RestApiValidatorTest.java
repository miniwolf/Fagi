package com.fagi.http.server.validation;

import com.fagi.BaseFagiTest;
import com.fagi.http.server.exception.RestApiPathDuplicatedException;
import com.fagi.http.server.handler.FilterChain;
import com.fagi.http.server.handler.RestApiHandler;
import com.fagi.http.server.validation.test.api.invalid.InvalidRestApiInterface;
import com.fagi.http.server.validation.test.api.valid.DummyEmptyRestApi;
import com.fagi.http.server.validation.test.api.valid.DummyRestApiService;
import com.fagi.http.server.validation.test.api.valid.EmptyRestApi;
import com.fagi.http.server.validation.test.api.valid.EmptyRestApiService;
import com.fagi.http.server.validation.test.api.valid.ValidRestApi;
import com.fagi.http.server.validation.test.api.valid.ValidRestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class RestApiValidatorTest extends BaseFagiTest {
    @Test
    void testUnsupportedOperationExceptionThrownWhenNewingClass() {
        var constructors = RestApiValidator.class.getDeclaredConstructors();

        Assertions.assertEquals(
                1,
                constructors.length
        );

        var constructor = constructors[0];

        constructor.setAccessible(true);

        InvocationTargetException invocationTargetException = Assertions.assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        Assertions.assertInstanceOf(
                UnsupportedOperationException.class,
                invocationTargetException.getCause()
        );

        Assertions.assertEquals(
                "Utility class - do not instantiate",
                invocationTargetException
                        .getCause()
                        .getMessage()
        );
    }

    @Test
    void testRestHandlersCanNotDuplicatePaths() {
        var handler1 = new RestApiHandler<>(
                EmptyRestApi.class,
                new EmptyRestApiService(),
                new FilterChain(Collections.emptyList())
        );
        var handler2 = new RestApiHandler<>(
                DummyEmptyRestApi.class,
                new DummyRestApiService(),
                new FilterChain(Collections.emptyList())
        );

        Assertions.assertThrows(
                RestApiPathDuplicatedException.class,
                () -> RestApiValidator.validateNoDuplicatePaths(Set.of(
                        handler1,
                        handler2
                ))
        );
    }

    @Test
    void testRestHandlersCanAcceptHandlersWithDifferentPaths() {
        var handler1 = new RestApiHandler<>(
                ValidRestApi.class,
                new ValidRestService(),
                new FilterChain(Collections.emptyList())
        );
        var handler2 = new RestApiHandler<>(
                DummyEmptyRestApi.class,
                new DummyRestApiService(),
                new FilterChain(Collections.emptyList())
        );

        Assertions.assertDoesNotThrow(() -> RestApiValidator.validateNoDuplicatePaths(Set.of(
                handler1,
                handler2
        )));
    }

    @Test
    void testClassMissingPathAnnotation() {
        var errors = RestApiValidator.validateRestApiClass(InvalidRestApiInterface.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PATH_MISSING_ON_API_CLASS,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testMethodMissingPath() throws Exception {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod("missingPath"));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PATH_MISSING_ON_METHOD,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testMethodMissingHttpMethod() throws Exception {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod("missingHttpMethod"));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.HTTP_METHOD_ANNOTATION_MISSING,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testGetMethodReturnVoid() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod("getWithVoidReturn"));

        Assertions.assertAll(
                () -> Assertions.assertFalse(errors.isEmpty()),
                () -> Assertions.assertTrue(errors.contains(RestEndpointValidationError.VOID_RETURN_TYPE_NOT_ALLOWED))
        );
    }

    @Test
    void testGetWithMissingProduces() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod("getMissingProduces"));

        Assertions.assertAll(
                () -> Assertions.assertFalse(errors.isEmpty()),
                () -> Assertions.assertTrue(errors.contains(RestEndpointValidationError.PRODUCES_ANNOTATION_MISSING))
        );
    }

    @Test
    void testGetWithWrongReturnType() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod("getWithWrongReturnType"));

        Assertions.assertAll(
                () -> Assertions.assertFalse(errors.isEmpty()),
                () -> Assertions.assertTrue(errors.contains(RestEndpointValidationError.PRODUCES_TEXT_ONLY_ALLOWS_STRING_OR_PRIMITIVE_RETURN_TYPE))
        );
    }

    @Test
    void testGetWithBodyParameter() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "getWithBody",
                Object.class,
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertFalse(errors.isEmpty()),
                () -> Assertions.assertTrue(errors.contains(RestEndpointValidationError.BODY_PARAM_IN_HTTP_GET_ENDPOINT))
        );
    }

    @Test
    void testPostWithNoProduces() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "postWithNoProduces",
                Object.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PRODUCES_ANNOTATION_MISSING,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testPostWithNoBodyParam() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "postWithNoBodyParam",
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.BODY_PARAM_MISSING,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testPostWithMultipleBodyParams() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "postWithTwoBodyParams",
                Object.class,
                Object.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.ONLY_ONE_BODY_PARAM_ALLOWED,
                        errors.getFirst()
                )
        );
    }


    @Test
    void testPostWithNoConsumes() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "postWithNoConsumes",
                Object.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.CONSUMES_ANNOTATION_MISSING,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testPostWithReturnTypeNotMatchingProducesMimeType() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "postWithMismatchInputType",
                List.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.CONSUMES_TEXT_ONLY_ALLOWS_STRING_OR_PRIMITIVE_TYPE,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testPutMissingProducesWhenReturnTypeIsNotVoid() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "putMissingProducesWhenReturnIsVoid",
                Object.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PRODUCES_ANNOTATION_MISSING,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testPutWithMismatchReturnType() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "putWithMismatchReturnType",
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PRODUCES_TEXT_ONLY_ALLOWS_STRING_OR_PRIMITIVE_RETURN_TYPE,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testPutWithProducesWhenVoidReturnType() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "putWithProducesWhenVoidReturnType",
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.VOID_RETURN_TYPE_WHEN_PRODUCES_DEFINED,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testPutOnlyAcceptOneBodyParam() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "putWithMultipleBodyParams",
                String.class,
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.ONLY_ONE_BODY_PARAM_ALLOWED,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testPutMissingBody() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "putWithMissingBody",
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.BODY_PARAM_MISSING,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testPutMissingConsumesAnnotation() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "putMissingConsumes",
                Object.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.CONSUMES_ANNOTATION_MISSING,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testPutMismatchBodyTypeWithMimeType() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "putMismatchBodyType",
                List.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.CONSUMES_TEXT_ONLY_ALLOWS_STRING_OR_PRIMITIVE_TYPE,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testDeleteWithMissingProduces() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod("deleteWithMissingProduces"));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PRODUCES_ANNOTATION_MISSING,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testDeleteWithProducesWhenVoidReturnType() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod("deleteWithProducesWhenVoidReturnType"));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.VOID_RETURN_TYPE_WHEN_PRODUCES_DEFINED,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testDeleteWithMultipleBodyParams() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "deleteWithMultipleBodyParams",
                String.class,
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.ONLY_ONE_BODY_PARAM_ALLOWED,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testDeleteWithMissingBody() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "deleteWithMissingBody",
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.BODY_PARAM_MISSING,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testDeleteMissingConsumes() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "deleteWithMissingConsumes",
                Object.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.CONSUMES_ANNOTATION_MISSING,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testDeleteWithMismatchReturnType() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod("deleteWithMismatchReturnType"));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PRODUCES_TEXT_ONLY_ALLOWS_STRING_OR_PRIMITIVE_RETURN_TYPE,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testDeleteWithMismatchBodyType() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "deleteMismatchBodyType",
                List.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.CONSUMES_TEXT_ONLY_ALLOWS_STRING_OR_PRIMITIVE_TYPE,
                        errors.getFirst()
                )
        );
    }

    // TODO: zargess - tests for header, query and path types
    @Test
    void testPathWithDuplicateTemplateParams() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "pathWithDuplicateTemplateParams",
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PATH_PARAM_DUPLICATE_IN_TEMPLATE,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testMethodWithDuplicatePathParams() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "methodWithDuplicatePathParams",
                String.class,
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PATH_PARAM_DUPLICATE_ANNOTATION,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testMethodWithParamInPathTemplateButNotDefinedPathParam() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "methodWithMissingPathParam",
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PATH_PARAM_NOT_DEFINED,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testMethodWithPathParamNotInPathTemplate() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "methodWithPathParamNotInPath",
                String.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.PATH_PARAM_NOT_IN_TEMPLATE,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testMethodWithQueryParamWithComplexType() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "methodWithQueryParamWithComplexType",
                List.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.QUERY_PARAM_TYPE_NOT_ALLOWED,
                        errors.getFirst()
                )
        );
    }

    @Test
    void testMethodWithHeaderParamWithComplexType() throws NoSuchMethodException {
        var errors = RestApiValidator.validateRestEndpoint(InvalidRestApiInterface.class.getMethod(
                "methodWithHeaderParamWithComplexType",
                List.class
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        errors.size()
                ),
                () -> Assertions.assertEquals(
                        RestEndpointValidationError.HEADER_PARAM_TYPE_NOT_ALLOWED,
                        errors.getFirst()
                )
        );
    }
}