package com.escola;

import com.escola.config.PersistenceManager;
import com.escola.controller.HttpHandlerFactory;
import com.escola.controller.RelatorioController;
import com.escola.service.AlunoService;
import com.escola.service.CursoService;
import com.escola.service.MatriculaService;
import com.escola.service.RelatorioService;
import com.escola.service.impl.AlunoServiceImpl;
import com.escola.service.impl.CursoServiceImpl;
import com.escola.service.impl.MatriculaServiceImpl;
import com.escola.service.impl.RelatorioServiceImpl;
import com.escola.repository.impl.AlunoRepositoryImpl;
import com.escola.repository.impl.CursoRepositoryImpl;
import com.escola.repository.impl.MatriculaRepositoryImpl;

import com.escola.util.ConsoleUI;
import com.escola.util.DateUtil;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main application class for the Course Management System.
 * Initializes the JPA EntityManagerFactory, sets up services,
 * and starts either a simple HTTP server or console UI to handle user interaction.
 *
 * @version 1.2
 * @author FelipeCardoso
 */
public class MainApp {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class); // Use SLF4J for MainApp
    private static final int SERVER_PORT = 8080;
    private static HttpServer httpServer; // Keep a reference to the server for graceful shutdown

    public static void main(String[] args) { // main can throw Exception, but catching is cleaner
        logger.info("Iniciando Sistema de Controle de Cursos...");

        // Initialize JPA EntityManagerFactory
        try {
            PersistenceManager.getEntityManagerFactory(); // Initialize on startup
            logger.info("EntityManagerFactory inicializado com sucesso.");
        } catch (Exception e) {
            logger.error("Falha ao inicializar EntityManagerFactory. Encerrando aplicação.", e);
            // Consider more specific exception handling if needed (e.g., SQLException)
            return; // Exit if DB connection fails
        }

        // --- Service Instantiation (Manual "Dependency Injection") ---
        // Using `var` for local type inference is fine.
        var alunoRepository = new AlunoRepositoryImpl();
        var cursoRepository = new CursoRepositoryImpl();
        var matriculaRepository = new MatriculaRepositoryImpl();

        // Instantiate services, injecting repositories
        AlunoService alunoService = new AlunoServiceImpl(alunoRepository, matriculaRepository);
        CursoService cursoService = new CursoServiceImpl(cursoRepository);
        MatriculaService matriculaService = new MatriculaServiceImpl(matriculaRepository, alunoRepository, cursoRepository);
        RelatorioService relatorioService = new RelatorioServiceImpl(matriculaRepository);

        // Initial menu to choose execution mode
        String[] executionModes = {
                "Iniciar servidor HTTP (API)",
                "Iniciar interface de console",
                "Sair"
        };

        boolean running = true;
        while (running) {
            ConsoleUI.exibirMenu("Escolha o modo de execução:", executionModes);
            int choice = ConsoleUI.lerInteiro(""); // Use more descriptive variable name
            switch (choice) {
                case 1:
                    try {
                        startHttpServer(alunoService, cursoService, matriculaService, relatorioService);
                        running = false; // Exit loop after starting server (it's blocking)
                    } catch (IOException e) {
                        logger.error("Erro ao iniciar servidor HTTP: {}", e.getMessage(), e);
                        System.out.println("Erro ao iniciar servidor HTTP. Verifique os logs.");
                    } catch (InterruptedException e) {
                        logger.warn("Servidor HTTP interrompido enquanto aguardava finalização.", e);
                        Thread.currentThread().interrupt(); // Restore interrupted status
                    }
                    break;
                case 2:
                    startConsoleUI(alunoService, cursoService, matriculaService, relatorioService);
                    // After console UI exits, return to an initial menu
                    break;
                case 3:
                    logger.info("Encerrando aplicação...");
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }

        // --- Application Shutdown ---
        // Close resources gracefully
        if (httpServer != null) {
            logger.info("Parando servidor HTTP...");
            httpServer.stop(0); // Stop immediately
            // Optionally, for a graceful shutdown, you might use a timeout:
            // httpServer.stop(3); // Wait up to 3 seconds for active requests to finish
            logger.info("Servidor HTTP parado.");
        }
        PersistenceManager.close();
        ConsoleUI.fecharScanner();
        logger.info("Aplicação encerrada.");
    }

    /**
     * Initializes and starts the HTTP server for the API.
     *
     * @param alunoService Service for student operations.
     * @param cursoService Service for course operations.
     * @param matriculaService Service for enrollment operations.
     * @param relatorioService Service for report generation.
     * @throws IOException If an I/O error occurs when creating the server.
     * @throws InterruptedException If the main thread is interrupted while waiting.
     */
    public static void startHttpServer(
            AlunoService alunoService,
            CursoService cursoService,
            MatriculaService matriculaService,
            RelatorioService relatorioService) throws IOException, InterruptedException {

        httpServer = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        HttpHandlerFactory handlerFactory = new HttpHandlerFactory(
                alunoService, cursoService, matriculaService, relatorioService);

        // --- 1) Register API Endpoints FIRST (Most Specific Paths) ---
        // This order is crucial: more specific paths must be registered before general ones.
        httpServer.createContext("/api/alunos", handlerFactory.getAlunoHandler());
        httpServer.createContext("/api/cursos", handlerFactory.getCursoHandler());
        httpServer.createContext("/api/matriculas", handlerFactory.getMatriculaHandler());
        // Use the factory to get the RelatorioController instance
        httpServer.createContext("/api/relatorios/engajamento-cursos", handlerFactory.getRelatorioEngajamentoHandler());

        // --- 2) Register Static File Server and Default Not Found Handler LAST (Catch-all) ---
        // This context will handle all requests not caught by the specific API endpoints above.
        httpServer.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            logger.debug("Attempting to serve static file or handle unmapped path for: {}", path);

            // 1. Try to serve static files from /web resources
            String resourcePath = path.equals("/") ? "/web/index.html" : "/web" + path;
            try (InputStream is = MainApp.class.getResourceAsStream(resourcePath)) {
                if (is != null) {
                    // Determine content type
                    String contentType = "application/octet-stream"; // Default
                    if (path.endsWith(".css")) contentType = "text/css; charset=UTF-8";
                    else if (path.endsWith(".js")) contentType = "application/javascript; charset=UTF-8";
                    else if (path.endsWith(".html") || path.equals("/")) contentType = "text/html; charset=UTF-8";
                    // Add more content types as needed (e.g., .png, .jpg, .json)

                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    byte[] data = is.readAllBytes();
                    exchange.sendResponseHeaders(200, data.length);
                    try (OutputStream os = exchange.getResponseBody()) { // Use try-with-resources for OutputStream
                        os.write(data);
                        os.flush();
                    }
                    logger.debug("Served static file: {}", resourcePath);
                    return; // Crucial: return after handling
                }
            } catch (IOException e) {
                logger.error("Error serving static file {}: {}", resourcePath, e.getMessage(), e);
                // If an error occurs serving a static file, fall through to default 500 or 404 handler
                exchange.sendResponseHeaders(500, -1); // -1 for unknown length
                return; // Ensure no further processing for this request
            } finally {
                // This finally block is specific to the static file serving attempt
                // The outer finally block in the default handler will handle closing the exchange
            }


            // 2. If it's not a static file, delegate to the generic Not Found Handler
            // This ensures consistent JSON 404 responses for all unhandled paths
            logger.warn("Path not found as static resource or specific API, delegating to default handler: {}", path);
            try {
                handlerFactory.getDefaultNotFoundHandler().handle(exchange);
            } catch (IOException e) {
                logger.error("Error during default 404 handling for path {}: {}", path, e.getMessage(), e);
                // Last resort if the 404 handler itself fails
                exchange.sendResponseHeaders(500, -1); // -1 for unknown length
            } finally {
                exchange.close(); // Ensure exchange is closed after handling (either static or 404)
            }
        });

        // Set a cached thread pool for handling requests concurrently
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.start();

        logger.info("Servidor iniciado em http://localhost:{}", SERVER_PORT);

        // Register a shutdown hook to gracefully stop the server and close resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Detectado sinal de desligamento. Parando servidor HTTP e liberando recursos...");
            if (httpServer != null) {
                // Give some time for ongoing requests to finish before stopping
                httpServer.stop(5); // Stop gracefully within 5 seconds
            }
            PersistenceManager.close();
            ConsoleUI.fecharScanner();
            logger.info("Recursos liberados. Aplicação desligada.");
        }));

        // Blocks the main thread, keeping the server alive.
        // The server will stop when the JVM exits (e.g., via Ctrl+C or a shutdown hook).
        Thread.currentThread().join();
    }

    /**
     * Initializes and starts the Console User Interface.
     *
     * @param alunoService Service for student operations.
     * @param cursoService Service for course operations.
     * @param matriculaService Service for enrollment operations.
     * @param relatorioService Service for report generation.
     */
    private static void startConsoleUI(
            AlunoService alunoService,
            CursoService cursoService,
            MatriculaService matriculaService,
            RelatorioService relatorioService) {

        logger.info("Iniciando interface de console...");

        boolean continueConsole = true;
        while (continueConsole) {
            String[] mainMenuOptions = {
                    "Gerenciar Alunos",
                    "Gerenciar Cursos",
                    "Gerenciar Matrículas",
                    "Relatórios",
                    "Calcular Idade (Exemplo DateUtil)",
                    "Voltar ao menu principal"
            };
            ConsoleUI.exibirMenu("Menu Principal - ConsoleUI", mainMenuOptions);
            int choice = ConsoleUI.lerInteiro("");

            switch (choice) {
                case 1:
                    logger.info("Funcionalidade 'Gerenciar Alunos' selecionada.");
                    System.out.println("Funcionalidade de Gerenciar Alunos - ainda não implementada.");
                    // TODO: Implement student management UI logic here
                    break;
                case 2:
                    logger.info("Funcionalidade 'Gerenciar Cursos' selecionada.");
                    System.out.println("Funcionalidade de Gerenciar Cursos - ainda não implementada.");
                    // TODO: Implement course management UI logic here
                    break;
                case 3:
                    logger.info("Funcionalidade 'Gerenciar Matrículas' selecionada.");
                    System.out.println("Funcionalidade de Gerenciar Matrículas - ainda não implementada.");
                    // TODO: Implement enrollment management UI logic here
                    break;
                case 4:
                    logger.info("Funcionalidade 'Relatórios' selecionada.");
                    System.out.println("Funcionalidade de Relatórios - ainda não implementada.");
                    // TODO: Implement report UI logic here
                    break;
                case 5:
                    logger.info("Funcionalidade 'Calcular Idade' selecionada.");
                    LocalDate birthDate = ConsoleUI.lerData("Informe a data de nascimento para calcular a idade");
                    try {
                        int age = DateUtil.calcularIdade(birthDate);
                        System.out.println("Idade calculada: " + age + " anos.");
                    } catch (IllegalArgumentException e) {
                        logger.warn("Erro ao calcular idade: {}", e.getMessage());
                        System.out.println("Erro: " + e.getMessage());
                    }
                    break;
                case 6:
                    logger.info("Saindo da interface de console.");
                    continueConsole = false; // Go back to the main program menu
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    logger.warn("Opção de console inválida selecionada: {}", choice);
            }
        }
    }
}