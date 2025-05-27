package com.escola.controller;

import com.escola.service.AlunoService;
import com.escola.service.CursoService;
import com.escola.service.MatriculaService;
import com.escola.service.RelatorioService;
import com.sun.net.httpserver.HttpHandler;

/**
 * A factory class responsible for creating and providing instances of HttpHandler
 * for different API contexts.
 * This helps in centralizing handler creation and dependency injection if used.
 * This class can be made final as it's a utility.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public final class HttpHandlerFactory {

    // Services that handlers depend on.
    // These would typically be initialized once and passed to the factory,
    // or the factory itself could be responsible for initializing them.
    private final AlunoService alunoService;
    private final CursoService cursoService;
    private final MatriculaService matriculaService;
    private final RelatorioService relatorioService;

    /**
     * Constructs an HttpHandlerFactory with the necessary service dependencies.
     *
     * @param alunoService Service for student operations.
     * @param cursoService Service for course operations.
     * @param matriculaService Service for enrollment operations.
     * @param relatorioService Service for report generation.
     */
    public HttpHandlerFactory(
            AlunoService alunoService,
            CursoService cursoService,
            MatriculaService matriculaService,
            RelatorioService relatorioService) {
        this.alunoService = alunoService;
        this.cursoService = cursoService;
        this.matriculaService = matriculaService;
        this.relatorioService = relatorioService;
    }

    /**
     * Gets an HttpHandler for the Aluno (student) API context.
     *
     * @return A new instance of {@link AlunoController}.
     */
    public HttpHandler getAlunoHandler() {
        return new AlunoController(alunoService);
    }

    /**
     * Gets an HttpHandler for the Curso (course) API context.
     *
     * @return A new instance of {@link CursoController}.
     */
    public HttpHandler getCursoHandler() {
        return new CursoController(cursoService, relatorioService);
    }

    /**
     * Gets an HttpHandler for the Matricula (enrollment) API context.
     *
     * @return A new instance of {@link MatriculaController}.
     */
    public HttpHandler getMatriculaHandler() {
        return new MatriculaController(matriculaService);
    }

    // You could also have a generic method if handlers followed a stricter pattern
    // or were registered, but for a few handlers, specific methods are clear.
    // Example:
    // public HttpHandler getHandler(String contextPath) {
    //     switch (contextPath) {
    //         case "/api/alunos":
    //             return new AlunoController(alunoService);
    //         case "/api/cursos":
    //             return new CursoController(cursoService, relatorioService);
    //         case "/api/matriculas":
    //             return new MatriculaController(matriculaService);
    //         default:
    //             // Return a default 404 handler or throw an exception
    //             return exchange -> {
    //                 String response = "{\"error\":\"Context not found by factory\"}";
    //                 exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
    //                 exchange.sendResponseHeaders(404, response.getBytes().length);
    //                 try (OutputStream os = exchange.getResponseBody()) {
    //                     os.write(response.getBytes());
    //                 }
    //             };
    //     }
    // }
}