package com.fagi.http.server.factory;

import com.fagi.BaseFagiTest;
import com.fagi.http.annotation.Path;
import com.fagi.http.server.exception.InvalidApiException;
import com.fagi.http.server.validation.test.api.invalid.InvalidRestApiInterface;
import com.fagi.http.server.validation.test.api.invalid.RestApiWithInvalidMethod;
import com.fagi.http.server.validation.test.api.valid.ValidRestApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

class RestApiFactoryTest extends BaseFagiTest {
    @Test
    void testUnsupportedOperationExceptionThrownWhenNewingClass() {
        var constructors = RestApiFactory.class.getDeclaredConstructors();

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
                "Factory class - do not instantiate",
                invocationTargetException
                        .getCause()
                        .getMessage()
        );
    }

    @Test
    void testPathOfAllRestEndpointsStartsWithRootPath() {
        var restEndpoints = RestApiFactory.createRestEndpointsFromInterface(ValidRestApi.class);

        for (var restEndpoint : restEndpoints) {
            Assertions.assertTrue(restEndpoint
                                          .pathTemplate()
                                          .startsWith(ValidRestApi.class
                                                              .getAnnotation(Path.class)
                                                              .value()));
        }
    }

    @Test
    void testRestEndpointHasAllRegisteredParameters() {
        var restEndpoints = RestApiFactory.createRestEndpointsFromInterface(ValidRestApi.class);

        var restEndpoint = restEndpoints
                .stream()
                .filter(endpoint -> endpoint
                        .method()
                        .getName()
                        .equals("getUserHistory"))
                .toList()
                .getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        2,
                        restEndpoint
                                .pathParamTypes()
                                .size()
                ),
                () -> Assertions.assertEquals(
                        3,
                        restEndpoint
                                .requiredQueryParams()
                                .size()
                ),
                () -> Assertions.assertEquals(
                        4,
                        restEndpoint
                                .requiredHeaderParams()
                                .size()
                )
        );
    }

    @Test
    void testRestInterfaceHasValidationErrors() {
        Assertions.assertThrowsExactly(
                InvalidApiException.class,
                () -> RestApiFactory.createRestEndpointsFromInterface(InvalidRestApiInterface.class)
        );
    }

    @Test
    void testRestInterfaceMethodHasValidationErrors() {
        Assertions.assertThrowsExactly(
                InvalidApiException.class,
                () -> RestApiFactory.createRestEndpointsFromInterface(RestApiWithInvalidMethod.class)
        );
    }

    @Test
    void testRestEndpointHasBody() {
        var restEndpoints = RestApiFactory.createRestEndpointsFromInterface(ValidRestApi.class);

        var restEndpoint = restEndpoints
                .stream()
                .filter(endpoint -> endpoint
                        .method()
                        .getName()
                        .equals("createUser"))
                .toList()
                .getFirst();

        Assertions.assertTrue(restEndpoint.isBodyRequired());
    }

    @Test
    void testRestEndpointDoesNotProduce() {
        var restEndpoints = RestApiFactory.createRestEndpointsFromInterface(ValidRestApi.class);

        var restEndpoint = restEndpoints
                .stream()
                .filter(endpoint -> endpoint
                        .method()
                        .getName()
                        .equals("updateUser"))
                .toList()
                .getFirst();

        Assertions.assertNull(restEndpoint.producesMimeType());
    }

    @Test
    void testRestEndpointPathIsStatic() {
        var restEndpoints = RestApiFactory.createRestEndpointsFromInterface(ValidRestApi.class);

        var restEndpoint = restEndpoints
                .stream()
                .filter(endpoint -> endpoint
                        .method()
                        .getName()
                        .equals("updateUser"))
                .toList()
                .getFirst();

        Assertions.assertTrue(restEndpoint.isPathStatic());
    }
}