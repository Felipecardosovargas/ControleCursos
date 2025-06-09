package com.escola.controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * A functional interface for handling HTTP exchanges within a custom HTTP server.
 * <p>
 * Implementations of this interface define the logic to process an incoming
 * {@link HttpExchange} and utilize a {@link Matcher} object, typically for
 * extracting path variables or query parameters from a URL pattern.
 * </p>
 * <p>
 * This interface is marked as a {@code @FunctionalInterface}, meaning it can be
 * implemented using a lambda expression or method reference, promoting concise
 * and readable request handling logic.
 * </p>
 *
 * @version 1.1
 * @author FelipeCardoso
 */
@FunctionalInterface
public interface HttpExchangeHandler {

    /**
     * Handles an incoming HTTP exchange.
     * <p>
     * This method is responsible for processing the HTTP request, generating a response,
     * and sending it back to the client. It also provides a {@link Matcher} object
     * that contains the results of matching the request URI against a predefined
     * regular expression, useful for extracting dynamic parts of the URL.
     * </p>
     *
     * @param exchange The {@link HttpExchange} object representing the incoming HTTP request and outgoing response.
     * It provides access to request headers, body, and methods for sending the response.
     * @param matcher  A {@link Matcher} object containing the result of the regular expression
     * match against the request URI. Groups within the matcher can be used to
     * extract path variables.
     * @throws IOException If an I/O error occurs while reading the request or writing the response.
     */
    void handle(HttpExchange exchange, Matcher matcher) throws IOException;
}