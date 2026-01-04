package com.fagi.main;

import com.fagi.http.annotation.Path;
import com.fagi.http.rest.service.DummyRestRestService;
import com.fagi.http.server.handler.FilterChain;
import com.fagi.http.server.handler.RestApiHandler;
import com.fagi.http.server.handler.filter.AuthRequestFilter;
import com.fagi.http.server.handler.java.JavaHttpHandlerAdapter;
import com.fagi.http.server.validation.RestApiValidator;
import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;
import com.fagi.rest.api.DummyRestApi;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

// TODO: zargess - javadoc
public class RestServer {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(RestServer.class);

    // TODO: zargess - not main method.
    public static void main(String[] args) throws Exception {
        if (!FagiLoggerFactory.isCustomConfigurationAvailable()) {
            FagiLoggerFactory.setupDefaultConfiguration(java.nio.file.Path.of("server.log"));
            LOGGER.info(() -> "No log config file specified. Using default log config instead.");
        }

        int port = 8080;

        HttpServer server = HttpServer.create(
                new InetSocketAddress(port),
                0
        );

        // Add filters here. At minimum an Authentication filter should be added.
        var filterChain = new FilterChain(Collections.emptyList());

        Set<RestApiHandler<?, ?>> apis = new HashSet<>();
        apis.add(new RestApiHandler<>(
                DummyRestApi.class,
                new DummyRestRestService(),
                filterChain
        ));

        // To make sure that no handlers use the same path as this will cause issues when registering the contexts
        RestApiValidator.validateNoDuplicatePaths(apis);

        for (RestApiHandler<?, ?> handler : apis) {
            HttpContext context = server.createContext(handler
                                                               .getApiContract()
                                                               .getAnnotation(Path.class)
                                                               .value());
            context.setHandler(new JavaHttpHandlerAdapter<>(handler));
        }

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        LOGGER.info(() -> "Server started on port " + port);
    }
}

