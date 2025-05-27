package com.escola.controller;

import com.escola.dto.ApiResponse;
import com.escola.dto.MatriculaDTO;
import com.escola.dto.MatriculaRequestDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.OperacaoInvalidaException;
import com.escola.model.Matricula;
import com.escola.service.MatriculaService;
import com.escola.util.JsonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HttpHandler para gerenciar as requisições HTTP relacionadas a matrículas.
 * Expõe endpoints para criar, listar, buscar e remover matrículas.
 */
public class MatriculaController implements HttpHandler {
    private static final String API_MATRICULAS_PATH = "/api/matriculas";
    private static final String APPLICATION_JSON = "application/json; charset=UTF-8";
    private static final String ID_PATH_PARAM_REGEX = API_MATRICULAS_PATH + "/\\d+";

    private final MatriculaService matriculaService;

    /**
     * Construtor do MatriculaController.
     *
     * @param matriculaService Serviço para operações de matrícula.
     */
    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Tratamento da requisição OPTIONS para CORS preflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1); // No Content
            return;
        }

        int statusCode = 200; // HTTP OK por padrão
        String responseBody = "";

        try {
            if (API_MATRICULAS_PATH.equals(path)) {
                responseBody = handleMatriculasCollection(exchange, method);
                statusCode = "POST".equalsIgnoreCase(method) ? 201 : 200; // HTTP Created para POST
            } else if (path.matches(ID_PATH_PARAM_REGEX)) {
                long id = parseMatriculaIdFromPath(path);
                responseBody = handleMatriculaIndividual(exchange, method, id);
                // DELETE pode retornar 200 OK (com corpo) ou 204 No Content (sem corpo)
                // Mantendo 200 OK com corpo para consistência na resposta de sucesso.
            } else {
                statusCode = 404; // Not Found
                responseBody = JsonMapper.toJson(ApiResponse.error("Endpoint não encontrado."));
            }
        } catch (OperacaoInvalidaException e) {
            statusCode = 400; // Bad Request
            responseBody = JsonMapper.toJson(ApiResponse.error(e.getMessage()));
        } catch (EntidadeNaoEncontradaException e) {
            statusCode = 404; // Not Found
            responseBody = JsonMapper.toJson(ApiResponse.error(e.getMessage()));
        } catch (NumberFormatException e) {
            statusCode = 400; // Bad Request
            String invalidIdPath = path.substring(path.lastIndexOf('/') + 1);
            responseBody = JsonMapper.toJson(ApiResponse.error("ID de matrícula inválido fornecido no path: " + invalidIdPath));
        } catch (IllegalArgumentException e) { // Para erros de desserialização do JsonMapper, por exemplo
            statusCode = 400; // Bad Request
            responseBody = JsonMapper.toJson(ApiResponse.error("Requisição mal formatada: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // Log do erro no servidor
            statusCode = 500; // Internal Server Error
            responseBody = JsonMapper.toJson(ApiResponse.error("Erro interno no servidor. Por favor, tente novamente mais tarde."));
        } finally {
            sendJsonResponse(exchange, statusCode, responseBody);
        }
    }

    /**
     * Trata requisições para a coleção de matrículas (ex: /api/matriculas).
     * Suporta POST para criar e GET para listar.
     *
     * @param exchange O objeto HttpExchange.
     * @param method   O método HTTP (POST, GET).
     * @return O corpo da resposta em formato JSON.
     * @throws IOException               Se ocorrer um erro de I/O.
     * @throws OperacaoInvalidaException Se o método HTTP não for permitido.
     */
    private String handleMatriculasCollection(HttpExchange exchange, String method) throws IOException, OperacaoInvalidaException {
        if ("POST".equalsIgnoreCase(method)) {
            MatriculaRequestDTO requestDTO = JsonMapper.fromJson(readRequestBody(exchange), MatriculaRequestDTO.class);
            if (requestDTO.getAlunoId() == null || requestDTO.getCursoId() == null) {
                throw new IllegalArgumentException("IDs de aluno e curso são obrigatórios para realizar a matrícula.");
            }
            // Usando o método da service que recebe os IDs diretamente e retorna DTO
            MatriculaDTO matriculaDTO = matriculaService.realizarMatricula(requestDTO);
            // Usando ApiResponse.success(data, message) para sucesso
            return JsonMapper.toJson(ApiResponse.success(matriculaDTO, "Matrícula realizada com sucesso."));

        } else if ("GET".equalsIgnoreCase(method)) {
            // Usando o método da service que já retorna List<MatriculaDTO>
            List<MatriculaDTO> matriculasDTO = matriculaService.listarTodasMatriculasComDetalhes();
            // Usando ApiResponse.success(data, message) para sucesso
            return JsonMapper.toJson(ApiResponse.success(matriculasDTO, "Matrículas listadas com sucesso."));
        } else {
            throw new OperacaoInvalidaException("Método " + method + " não permitido para " + API_MATRICULAS_PATH);
        }
    }

    /**
     * Trata requisições para uma matrícula específica (ex: /api/matriculas/{id}).
     * Suporta GET para buscar e DELETE para remover.
     *
     * @param exchange O objeto HttpExchange.
     * @param method   O método HTTP (GET, DELETE).
     * @param id       O ID da matrícula.
     * @return O corpo da resposta em formato JSON.
     * @throws OperacaoInvalidaException      Se o método HTTP não for permitido.
     * @throws EntidadeNaoEncontradaException Se a matrícula não for encontrada.
     */
    private String handleMatriculaIndividual(HttpExchange exchange, String method, long id) throws OperacaoInvalidaException, EntidadeNaoEncontradaException, JsonProcessingException {
        if ("GET".equalsIgnoreCase(method)) {
            // Usando o método da service que busca por ID e retorna DTO
            MatriculaDTO matriculaDTO = matriculaService.buscarMatriculaPorIdComDetalhes(id);
            // A própria service deve lançar EntidadeNaoEncontradaException se não achar
            // Usando ApiResponse.success(data, message) para sucesso
            return JsonMapper.toJson(ApiResponse.success(matriculaDTO, "Matrícula encontrada com sucesso."));

        } else if ("DELETE".equalsIgnoreCase(method)) {
            // Usando o método cancelarMatricula, que não retorna nada (void)
            matriculaService.cancelarMatricula(id);
            // A própria service deve lançar EntidadeNaoEncontradaException se não achar
            // Usando ApiResponse.success(null, message) para sucesso sem dados específicos de retorno.
            // Ou ApiResponse.error(message) se o cancelamento pudesse falhar de forma que não fosse uma exceção.
            // Neste caso, como o cancelamento bem-sucedido não retorna dados, podemos usar:
            return JsonMapper.toJson(ApiResponse.success(null, "Matrícula com ID " + id + " cancelada com sucesso."));
        } else {
            throw new OperacaoInvalidaException("Método " + method + " não permitido para " + API_MATRICULAS_PATH + "/" + id);
        }
    }

    /**
     * Extrai o ID da matrícula do path da URI.
     *
     * @param path O path da URI (ex: /api/matriculas/123).
     * @return O ID da matrícula como um long.
     * @throws NumberFormatException Se o ID no path não for um número válido.
     */
    private long parseMatriculaIdFromPath(String path) throws NumberFormatException {
        return Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
    }

    /**
     * Lê o corpo da requisição HTTP.
     *
     * @param exchange O objeto HttpExchange.
     * @return O corpo da requisição como String.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream requestBodyStream = exchange.getRequestBody()) {
            return new String(requestBodyStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Adiciona cabeçalhos CORS à resposta.
     *
     * @param exchange O objeto HttpExchange.
     */
    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Permitir de qualquer origem
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    /**
     * Envia a resposta JSON ao cliente.
     *
     * @param exchange     O objeto HttpExchange.
     * @param statusCode   O código de status HTTP.
     * @param responseBody O corpo da resposta JSON.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private void sendJsonResponse(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", APPLICATION_JSON);
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    // Método auxiliar para converter Matricula para MatriculaDTO, caso a service retorne a entidade
    // Este método pode não ser necessário se a service já retornar DTOs.
    private MatriculaDTO convertToDto(Matricula matricula) {
        if (matricula == null) return null;
        if (matricula.getAluno() == null || matricula.getCurso() == null) {
            // Isso indica um problema de dados ou que a entidade não foi totalmente carregada.
            // Lançar uma exceção ou tratar como apropriado.
            throw new IllegalStateException("Matrícula com dados incompletos de aluno ou curso. ID: " + matricula.getId());
        }
        return new MatriculaDTO(
                matricula.getId(),
                matricula.getAluno().getId(),
                matricula.getAluno().getNome(), // Requer que Aluno tenha getNome()
                matricula.getCurso().getId(),
                matricula.getCurso().getNome(), // Requer que Curso tenha getNome()
                matricula.getDataMatricula()
        );
    }

    private String handlePostMatricula(HttpExchange exchange) throws IOException {
        try {
            // 1. Ler o corpo da requisição
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }

            // 2. Desserializar JSON para MatriculaRequestDTO
            ObjectMapper objectMapper = new ObjectMapper();
            MatriculaRequestDTO requestDTO = objectMapper.readValue(body.toString(), MatriculaRequestDTO.class);

            // 3. Chamar o serviço e obter a entidade Matricula
            MatriculaDTO matricula = matriculaService.realizarMatricula(requestDTO);

            // 4. Converter a entidade para DTO (se necessário)
            MatriculaDTO matriculaDTO = matricula; // ou simplesmente use a variável matricula

            // 5. Serializar a resposta para JSON
            String jsonResponse = objectMapper.writeValueAsString(matriculaDTO);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, jsonResponse.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.getBytes());
            os.close();

            return null; // resposta já foi enviada

        } catch (EntidadeNaoEncontradaException e) {
            exchange.sendResponseHeaders(404, 0);
            return "Entidade não encontrada: " + e.getMessage();

        } catch (OperacaoInvalidaException e) {
            exchange.sendResponseHeaders(400, 0);
            return "Operação inválida: " + e.getMessage();

        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
            return "Erro interno no servidor: " + e.getMessage();
        }
    }
}
