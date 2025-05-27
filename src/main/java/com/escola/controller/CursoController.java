package com.escola.controller;

import com.escola.dto.CursoDTO;
import com.escola.dto.RelatorioCursoDTO; // For the bonus report
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.ValidacaoException;
import com.escola.service.CursoService;
import com.escola.service.RelatorioService; // For the bonus report
import com.escola.util.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map; // For request DTO if not using a specific one for create/update

/**
 * HTTP Handler for managing Curso (course) related requests.
 * Implements {@link HttpHandler} to integrate with Java's built-in HTTP server.
 * <p>
 * Endpoints:
 * - POST /api/cursos: Create a new course. Expects JSON body.
 * - GET /api/cursos: List all courses.
 * - GET /api/cursos/{id}: Get a course by ID.
 * - GET /api/cursos/nome/{nome}: Get a course by name (or part of it).
 * - PUT /api/cursos/{id}: Update a course. Expects JSON body.
 * - DELETE /api/cursos/{id}: Delete a course.
 * - GET /api/cursos/relatorio/engajamento: (Bonus) Get a course engagement report.
 * </p>
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public class CursoController implements HttpHandler {

    private final CursoService cursoService;
    private final RelatorioService relatorioService; // Injected for the bonus report

    public CursoController(CursoService cursoService, RelatorioService relatorioService) {
        this.cursoService = cursoService;
        this.relatorioService = relatorioService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery(); // For search by name part
        String responseBody = "";
        int statusCode = 200;

        try {
            // CORS Headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (path.equals("/api/cursos")) {
                if ("POST".equalsIgnoreCase(method)) {
                    InputStream requestBodyStream = exchange.getRequestBody();
                    // Using a generic Map for simplicity, or create CursoCreateDTO
                    @SuppressWarnings("unchecked") // Be cautious with direct casting
                    Map<String, Object> requestData = JsonMapper.fromJson(new String(requestBodyStream.readAllBytes(), StandardCharsets.UTF_8), Map.class);

                    String nome = (String) requestData.get("nome");
                    String descricao = (String) requestData.get("descricao");
                    // Ensure cargaHoraria is parsed correctly (Jackson might give Integer or Double)
                    Object cargaHorariaObj = requestData.get("cargaHoraria");
                    int cargaHoraria = 0;
                    if (cargaHorariaObj instanceof Number) {
                        cargaHoraria = ((Number) cargaHorariaObj).intValue();
                    } else if (cargaHorariaObj instanceof String) {
                        cargaHoraria = Integer.parseInt((String) cargaHorariaObj);
                    } else {
                        throw new ValidacaoException("Carga horária inválida ou ausente.");
                    }

                    CursoDTO createdCurso = cursoService.criarCurso(nome, descricao, cargaHoraria);
                    responseBody = JsonMapper.toJson(createdCurso);
                    statusCode = 201;
                } else if ("GET".equalsIgnoreCase(method)) {
                    // Check for search by name parameter
                    if (query != null && query.startsWith("nome=")) {
                        String nomeQuery = query.substring(query.indexOf("nome=") + 5);
                        // Decode URL-encoded characters (e.g., %20 for space)
                        nomeQuery = java.net.URLDecoder.decode(nomeQuery, StandardCharsets.UTF_8.name());
                        List<CursoDTO> cursos = cursoService.buscarCursosPorNomeContendo(nomeQuery);
                        responseBody = JsonMapper.toJson(cursos);
                    } else {
                        List<CursoDTO> cursos = cursoService.listarTodosCursos();
                        responseBody = JsonMapper.toJson(cursos);
                    }
                } else {
                    statusCode = 405;
                    responseBody = "{\"error\":\"Method Not Allowed\"}";
                }
            } else if (path.matches("/api/cursos/\\d+")) { // e.g., /api/cursos/1
                Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
                if ("GET".equalsIgnoreCase(method)) {
                    CursoDTO curso = cursoService.buscarCursoPorId(id);
                    responseBody = JsonMapper.toJson(curso);
                } else if ("PUT".equalsIgnoreCase(method)) {
                    InputStream requestBodyStream = exchange.getRequestBody();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> requestData = JsonMapper.fromJson(new String(requestBodyStream.readAllBytes(), StandardCharsets.UTF_8), Map.class);

                    String nome = (String) requestData.get("nome");
                    String descricao = (String) requestData.get("descricao");
                    Integer cargaHoraria = requestData.get("cargaHoraria") != null ? ((Number)requestData.get("cargaHoraria")).intValue() : null;

                    CursoDTO updatedCurso = cursoService.atualizarCurso(id, nome, descricao, cargaHoraria);
                    responseBody = JsonMapper.toJson(updatedCurso);
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    cursoService.deletarCurso(id);
                    responseBody = "{\"message\":\"Curso deletado com sucesso.\"}";
                } else {
                    statusCode = 405;
                    responseBody = "{\"error\":\"Method Not Allowed\"}";
                }
            } else if (path.equals("/api/cursos/relatorio/engajamento")) { // Bonus endpoint
                if ("GET".equalsIgnoreCase(method)) {
                    List<RelatorioCursoDTO> relatorio = relatorioService.gerarRelatorioEngajamentoCursos();
                    responseBody = JsonMapper.toJson(relatorio);
                } else {
                    statusCode = 405;
                    responseBody = "{\"error\":\"Method Not Allowed\"}";
                }
            }
            // Note: The "/api/cursos/nome/{nome}" specific path for full name match can be ambiguous
            // with "/api/cursos/{id}" if a name is numeric. Using query param `?nome=...` is safer for partial matches.
            // If you need specific path for full name:
            // else if (path.startsWith("/api/cursos/nome/")) {
            //    String nome = path.substring("/api/cursos/nome/".length());
            //    nome = java.net.URLDecoder.decode(nome, StandardCharsets.UTF_8.name());
            //    if ("GET".equalsIgnoreCase(method)) {
            //        CursoDTO curso = cursoService.buscarCursoPorNomeExato(nome); // Requires this method in service
            //        responseBody = JsonMapper.toJson(curso);
            //    } else { ... }
            // }
            else {
                statusCode = 404;
                responseBody = "{\"error\":\"Endpoint não encontrado\"}";
            }
        } catch (ValidacaoException e) {
            statusCode = 400;
            responseBody = "{\"error\":\"Erro de validação: " + e.getMessage() + "\"}";
        } catch (EntidadeNaoEncontradaException e) {
            statusCode = 404;
            responseBody = "{\"error\":\"" + e.getMessage() + "\"}";
        } catch (NumberFormatException e) {
            statusCode = 400;
            responseBody = "{\"error\":\"ID ou parâmetro numérico inválido: " + e.getMessage() + "\"}";
        }
        catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            responseBody = "{\"error\":\"Erro interno no servidor: " + e.getMessage() + "\"}";
        } finally {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}