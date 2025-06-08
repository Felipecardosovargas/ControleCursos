package com.escola.controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.regex.Matcher;

@FunctionalInterface
public interface HttpExchangeHandler {
    void handle(HttpExchange exchange, Matcher matcher) throws IOException;
}