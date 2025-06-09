package com.escola.controller;

import com.escola.dto.AlunoDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.ValidacaoException;
import com.escola.service.AlunoService;
import com.escola.util.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler; // <<< Importe HttpHandler do SDK
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP Handler for managing Aluno (student) related requests.
 * Implements {@link HttpHandler} to integrate with Java's built-in HTTP server.
 * <p>
 * This controller leverages a more structured approach for request handling,
 * dispatching requests to specific methods based on the HTTP method and URI path.
 * It focuses on separating concerns, improving readability, and making the code
 * more maintainable and testable.
 * </p>
 * <p>
 * Endpoints:
 * - POST /api/alunos: Create a new student. Expects JSON body (AlunoDTO).
 * - POST /api/alunos/lote: Create multiple students from a JSON array.
 * - GET /api/alunos: List all students.
 * - GET /api/alunos/{id}: Get a student by ID.
 * - GET /api/alunos/email/{email}: Get a student by email.
 * - PUT /api/alunos/{id}: Update a student. Expects JSON body (AlunoDTO).
 * - DELETE /api/alunos/{id}: Delete a student.
 * </p>
 *
 * @version 1.2
 * @author FelipeCardoso
 */
public class AlunoController implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(AlunoController.class);
    private final AlunoService alunoService;
    private final Map<String, Map<String, HttpExchangeHandler>> routes;

    // Pattern for paths that include an ID (e.g., /api/alunos/{id})
    private static final Pattern ID_PATH_PATTERN = Pattern.compile("/api/alunos/(\\d+)");
    // Pattern for paths that include an email (e.g., /api/alunos/email/{email})
    private static final Pattern EMAIL_PATH_PATTERN = Pattern.compile("/api/alunos/email/(.+)");

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
        this.routes = new HashMap<>();
        initializeRoutes();
    }

    /**
     * Initializes the routing map, associating HTTP methods and URI patterns with their
     * respective handler methods. This centralizes routing configuration and makes it
     * easier to manage new endpoints.
     */
    private void initializeRoutes() {
        // /api/alunos
        Map<String, HttpExchangeHandler> alunosRootRoutes = new HashMap<>();
        alunosRootRoutes.put("POST", this::handleCreateAluno);
        alunosRootRoutes.put("GET", this::handleListAllAlunos);
        routes.put("/api/alunos", alunosRootRoutes);

        // /api/alunos/lote
        Map<String, HttpExchangeHandler> alunosLoteRoutes = new HashMap<>();
        alunosLoteRoutes.put("POST", this::handleCreateBatchAlunos);
        routes.put("/api/alunos/lote", alunosLoteRoutes);

        // /api/alunos/{id}
        Map<String, HttpExchangeHandler> alunosIdRoutes = new HashMap<>();
        alunosIdRoutes.put("GET", this::handleGetAlunoById);
        alunosIdRoutes.put("PUT", this::handleUpdateAluno);
        alunosIdRoutes.put("DELETE", this::handleDeleteAluno);
        routes.put(ID_PATH_PATTERN.pattern(), alunosIdRoutes); // Use a pattern for regex matching

        // /api/alunos/email/{email}
        Map<String, HttpExchangeHandler> alunosEmailRoutes = new HashMap<>();
        alunosEmailRoutes.put("GET", this::handleGetAlunoByEmail);
        routes.put(EMAIL_PATH_PATTERN.pattern(), alunosEmailRoutes); // Use a pattern for regex matching
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // CORS Preflight Handling (Always first)
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(method)) {
            sendResponse(exchange, "", 204); // No Content for preflight
            return;
        }

        // Request Dispatching
        try {
            // <<< Use HttpExchangeHandler para o tipo do handler
            HttpExchangeHandler handler = findHandler(method, path);

            if (handler != null) {
                // Execute the specific handler method
                Matcher matcher = matchPath(path); // Re-match to get groups for the handler
                handler.handle(exchange, matcher);
            } else {
                handleNotFound(exchange); // No specific handler found for this path/method
            }
        } catch (ValidacaoException e) {
            logger.warn("Erro de validação para a requisição {}: {}",
                    path, e.getMessage());
            sendErrorResponse(exchange, "Erro de validação: "
                    + e.getMessage(), 400); // Bad Request
        } catch (EntidadeNaoEncontradaException e) {
            logger.warn("Entidade não encontrada para a requisição {}: {}",
                    path, e.getMessage());
            sendErrorResponse(exchange, e.getMessage(), 404); // Not Found
        } catch (IOException e) {
            logger.error("Erro de I/O durante o processamento da requisição {}: {}",
                    path, e.getMessage(), e);
            sendErrorResponse(exchange, "Erro de comunicação com o servidor: "
                    + e.getMessage(), 500); // Internal Server Error
        } catch (Exception e) {
            logger.error("Erro interno inesperado ao processar a requisição {}: {}",
                    path, e.getMessage(), e);
            sendErrorResponse(exchange, "Erro interno no servidor: "
                    + e.getMessage(), 500); // Internal Server Error
        }
    }

    /**
     * Finds the appropriate handler method for the given HTTP method and path.
     * This uses regex patterns to match dynamic paths.
     *
     * @param method The HTTP method (e.g., "GET", "POST").
     * @param path The request URI path.
     * @return A HttpExchangeHandler representing the handler method, or null if no handler is found.
     */
    private HttpExchangeHandler findHandler(String method, String path) {
        // Try exact path match first
        Map<String, HttpExchangeHandler> methodHandlers = routes.get(path);
        if (methodHandlers != null) {
            return methodHandlers.get(method);
        }

        // Try regex path matches
        if (ID_PATH_PATTERN.matcher(path).matches()) {
            Map<String, HttpExchangeHandler> idMethodHandlers = routes.get(ID_PATH_PATTERN.pattern());
            if (idMethodHandlers != null) {
                return idMethodHandlers.get(method);
            }
        } else if (EMAIL_PATH_PATTERN.matcher(path).matches()) {
            Map<String, HttpExchangeHandler> emailMethodHandlers = routes.get(EMAIL_PATH_PATTERN.pattern());
            if (emailMethodHandlers != null) {
                return emailMethodHandlers.get(method);
            }
        }

        return null; // No handler found
    }

    /**
     * Matches the given path against known regex patterns and returns a Matcher.
     *
     * @param path The request URI path.
     * @return A Matcher object if a regex pattern matches, otherwise null.
     */
    private Matcher matchPath(String path) {
        Matcher idMatcher = ID_PATH_PATTERN.matcher(path);
        if (idMatcher.matches()) {
            return idMatcher;
        }
        Matcher emailMatcher = EMAIL_PATH_PATTERN.matcher(path);
        if (emailMatcher.matches()) {
            return emailMatcher;
        }
        return null;
    }


    // --- Specific Handler Methods for each Endpoint and HTTP Method ---

    /**
     * Handles POST requests to /api/alunos to create a new student.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  Not used for this specific handler, but required by BiConsumer signature.
     * @throws IOException If an I/O error occurs.
     */
    private void handleCreateAluno(HttpExchange exchange, Matcher matcher) throws IOException {
        logger.debug("Handling POST /api/alunos request.");
        InputStream requestBody = exchange.getRequestBody();
        AlunoDTO requestDTO = JsonMapper.fromJson(
                new String(requestBody.readAllBytes(), StandardCharsets.UTF_8), AlunoDTO.class
        );
        AlunoDTO createdAluno = alunoService.criarAluno(
                requestDTO.getNome(),
                requestDTO.getEmail(),
                requestDTO.getDataNascimento());
        sendResponse(
                exchange,
                JsonMapper.toJson(createdAluno),
                201); // Created
    }

    /**
     * Handles POST requests to /api/alunos/lote to create multiple students.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  Not used for this specific handler, but required by BiConsumer signature.
     * @throws IOException If an I/O error occurs.
     */
    private void handleCreateBatchAlunos(HttpExchange exchange, Matcher matcher) throws IOException {
        logger.debug("Handling POST /api/alunos/lote request.");
        InputStream requestBody = exchange.getRequestBody();
        List<AlunoDTO> alunos = JsonMapper.fromJsonList(
                new String(requestBody.readAllBytes(), StandardCharsets.UTF_8), AlunoDTO.class
        );

        for (AlunoDTO aluno : alunos) {
            alunoService.criarAluno(aluno.getNome(), aluno.getEmail(), aluno.getDataNascimento());
        }

        sendResponse(exchange, "{\"message\":\"Alunos cadastrados com sucesso.\"}", 201);
    }

    /**
     * Handles GET requests to /api/alunos to list all students.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  Not used for this specific handler, but required by BiConsumer signature.
     * @throws IOException If an I/O error occurs.
     */
    private void handleListAllAlunos(HttpExchange exchange, Matcher matcher) throws IOException {
        logger.debug("Handling GET /api/alunos request.");
        List<AlunoDTO> alunos = alunoService.listarTodosAlunos();
        sendResponse(exchange, JsonMapper.toJson(alunos), 200);
    }

    /**
     * Handles GET requests to /api/alunos/{id} to retrieve a student by ID.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  The Matcher containing the ID captured from the URI.
     * @throws IOException If an I/O error occurs.
     */
    private void handleGetAlunoById(HttpExchange exchange, Matcher matcher) throws IOException {
        Long id = Long.parseLong(matcher.group(1));
        logger.debug("Handling GET /api/alunos/{} request.", id);
        AlunoDTO aluno = alunoService.buscarAlunoPorId(id);
        sendResponse(exchange, JsonMapper.toJson(aluno), 200);
    }

    /**
     * Handles PUT requests to /api/alunos/{id} to update an existing student.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  The Matcher containing the ID captured from the URI.
     * @throws IOException If an I/O error occurs.
     */
    private void handleUpdateAluno(HttpExchange exchange, Matcher matcher) throws IOException {
        Long id = Long.parseLong(matcher.group(1));
        logger.debug("Handling PUT /api/alunos/{} request.", id);
        InputStream requestBody = exchange.getRequestBody();
        AlunoDTO requestDTO = JsonMapper.fromJson(
                new String(requestBody.readAllBytes(), StandardCharsets.UTF_8), AlunoDTO.class
        );
        AlunoDTO updatedAluno = alunoService.atualizarAluno(
                id,
                requestDTO.getNome(),
                requestDTO.getEmail(),
                requestDTO.getDataNascimento()
        );
        sendResponse(exchange, JsonMapper.toJson(updatedAluno), 200);
    }

    /**
     * Handles DELETE requests to /api/alunos/{id} to delete a student by ID.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  The Matcher containing the ID captured from the URI.
     * @throws IOException If an I/O error occurs.
     */
    private void handleDeleteAluno(HttpExchange exchange, Matcher matcher) throws IOException {
        Long id = Long.parseLong(matcher.group(1));
        logger.debug("Handling DELETE /api/alunos/{} request.", id);
        alunoService.deletarAluno(id);
        sendResponse(exchange, "{\"message\":\"Aluno deletado com sucesso.\"}", 200); // Or 204 No Content
    }

    /**
     * Handles GET requests to /api/alunos/email/{email} to retrieve a student by email.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  The Matcher containing the email captured from the URI.
     * @throws IOException If an I/O error occurs.
     */
    private void handleGetAlunoByEmail(HttpExchange exchange, Matcher matcher) throws IOException {
        String email = matcher.group(1);
        logger.debug("Handling GET /api/alunos/email/{} request.", email);
        AlunoDTO aluno = alunoService.buscarAlunoPorEmail(email);
        sendResponse(exchange, JsonMapper.toJson(aluno), 200);
    }

    /**
     * Handles requests for unknown endpoints or unsupported HTTP methods.
     *
     * @param exchange The HttpExchange object.
     * @throws IOException If an I/O error occurs.
     */
    private void handleNotFound(HttpExchange exchange) throws IOException {
        logger.warn("Endpoint não encontrado: {} {}", exchange.getRequestMethod(), exchange.getRequestURI().getPath());
        sendErrorResponse(exchange, "Endpoint não encontrado ou Método Não Permitido.", 404);
    }

    // --- Helper Methods for Response Handling ---

    /**
     * Adds CORS headers to the HTTP response.
     *
     * @param exchange The HttpExchange object to modify.
     */
    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }

    /**
     * Sends an HTTP response with the given status code and body.
     *
     * @param exchange   The HttpExchange object.
     * @param responseBody The response body as a String.
     * @param statusCode The HTTP status code to send.
     * @throws IOException If an I/O error occurs during response writing.
     */
    private void sendResponse(HttpExchange exchange, String responseBody, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
        logger.debug("Response sent for {}: Status {}", exchange.getRequestURI().getPath(), statusCode);
    }

    /**
     * Sends an error HTTP response with the given status code and error message.
     *
     * @param exchange   The HttpExchange object.
     * @param errorMessage The error message to include in the response body.
     * @param statusCode The HTTP status code to send.
     * @throws IOException If an I/O error occurs during response writing.
     */
    private void sendErrorResponse(HttpExchange exchange, String errorMessage, int statusCode) throws IOException {
        String errorJson = "{\"error\":\"" + errorMessage + "\"}";
        sendResponse(exchange, errorJson, statusCode);
    }
}