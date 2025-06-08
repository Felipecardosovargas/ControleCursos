package com.escola.controller;

import com.escola.dto.ApiResponse;
import com.escola.dto.RelatorioCursoDTO;
import com.escola.service.RelatorioService;
import com.escola.util.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HTTP Handler to manage requests related to reports.
 * This specific handler generates the course engagement report.
 * Implements {@link HttpHandler} to integrate with Java's native HTTP server.
 *
 * <p>Supported Endpoint:</p>
 * <ul>
 * <li><b>GET /api/relatorios/engajamento-cursos</b>: Generates and returns a course engagement report.</li>
 * </ul>
 *
 * @version 1.0
 * @author FelipeCardoso
 */
public class RelatorioController implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioController.class);

    private static final String APPLICATION_JSON = "application/json; charset=UTF-8";
    private final RelatorioService relatorioService;

    /**
     * Constructor for RelatorioController.
     *
     * @param relatorioService The service responsible for report generation logic.
     */
    public RelatorioController(RelatorioService relatorioService) {
        if (relatorioService == null) {
            logger.error("RelatorioService não pode ser nulo.");
            throw new IllegalArgumentException("RelatorioService deve ser fornecido.");
        }
        this.relatorioService = relatorioService;
        logger.info("RelatorioController inicializado.");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 1. Adds CORS headers (always first)
        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        logger.debug("Requisição recebida: {} {}", method, path);

        // 2. Handling OPTIONS request for CORS preflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            sendJsonResponse(exchange, 204, ""); // No Content
            return;
        }

        // 3. Request dispatching and centralized exception handling
        try {
            if ("GET".equalsIgnoreCase(method) && path.equals("/api/relatorios/engajamento-cursos")) {
                handleGetEngajamentoReport(exchange);
            } else {
                handleNotFoundOrMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            logger.error("Erro interno inesperado ao processar requisição de relatório {}: {}", path, e.getMessage(), e);
            sendJsonResponse(exchange, 500, JsonMapper.toJson(
                    ApiResponse.error("Erro interno no servidor ao gerar relatório. Por favor, tente novamente mais tarde."))
            );
        }
    }

    /**
     * Handles GET requests to /api/relatorios/engajamento-cursos to generate the engagement report.
     *
     * @param exchange The HttpExchange object.
     * @throws IOException If an I/O error occurs.
     */
    private void handleGetEngajamentoReport(HttpExchange exchange) throws IOException {
        logger.debug("Lidando com GET /api/relatorios/engajamento-cursos.");
        List<RelatorioCursoDTO> relatorio = relatorioService.gerarRelatorioEngajamentoCursos();
        sendJsonResponse(exchange, 200, JsonMapper.toJson(
                ApiResponse.success(relatorio, "Relatório de engajamento de cursos gerado com sucesso."))
        );
    }

    /**
     * Handles requests to endpoints not found or methods not allowed for this controller.
     *
     * @param exchange The HttpExchange object.
     * @throws IOException If an I/O error occurs.
     */
    private void handleNotFoundOrMethodNotAllowed(HttpExchange exchange) throws IOException {
        logger.warn("Endpoint de relatório não encontrado ou método não permitido: {} {}",
                exchange.getRequestMethod(), exchange.getRequestURI().getPath()
        );
        sendJsonResponse(exchange, 404, JsonMapper.toJson(
                ApiResponse.error("Recurso de relatório não encontrado ou Método Não Permitido."))
        );
    }

    // --- Helper Methods ---

    /**
     * Adds CORS headers to the response.
     *
     * @param exchange The HttpExchange object.
     */
    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS"); // Only GET allowed for reports
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    /**
     * Sends the JSON response to the client.
     *
     * @param exchange     The HttpExchange object.
     * @param statusCode   The HTTP status code.
     * @param responseBody The JSON response body.
     * @throws IOException If an I/O error occurs.
     */
    private void sendJsonResponse(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", APPLICATION_JSON);
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
            os.flush(); // Ensures data is sent immediately
        } finally {
            exchange.close(); // Ensures the exchange is closed after sending the response
        }
        logger.debug("Resposta enviada para {}: Status {}", exchange.getRequestURI().getPath(), statusCode);
    }

    public HttpHandler engajamentoHandler() {
        return null;
    }
}