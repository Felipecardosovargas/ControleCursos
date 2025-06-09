package com.escola.controller;

import com.escola.dto.ApiResponse;
import com.escola.dto.MatriculaDTO;
import com.escola.dto.MatriculaRequestDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.OperacaoInvalidaException;
import com.escola.service.MatriculaService;
import com.escola.util.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
 * HTTP Handler to manage HTTP requests related to enrollments.
 * Exposes endpoints to create, list, fetch by ID, and remove enrollments.
 * Implements {@link HttpHandler} to integrate with Java’s native HTTP server.
 *
 * <p>Supported Endpoints:</p>
 * <ul>
 * <li><b>POST /api/matriculas</b>: Creates a new enrollment. Expects a JSON body (MatriculaRequestDTO).</li>
 * <li><b>GET /api/matriculas</b>: Lists all enrollments with student and course details.</li>
 * <li><b>GET /api/matriculas/{id}</b>: Fetches a specific enrollment by ID.</li>
 * <li><b>DELETE /api/matriculas/{id}</b>: Removes (cancels) an enrollment by ID.</li>
 * </ul>
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public class MatriculaController implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(MatriculaController.class);

    private static final String APPLICATION_JSON = "application/json; charset=UTF-8";
    // Regex pattern for paths that include an ID, e.g., /api/matriculas/{id}
    private static final Pattern ID_PATH_PATTERN = Pattern.compile("/api/matriculas/(\\d+)");

    private final MatriculaService matriculaService;
    // Route map to dispatch requests to specific handlers
    private final Map<String, Map<String, HttpExchangeHandler>> routes;

    /**
     * Constructor for MatriculaController.
     *
     * @param matriculaService Service for enrollment operations.
     */
    public MatriculaController(MatriculaService matriculaService) {
        if (matriculaService == null) {
            logger.error("MatriculaService não pode ser nulo.");
            throw new IllegalArgumentException("MatriculaService deve ser fornecido.");
        }
        this.matriculaService = matriculaService;
        this.routes = new HashMap<>();
        initializeRoutes();
        logger.info("MatriculaController inicializado.");
    }

    /**
     * Initializes the routing map, associating HTTP methods and URI patterns
     * with their respective handler methods.
     */
    private void initializeRoutes() {
        // Rotas para /api/matriculas
        Map<String, HttpExchangeHandler> matriculasCollectionRoutes = new HashMap<>();
        matriculasCollectionRoutes.put("POST", this::handleCreateMatricula);
        matriculasCollectionRoutes.put("GET", this::handleListAllMatriculas);
        routes.put("/api/matriculas", matriculasCollectionRoutes);

        // Routes for /api/matriculas/{id}
        Map<String, HttpExchangeHandler> matriculasIdRoutes = new HashMap<>();
        matriculasIdRoutes.put("GET", this::handleGetMatriculaById);
        matriculasIdRoutes.put("DELETE", this::handleDeleteMatricula);
        routes.put(ID_PATH_PATTERN.pattern(), matriculasIdRoutes);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 1. Add CORS headers (always first)
        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        logger.debug("Requisição recebida: {} {}", method, path);

        // 2. Handle OPTIONS request for CORS preflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            sendJsonResponse(exchange, 204, ""); // No Content
            return;
        }

        // 3. Request dispatching and centralized exception handling
        try {
            HttpExchangeHandler handler = findHandler(method, path);

            if (handler != null) {
                Matcher matcher = matchPath(path); // Get the Matcher to extract IDs, etc.
                handler.handle(exchange, matcher); // Call the specific handler
            } else {
                handleNotFound(exchange); // Endpoint isn't found or method not allowed
            }
        } catch (OperacaoInvalidaException e) {
            logger.warn("Operação inválida para {}: {}", path, e.getMessage());
            sendJsonResponse(exchange, 400, JsonMapper.toJson(
                    ApiResponse.error(e.getMessage()))
            );
        } catch (EntidadeNaoEncontradaException e) {
            logger.warn("Entidade não encontrada para {}: {}", path, e.getMessage());
            sendJsonResponse(exchange, 404, JsonMapper.toJson(
                    ApiResponse.error(e.getMessage()))
            );
        } catch (NumberFormatException e) {
            logger.warn("Formato de ID inválido no path {}: {}", path, e.getMessage());
            String invalidIdPart = path.substring(path.lastIndexOf('/') + 1);
            sendJsonResponse(exchange, 400, JsonMapper.toJson(
                    ApiResponse.error("ID inválido fornecido: " + invalidIdPart))
            );
        } catch (IllegalArgumentException e) {
            // Usually for deserialization errors (malformed JSON) or DTO validation
            logger.warn("Requisição mal formatada para {}: {}", path, e.getMessage());
            sendJsonResponse(exchange, 400, JsonMapper.toJson(
                    ApiResponse.error("Requisição mal formatada: " + e.getMessage()))
            );
        } catch (Exception e) {
            logger.error("Erro interno inesperado ao processar requisição {}: {}", path, e.getMessage(), e);
            sendJsonResponse(exchange, 500, JsonMapper.toJson(
                    ApiResponse.error("Erro interno no servidor. Por favor, tente novamente mais tarde."))
            );
        }
    }

    /**
     * Finds the appropriate handler for the HTTP method and request a path.
     *
     * @param method The HTTP method of the request.
     * @param path   The URI path of the request.
     * @return The corresponding HttpExchangeHandler, or null if no handler is found.
     */
    private HttpExchangeHandler findHandler(String method, String path) {
        // Tenta corresponder ao path exato primeiro
        Map<String, HttpExchangeHandler> methodHandlers = routes.get(path);
        if (methodHandlers != null) {
            return methodHandlers.get(method);
        }

        // If not an exact path, try to match regex patterns (e.g., with ID)
        if (ID_PATH_PATTERN.matcher(path).matches()) {
            Map<String, HttpExchangeHandler> idMethodHandlers = routes.get(ID_PATH_PATTERN.pattern());
            if (idMethodHandlers != null) {
                return idMethodHandlers.get(method);
            }
        }
        return null;
    }

    /**
     * Attempts to obtain a Matcher for the URI path if it matches a regex pattern.
     *
     * @param path The URI path.
     * @return A Matcher if there is a match, otherwise null.
     */
    private Matcher matchPath(String path) {
        Matcher idMatcher = ID_PATH_PATTERN.matcher(path);
        if (idMatcher.matches()) {
            return idMatcher;
        }
        return null;
    }

    // --- Specific Handler Methods for each Endpoint and HTTP Method ---

    /**
     * Handles POST requests to /api/matriculas to create a new enrollment.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  Not used for this handler.
     * @throws IOException If an I/O error occurs.
     */
    private void handleCreateMatricula(HttpExchange exchange, Matcher matcher) throws IOException {
        logger.debug("Lidando com POST /api/matriculas (criar matrícula).");
        MatriculaRequestDTO requestDTO = JsonMapper.fromJson(readRequestBody(exchange), MatriculaRequestDTO.class);

        if (requestDTO.getAlunoId() == null || requestDTO.getCursoId() == null) {
            throw new IllegalArgumentException("IDs de aluno e curso são obrigatórios para realizar a matrícula.");
        }

        MatriculaDTO matriculaDTO = matriculaService.realizarMatricula(requestDTO);
        sendJsonResponse(exchange, 201, JsonMapper.toJson(
                ApiResponse.success(matriculaDTO, "Matrícula realizada com sucesso."))
        );
    }

    /**
     * Handles GET requests to /api/matriculas to list all enrollments.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  Not used for this handler.
     * @throws IOException If an I/O error occurs.
     */
    private void handleListAllMatriculas(HttpExchange exchange, Matcher matcher) throws IOException {
        logger.debug("Lidando com GET /api/matriculas (listar todas as matrículas).");
        List<MatriculaDTO> matriculasDTO = matriculaService.listarTodasMatriculasComDetalhes();
        sendJsonResponse(exchange, 200, JsonMapper.toJson(
                ApiResponse.success(matriculasDTO, "Matrículas listadas com sucesso."))
        );
    }

    /**
     * Handles GET requests to /api/matriculas/{id} to fetch an enrollment by ID.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  The Matcher containing the ID captured from the URI.
     * @throws IOException If an I/O error occurs.
     */
    private void handleGetMatriculaById(HttpExchange exchange, Matcher matcher) throws IOException {
        long id = Long.parseLong(matcher.group(1)); // O ID é o primeiro grupo da regex
        logger.debug("Lidando com GET /api/matriculas/{} (buscar por ID).", id);
        MatriculaDTO matriculaDTO = matriculaService.buscarMatriculaPorIdComDetalhes(id);
        sendJsonResponse(exchange, 200, JsonMapper.toJson(
                ApiResponse.success(matriculaDTO, "Matrícula encontrada com sucesso."))
        );
    }

    /**
     * Handles DELETE requests to /api/matriculas/{id} to remove an enrollment by ID.
     *
     * @param exchange The HttpExchange object.
     * @param matcher  The Matcher containing the ID captured from the URI.
     * @throws IOException If an I/O error occurs.
     */
    private void handleDeleteMatricula(HttpExchange exchange, Matcher matcher) throws IOException {
        long id = Long.parseLong(matcher.group(1)); // O ID é o primeiro grupo da regex
        logger.debug("Lidando com DELETE /api/matriculas/{} (cancelar matrícula).", id);
        matriculaService.cancelarMatricula(id);
        sendJsonResponse(exchange, 200, JsonMapper.toJson(
                ApiResponse.success(null, "Matrícula com ID " + id + " cancelada com sucesso."))
        );
    }

    /**
     * Handles requests to endpoints not found or methods not allowed.
     *
     * @param exchange The HttpExchange object.
     * @throws IOException If an I/O error occurs.
     */
    private void handleNotFound(HttpExchange exchange) throws IOException {
        logger.warn("Endpoint não encontrado ou método não permitido: {} {}",
                exchange.getRequestMethod(), exchange.getRequestURI().getPath()
        );
        sendJsonResponse(exchange, 404, JsonMapper.toJson(
                ApiResponse.error("Endpoint não encontrado ou Método Não Permitido."))
        );
    }

    // --- Helper Methods ---

    /**
     * Reads the body of the HTTP request.
     *
     * @param exchange The HttpExchange object.
     * @return The request body as a String.
     * @throws IOException If an I/O error occurs.
     */
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream requestBodyStream = exchange.getRequestBody()) {
            // If the body is very large, readAllBytes might be inefficient.
            // For small APIs, it is usually acceptable.
            byte[] rawBytes = requestBodyStream.readAllBytes();
            if (rawBytes.length == 0) {
                // Throw an exception if a body is expected but not received
                throw new IllegalArgumentException("Request body is empty. A valid JSON is expected.");
            }
            return new String(rawBytes, StandardCharsets.UTF_8);
        }
    }

    /**
     * Adds CORS headers to the response.
     *
     * @param exchange The HttpExchange object.
     */
    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
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
            os.flush();
        }
        logger.debug("Resposta enviada para {}: Status {}", exchange.getRequestURI().getPath(), statusCode);
    }
}