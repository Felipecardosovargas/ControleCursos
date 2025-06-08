package com.escola.controller;

import com.escola.dto.CursoDTO;
import com.escola.dto.RelatorioCursoDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.ValidacaoException;
import com.escola.service.CursoService;
import com.escola.service.RelatorioService;
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
import java.net.URLDecoder;

/**
 * HTTP Handler for managing Curso (course) related requests.
 * Implements {@link HttpHandler} to integrate with Java's built-in HTTP server.
 * <p>
 * This controller leverages a more structured approach for request handling,
 * dispatching requests to specific methods based on the HTTP method and URI path.
 * It focuses on separating concerns, improving readability, and making the code
 * more maintainable and testable.
 * </p>
 * <p>
 * Endpoints:
 * - POST /api/cursos: Create a new course. Expects JSON body (CursoDTO).
 * - GET /api/cursos: List all courses. Can filter by name using query param `?nome={nome}`.
 * - GET /api/cursos/{id}: Get a course by ID.
 * - PUT /api/cursos/{id}: Update a course. Expect JSON body (CursoDTO).
 * - DELETE /api/cursos/{id}: Delete a course.
 * - GET /api/cursos/relatorio/engajamento: (Bonus) Get a course engagement report.
 * </p>
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public class CursoController implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(CursoController.class);
    private final CursoService cursoService;
    private final RelatorioService relatorioService;

    // The route map stores our custom functional interface
    private final Map<String, Map<String, HttpExchangeHandler>> routes;

    // Regex patterns for dynamic URLs
    private static final Pattern ID_PATH_PATTERN = Pattern.compile("/api/cursos/(\\d+)"); // /api/cursos/{id}
    // The route /api/cursos/nome/{nome} is less robust than using query parameters, but if a dedicated route is needed:
    // private static final Pattern NOME_PATH_PATTERN = Pattern.compile("/api/cursos/nome/(.+)");


    public CursoController(CursoService cursoService, RelatorioService relatorioService) {
        this.cursoService = cursoService;
        this.relatorioService = relatorioService;
        this.routes = new HashMap<>();
        initializeRoutes();
    }

    /**
     * Initializes the routing map, associating HTTP methods and URI patterns with their
     * respective handler methods. This centralizes routing configuration and makes it
     * easier to manage new endpoints.
     */
    private void initializeRoutes() {
        // /api/cursos (Create all, List all/by name)
        Map<String, HttpExchangeHandler> cursosRootRoutes = new HashMap<>();
        cursosRootRoutes.put("POST", this::handleCreateCurso);
        cursosRootRoutes.put("GET", this::handleListAllCursos);
        routes.put("/api/cursos", cursosRootRoutes);

        // /api/cursos/{id} (Get by ID, Update, Delete)
        Map<String, HttpExchangeHandler> cursosIdRoutes = new HashMap<>();
        cursosIdRoutes.put("GET", this::handleGetCursoById);
        cursosIdRoutes.put("PUT", this::handleUpdateCurso);
        cursosIdRoutes.put("DELETE", this::handleDeleteCurso);
        routes.put(ID_PATH_PATTERN.pattern(), cursosIdRoutes); // Usa o padrão regex

        // /api/cursos/relatorio/engajamento (Bonus report)
        Map<String, HttpExchangeHandler> relatorioEngagementRoutes = new HashMap<>();
        relatorioEngagementRoutes.put("GET", this::handleGetRelatorioEngajamento);
        routes.put("/api/cursos/relatorio/engajamento", relatorioEngagementRoutes);

        // Se você decidir usar a rota /api/cursos/nome/{nome} para busca exata por nome:
        // Map<String, HttpExchangeHandler> cursosNomeRoutes = new HashMap<>();
        // cursosNomeRoutes.put("GET", this::handleGetCursoByNome);
        // routes.put(NOME_PATH_PATTERN.pattern(), cursosNomeRoutes);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        // String query = exchange.getRequestURI().getQuery(); // Query params serão tratados nos handlers específicos

        // 1. CORS Preflight Handling (Sempre primeiro)
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(method)) {
            sendResponse(exchange, "", 204); // No Content para preflight
            return;
        }

        // 2. Despacho da Requisição
        try {
            HttpExchangeHandler handler = findHandler(method, path);

            if (handler != null) {
                // Executa o método handler específico
                // O matcher é importante para rotas com parâmetros na URL como /api/cursos/{id}
                Matcher matcher = matchPath(path);
                handler.handle(exchange, matcher); // Chama o método 'handle' da nossa interface
            } else {
                handleNotFound(exchange); // Nenhum handler específico encontrado para este caminho/método
            }
        } catch (ValidacaoException e) {
            logger.warn("Erro de validação para a requisição {}: {}", path, e.getMessage());
            sendErrorResponse(exchange, "Erro de validação: " + e.getMessage(), 400); // Bad Request
        } catch (EntidadeNaoEncontradaException e) {
            logger.warn("Entidade não encontrada para a requisição {}: {}", path, e.getMessage());
            sendErrorResponse(exchange, e.getMessage(), 404); // Not Found
        } catch (NumberFormatException e) { // Captura erros de parsing de ID, etc.
            logger.error("Erro de formato numérico na requisição {}: {}", path, e.getMessage(), e);
            sendErrorResponse(exchange, "ID ou parâmetro numérico inválido: " + e.getMessage(), 400); // Bad Request
        } catch (IOException e) {
            logger.error("Erro de I/O durante o processamento da requisição {}: {}", path, e.getMessage(), e);
            sendErrorResponse(exchange, "Erro de comunicação com o servidor: " + e.getMessage(), 500); // Internal Server Error
        } catch (Exception e) { // Catch genérico para qualquer outra exceção inesperada
            logger.error("Erro interno inesperado ao processar a requisição {}: {}", path, e.getMessage(), e);
            sendErrorResponse(exchange, "Erro interno no servidor: " + e.getMessage(), 500); // Internal Server Error
        }
    }

    /**
     * Encontra o método handler apropriado para o método HTTP e caminho fornecidos.
     * Utiliza padrões regex para corresponder a caminhos dinâmicos.
     *
     * @param method O método HTTP (ex: "GET", "POST").
     * @param path   O caminho da URI da requisição.
     * @return Um {@link HttpExchangeHandler} representando o método handler, ou null se nenhum handler for encontrado.
     */
    private HttpExchangeHandler findHandler(String method, String path) {
        // Tenta corresponder o caminho exato primeiro
        Map<String, HttpExchangeHandler> methodHandlers = routes.get(path);
        if (methodHandlers != null) {
            return methodHandlers.get(method);
        }

        // Tenta corresponder padrões regex
        if (ID_PATH_PATTERN.matcher(path).matches()) {
            Map<String, HttpExchangeHandler> idMethodHandlers = routes.get(ID_PATH_PATTERN.pattern());
            if (idMethodHandlers != null) {
                return idMethodHandlers.get(method);
            }
        }
        // Se você tivesse a rota /api/cursos/nome/{nome}
        // else if (NOME_PATH_PATTERN.matcher(path).matches()) {
        //    Map<String, HttpExchangeHandler> nomeMethodHandlers = routes.get(NOME_PATH_PATTERN.pattern());
        //    if (nomeMethodHandlers != null) {
        //        return nomeMethodHandlers.get(method);
        //    }
        // }

        return null; // Nenhum handler encontrado
    }

    /**
     * Corresponde o caminho fornecido a padrões regex conhecidos e retorna um Matcher.
     *
     * @param path O caminho da URI da requisição.
     * @return Um objeto Matcher se um padrão regex corresponder, caso contrário, null.
     */
    private Matcher matchPath(String path) {
        Matcher idMatcher = ID_PATH_PATTERN.matcher(path);
        if (idMatcher.matches()) {
            return idMatcher;
        }
        // Se você tivesse a rota /api/cursos/nome/{nome}
        // Matcher nomeMatcher = NOME_PATH_PATTERN.matcher(path);
        // if (nomeMatcher.matches()) {
        //     return nomeMatcher;
        // }
        return null;
    }

    // --- Métodos Handler Específicos para cada Endpoint e Método HTTP ---

    /**
     * Lida com requisições POST para /api/cursos para criar um novo curso.
     * Espera um corpo JSON contendo 'nome', 'descricao' e 'cargaHoraria'.
     *
     * @param exchange O objeto HttpExchange.
     * @param matcher  Não utilizado diretamente para este handler, mas necessário pela assinatura de HttpExchangeHandler.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private void handleCreateCurso(HttpExchange exchange, Matcher matcher) throws IOException {
        logger.debug("Lidando com requisição POST /api/cursos.");
        InputStream requestBodyStream = exchange.getRequestBody();
        // Usando CursoDTO diretamente para uma tipagem mais forte e menos casting
        CursoDTO requestDTO = JsonMapper.fromJson(
                new String(requestBodyStream.readAllBytes(), StandardCharsets.UTF_8), CursoDTO.class
        );

        // Validação básica do DTO antes de passar para o serviço
        if (requestDTO.getNome() == null || requestDTO.getNome().trim().isEmpty() ||
                requestDTO.getCargaHoraria() <= 0) {
            throw new ValidacaoException("Nome do curso e carga horária são obrigatórios e válidos.");
        }

        CursoDTO createdCurso = cursoService.criarCurso(requestDTO.getNome(), requestDTO.getDescricao(), requestDTO.getCargaHoraria());
        sendResponse(exchange, JsonMapper.toJson(createdCurso), 201); // Created
    }

    /**
     * Lida com requisições GET para /api/cursos para listar todos os cursos ou buscar por nome.
     * Suporta um parâmetro de query 'nome' para busca parcial (ex: /api/cursos?nome=Java).
     *
     * @param exchange O objeto HttpExchange.
     * @param matcher  Não utilizado diretamente para este handler.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private void handleListAllCursos(HttpExchange exchange, Matcher matcher) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        logger.debug("Lidando com requisição GET /api/cursos. Query: {}", query);

        List<CursoDTO> cursos;
        if (query != null && query.startsWith("nome=")) {
            try {
                String nomeQuery = query.substring(query.indexOf("nome=") + 5);
                // Decodifica caracteres URL-encoded (ex: %20 para espaço)
                nomeQuery = URLDecoder.decode(nomeQuery, StandardCharsets.UTF_8.name());
                cursos = cursoService.buscarCursosPorNomeContendo(nomeQuery);
            } catch (IllegalArgumentException e) {
                // Erro de decodificação ou formato inválido
                throw new ValidacaoException("Parâmetro 'nome' inválido na URL.");
            }
        } else {
            cursos = cursoService.listarTodosCursos();
        }
        sendResponse(exchange, JsonMapper.toJson(cursos), 200);
    }

    /**
     * Lida com requisições GET para /api/cursos/{id} para buscar um curso por ID.
     *
     * @param exchange O objeto HttpExchange.
     * @param matcher  O Matcher contendo o ID capturado da URI.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private void handleGetCursoById(HttpExchange exchange, Matcher matcher) throws IOException {
        Long id = Long.parseLong(matcher.group(1));
        logger.debug("Lidando com requisição GET /api/cursos/{} (ID).", id);
        CursoDTO curso = cursoService.buscarCursoPorId(id);
        sendResponse(exchange, JsonMapper.toJson(curso), 200);
    }

    /**
     * Lida com requisições PUT para /api/cursos/{id} para atualizar um curso existente.
     * Espera um corpo JSON contendo os dados do curso a serem atualizados.
     *
     * @param exchange O objeto HttpExchange.
     * @param matcher  O Matcher contendo o ID capturado da URI.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private void handleUpdateCurso(HttpExchange exchange, Matcher matcher) throws IOException {
        Long id = Long.parseLong(matcher.group(1));
        logger.debug("Lidando com requisição PUT /api/cursos/{} (ID).", id);
        InputStream requestBodyStream = exchange.getRequestBody();
        CursoDTO requestDTO = JsonMapper.fromJson(
                new String(requestBodyStream.readAllBytes(), StandardCharsets.UTF_8), CursoDTO.class
        );

        // Validação básica
        if (requestDTO.getNome() != null && requestDTO.getNome().trim().isEmpty()) {
            throw new ValidacaoException("Nome do curso não pode ser vazio se fornecido.");
        }
        if (requestDTO.getCargaHoraria() <= 0) {
            throw new ValidacaoException("Carga horária deve ser um valor positivo.");
        }

        CursoDTO updatedCurso = cursoService.atualizarCurso(id, requestDTO.getNome(), requestDTO.getDescricao(), requestDTO.getCargaHoraria());
        sendResponse(exchange, JsonMapper.toJson(updatedCurso), 200);
    }

    /**
     * Lida com requisições DELETE para /api/cursos/{id} para deletar um curso por ID.
     *
     * @param exchange O objeto HttpExchange.
     * @param matcher  O Matcher contendo o ID capturado da URI.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private void handleDeleteCurso(HttpExchange exchange, Matcher matcher) throws IOException {
        Long id = Long.parseLong(matcher.group(1));
        logger.debug("Lidando com requisição DELETE /api/cursos/{} (ID).", id);
        cursoService.deletarCurso(id);
        sendResponse(exchange, "{\"message\":\"Curso deletado com sucesso.\"}", 200); // Ou 204 No Content
    }

    /**
     * Lida com requisições GET para /api/cursos/relatorio/engajamento para gerar um relatório de engajamento de cursos.
     * Este é um endpoint de bônus que demonstra a integração de um serviço de relatório.
     *
     * @param exchange O objeto HttpExchange.
     * @param matcher  Não utilizado para este handler.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private void handleGetRelatorioEngajamento(HttpExchange exchange, Matcher matcher) throws IOException {
        logger.debug("Lidando com requisição GET /api/cursos/relatorio/engajamento.");
        List<RelatorioCursoDTO> relatorio = relatorioService.gerarRelatorioEngajamentoCursos();
        sendResponse(exchange, JsonMapper.toJson(relatorio), 200);
    }

    /**
     * Lida com requisições para endpoints desconhecidos ou métodos HTTP não suportados.
     *
     * @param exchange O objeto HttpExchange.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private void handleNotFound(HttpExchange exchange) throws IOException {
        logger.warn("Endpoint não encontrado: {} {}", exchange.getRequestMethod(), exchange.getRequestURI().getPath());
        sendErrorResponse(exchange, "Endpoint não encontrado ou Método Não Permitido.", 404);
    }

    // --- Métodos Auxiliares para Manipulação de Resposta ---

    /**
     * Adiciona cabeçalhos CORS à resposta HTTP.
     *
     * @param exchange O objeto HttpExchange a ser modificado.
     */
    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }

    /**
     * Envia uma resposta HTTP com o código de status e corpo fornecidos.
     *
     * @param exchange    O objeto HttpExchange.
     * @param responseBody O corpo da resposta como uma String.
     * @param statusCode O código de status HTTP a ser enviado.
     * @throws IOException Se ocorrer um erro de I/O durante a escrita da resposta.
     */
    private void sendResponse(HttpExchange exchange, String responseBody, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
        logger.debug("Resposta enviada para {}: Status {}", exchange.getRequestURI().getPath(), statusCode);
    }

    /**
     * Envia uma resposta de erro HTTP com o código de status e mensagem de erro fornecidos.
     *
     * @param exchange     O objeto HttpExchange.
     * @param errorMessage A mensagem de erro a ser incluída no corpo da resposta.
     * @param statusCode O código de status HTTP a ser enviado.
     * @throws IOException Se ocorrer um erro de I/O durante a escrita da resposta.
     */
    private void sendErrorResponse(HttpExchange exchange, String errorMessage, int statusCode) throws IOException {
        String errorJson = "{\"error\":\"" + errorMessage + "\"}";
        sendResponse(exchange, errorJson, statusCode);
    }
}