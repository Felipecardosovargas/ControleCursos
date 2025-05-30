package com.escola.controller;

import com.escola.dto.RelatorioCursoDTO;
import com.escola.service.RelatorioService;
import com.escola.util.JsonMapper;
import com.sun.net.httpserver.HttpHandler;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RelatorioController {

    private static final Logger LOGGER = Logger.getLogger(RelatorioController.class.getName());

    private final RelatorioService relatorioService;

    // Construtor injeta o serviço
    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    // Método não estático que retorna o HttpHandler
    public HttpHandler engajamentoHandler() {
        return exchange -> {
            LOGGER.info("Recebida requisição " + exchange.getRequestMethod() + " em " + exchange.getRequestURI());

            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                LOGGER.warning("Método HTTP não permitido: " + exchange.getRequestMethod());
                exchange.close();
                return;
            }

            try {
                List<RelatorioCursoDTO> relatorio = relatorioService.gerarRelatorioEngajamentoCursos();
                String json = JsonMapper.toJson(relatorio);

                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);

                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
                LOGGER.info("Relatório enviado com sucesso.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao gerar relatório", e);
                String errorJson = "{\"error\":\"Erro interno ao gerar relatório.\"}";
                byte[] responseBytes = errorJson.getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(500, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
            } finally {
                exchange.close();
            }
        };
    }
}
