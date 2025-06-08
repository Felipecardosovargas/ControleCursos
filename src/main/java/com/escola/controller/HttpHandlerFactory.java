package com.escola.controller;

import com.escola.service.AlunoService;
import com.escola.service.CursoService;
import com.escola.service.MatriculaService;
import com.escola.service.RelatorioService;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * A centralized factory for creating and providing instances of {@link HttpHandler}s.
 * This class facilitates dependency injection into API controllers,
 * ensuring they are initialized with the necessary services.
 *
 * <p>By centralizing the creation of handlers, it promotes configuration consistency
 * and a single point for managing the service dependencies of each controller.</p>
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public final class HttpHandlerFactory {

    private static final Logger logger = LoggerFactory.getLogger(HttpHandlerFactory.class);

    private final AlunoService alunoService;
    private final CursoService cursoService;
    private final MatriculaService matriculaService;
    private final RelatorioService relatorioService;

    /**
     * Constructs a new instance of HttpHandlerFactory with all service dependencies.
     * The services are injected at the time the factory is constructed and are used to
     * initialize the HTTP controllers.
     *
     * @param alunoService The service responsible for Aluno (Student) operations.
     * @param cursoService The service responsible for Curso (Course) operations.
     * @param matriculaService The service responsible for Matrícula (Enrollment) operations.
     * @param relatorioService The service responsible for generating reports.
     */
    public HttpHandlerFactory(
            AlunoService alunoService,
            CursoService cursoService,
            MatriculaService matriculaService,
            RelatorioService relatorioService) {
        // Basic validation to ensure that no essential service is null
        if (alunoService == null || cursoService == null || matriculaService == null || relatorioService == null) {
            logger.error("Todos os serviços (AlunoService, CursoService, MatriculaService, RelatorioService) devem ser fornecidos e não podem ser nulos.");
            throw new IllegalArgumentException("Serviços não podem ser nulos ao inicializar HttpHandlerFactory.");
        }

        this.alunoService = alunoService;
        this.cursoService = cursoService;
        this.matriculaService = matriculaService;
        this.relatorioService = relatorioService;
        logger.info("HttpHandlerFactory inicializada com sucesso com todos os serviços.");
    }

    /**
     * Provides a new instance of {@link AlunoController}, injecting the {@link AlunoService}.
     * This handler is responsible for all CRUD operations related to students.
     *
     * @return A configured instance of {@link AlunoController}.
     */
    public HttpHandler getAlunoHandler() {
        logger.debug("Criando e retornando AlunoController.");
        return new AlunoController(alunoService);
    }

    /**
     * Provides a new instance of {@link CursoController}, injecting the {@link CursoService}
     * and {@link RelatorioService}. This handler manages course operations
     * and the course engagement report endpoint.
     *
     * @return A configured instance of {@link CursoController}.
     */
    public HttpHandler getCursoHandler() {
        logger.debug("Criando e retornando CursoController.");
        return new CursoController(cursoService, relatorioService);
    }

    /**
     * Provides a new instance of {@link MatriculaController}, injecting the {@link MatriculaService}.
     * This handler deals with student enrollment operations in courses.
     *
     * @return A configured instance of {@link MatriculaController}.
     */
    public HttpHandler getMatriculaHandler() {
        logger.debug("Criando e retornando MatriculaController.");
        return new MatriculaController(matriculaService);
    }

    /**
     * Fornece uma nova instância de {@link RelatorioController},
     * especificamente configurado para o relatório de engajamento de cursos.
     * Este método injeta o {@link RelatorioService} necessário no controlador de relatório.
     *
     * @return Uma nova instância configurada de {@link RelatorioController}.
     */
    public HttpHandler getRelatorioEngajamentoHandler() {
        logger.debug("Criando e retornando RelatorioController para o relatório de engajamento de cursos.");
        return new RelatorioController(relatorioService);
    }

    /**
     * Provides a default HttpHandler to handle API contexts that are not found
     * or not mapped by the factory. This handler returns a 404 (Not Found) error response.
     *
     * <p>It can be useful as a fallback in scenarios where a context path
     * is not explicitly assigned to a specific handler.</p>
     *
     * @return A generic {@link HttpHandler} that returns 404 Not Found.
     */
    public HttpHandler getDefaultNotFoundHandler() {
        logger.warn("Creating and returning DefaultNotFoundHandler.");
        return exchange -> {
            String response = "{\"error\":\"Resource not found\"}";
            try {
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(404, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                    os.flush(); // Ensure data is immediately sent
                }
                logger.debug("Sent 404 Not Found response for path: {}", exchange.getRequestURI().getPath());
            } catch (IOException e) {
                // This catch block handles errors *during* sending the 404 response.
                // At this point, the connection might be broken, so logging is key.
                logger.error("Error sending default 404 response for path {}: {}", exchange.getRequestURI().getPath(), e.getMessage(), e);
            } finally {
                // Crucial: Always close the exchange to free up resources.
                exchange.close();
            }
        };
    }
    }