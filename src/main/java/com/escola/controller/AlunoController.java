package com.escola.controller;

import com.escola.dto.AlunoDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.ValidacaoException;
import com.escola.service.AlunoService;
import com.escola.util.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HTTP Handler for managing Aluno (student) related requests.
 * Implements {@link HttpHandler} to integrate with Java's built-in HTTP server.
 * <p>
 * Endpoints:
 * - POST /alunos: Create a new student. Expects JSON body.
 * - GET /alunos: List all students.
 * - GET /alunos/{id}: Get a student by ID.
 * - GET /alunos/email/{email}: Get a student by email.
 * - PUT /alunos/{id}: Update a student. Expects JSON body.
 * - DELETE /alunos/{id}: Delete a student.
 * </p>
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public class AlunoController implements HttpHandler {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String responseBody = "";
        int statusCode = 200;

        try {
            // Enable CORS for local development if the frontend is served from a different port
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1); // No Content for preflight
                return;
            }

            if (path.equals("/api/alunos")) {
                if ("POST".equalsIgnoreCase(method)) {
                    // Create Aluno
                    InputStream requestBody = exchange.getRequestBody();
                    // A DTO for request might be better here, e.g., AlunoCreateRequestDTO
                    AlunoDTO requestDTO = JsonMapper.fromJson(new String(requestBody.readAllBytes(), StandardCharsets.UTF_8), AlunoDTO.class); // Simplified
                    AlunoDTO createdAluno = alunoService.criarAluno(requestDTO.getNome(), requestDTO.getEmail(), requestDTO.getDataNascimento());
                    responseBody = JsonMapper.toJson(createdAluno);
                    statusCode = 201; // Created
                } else if ("GET".equalsIgnoreCase(method)) {
                    // List Alunos
                    List<AlunoDTO> alunos = alunoService.listarTodosAlunos();
                    responseBody = JsonMapper.toJson(alunos);
                } else {
                    statusCode = 405; // Method Not Allowed
                    responseBody = "{\"error\":\"Method Not Allowed\"}";
                }
            } else if (path.equals("/api/alunos/lote")) {
            if ("POST".equalsIgnoreCase(method)) {
                InputStream requestBody = exchange.getRequestBody();
                List<AlunoDTO> alunos = JsonMapper.fromJsonList(
                        new String(requestBody.readAllBytes(), StandardCharsets.UTF_8),
                        AlunoDTO.class
                );

                for (AlunoDTO aluno : alunos) {
                    alunoService.criarAluno(aluno.getNome(), aluno.getEmail(), aluno.getDataNascimento());
                }

                responseBody = "{\"message\":\"Alunos cadastrados com sucesso.\"}";
                statusCode = 201;
            } else {
                statusCode = 405;
                responseBody = "{\"error\":\"Method Not Allowed\"}";
            }
        } else if (path.matches("/api/alunos/\\d+")) { // e.g., /api/alunos/1
                Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
                if ("GET".equalsIgnoreCase(method)) {
                    AlunoDTO aluno = alunoService.buscarAlunoPorId(id);
                    responseBody = JsonMapper.toJson(aluno);
                } else if ("PUT".equalsIgnoreCase(method)) {
                    InputStream requestBody = exchange.getRequestBody();
                    AlunoDTO requestDTO = JsonMapper.fromJson(new String(requestBody.readAllBytes(), StandardCharsets.UTF_8), AlunoDTO.class);
                    AlunoDTO updatedAluno = alunoService.atualizarAluno(id, requestDTO.getNome(), requestDTO.getEmail(), requestDTO.getDataNascimento());
                    responseBody = JsonMapper.toJson(updatedAluno);
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    alunoService.deletarAluno(id);
                    responseBody = "{\"message\":\"Aluno deletado com sucesso.\"}";
                    statusCode = 200; // Or 204 No Content
                } else {
                    statusCode = 405;
                    responseBody = "{\"error\":\"Method Not Allowed\"}";
                }
            } else if (path.matches("/api/alunos/email/.+")) { // e.g., /api/alunos/email/test@example.com
                String email = path.substring(path.lastIndexOf('/') + 1);
                if ("GET".equalsIgnoreCase(method)) {
                    AlunoDTO aluno = alunoService.buscarAlunoPorEmail(email);
                    responseBody = JsonMapper.toJson(aluno);
                } else {
                    statusCode = 405;
                    responseBody = "{\"error\":\"Method Not Allowed\"}";
                }
            } else {
                statusCode = 404; // Not Found
                responseBody = "{\"error\":\"Endpoint não encontrado\"}";
            }
        } catch (ValidacaoException e) {
            statusCode = 400; // Bad Request
            responseBody = "{\"error\":\"Erro de validação: " + e.getMessage() + "\"}";
        } catch (EntidadeNaoEncontradaException e) {
            statusCode = 404; // Not Found
            responseBody = "{\"error\":\"" + e.getMessage() + "\"}";
        } catch (Exception e) {
            e.printStackTrace(); // Log the full error
            statusCode = 500; // Internal Server Error
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