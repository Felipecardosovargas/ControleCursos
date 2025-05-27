package com.escola;

import com.escola.config.PersistenceManager;
import com.escola.controller.HttpHandlerFactory;
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

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.concurrent.Executors;

/**
 * Main application class for the Course Management System.
 * Initializes the JPA EntityManagerFactory, sets up services,
 * and starts either a simple HTTP server or console UI to handle user interaction.
 *
 * @version 1.1
 * @author SeuNomeAqui
 */
public class MainApp {

    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando Sistema de Controle de Cursos...");

        // Initialize JPA EntityManagerFactory
        try {
            PersistenceManager.getEntityManagerFactory(); // Initialize on startup
            System.out.println("EntityManagerFactory inicializado com sucesso.");
        } catch (Exception e) {
            System.err.println("Falha ao inicializar EntityManagerFactory. Encerrando aplicação.");
            e.printStackTrace();
            return; // Exit if DB connection fails
        }

        // --- Service Instantiation (Manual "Dependency Injection") ---
        var alunoRepository = new AlunoRepositoryImpl();
        var cursoRepository = new CursoRepositoryImpl();
        var matriculaRepository = new MatriculaRepositoryImpl();

        AlunoService alunoService = new AlunoServiceImpl(alunoRepository, matriculaRepository);
        CursoService cursoService = new CursoServiceImpl(cursoRepository);
        MatriculaService matriculaService = new MatriculaServiceImpl(matriculaRepository, alunoRepository, cursoRepository);
        RelatorioService relatorioService = new RelatorioServiceImpl(matriculaRepository);

        // Menu inicial para escolher modo de execução
        String[] opcoesModo = {
                "Iniciar servidor HTTP (API)",
                "Iniciar interface de console",
                "Sair"
        };

        boolean executar = true;
        while (executar) {
            ConsoleUI.exibirMenu("Escolha o modo de execução:", opcoesModo);
            int opcao = ConsoleUI.lerInteiro("");
            switch (opcao) {
                case 1:
                    iniciarServidorHttp(alunoService, cursoService, matriculaService, relatorioService);
                    executar = false; // Sai do loop após iniciar o servidor (que roda bloqueante)
                    break;
                case 2:
                    iniciarConsoleUI(alunoService, cursoService, matriculaService, relatorioService);
                    // Após encerrar console UI, voltar ao menu inicial
                    break;
                case 3:
                    System.out.println("Encerrando aplicação...");
                    executar = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }

        PersistenceManager.close();
        ConsoleUI.fecharScanner();
        System.out.println("Aplicação encerrada.");
    }

    public static void iniciarServidorHttp(
            AlunoService alunoService,
            CursoService cursoService,
            MatriculaService matriculaService,
            RelatorioService relatorioService) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        HttpHandlerFactory factory = new HttpHandlerFactory(
                alunoService, cursoService, matriculaService, relatorioService);

        // 1) Registra APIs em primeiro lugar
        server.createContext("/api/alunos", factory.getAlunoHandler());
        server.createContext("/api/cursos", factory.getCursoHandler());
        server.createContext("/api/matriculas", factory.getMatriculaHandler());

        // 2) Registra arquivos estáticos como último recurso
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.equals("/index.html")) path = "/index.html";
            String resource = "/web" + path;  // deve existir em src/main/resources/web
            try (InputStream is = MainApp.class.getResourceAsStream(resource)) {
                if (is == null) {
                    exchange.sendResponseHeaders(404, 0);
                    exchange.close();
                    return;
                }
                String ct = path.endsWith(".css")  ? "text/css; charset=UTF-8"
                        : path.endsWith(".js")   ? "application/javascript; charset=UTF-8"
                        : "text/html; charset=UTF-8";
                exchange.getResponseHeaders().set("Content-Type", ct);
                byte[] data = is.readAllBytes();
                exchange.sendResponseHeaders(200, data.length);
                exchange.getResponseBody().write(data);
            }
            exchange.close();
        });

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("Servidor iniciado em http://localhost:" + SERVER_PORT);
        // bloqueia o main thread
        Thread.currentThread().join();
    }

    private static void iniciarConsoleUI(
            AlunoService alunoService,
            CursoService cursoService,
            MatriculaService matriculaService,
            RelatorioService relatorioService) {

        System.out.println("\nIniciando interface de console...\n");

        boolean continuar = true;
        while (continuar) {
            String[] menuPrincipal = {
                    "Gerenciar Alunos",
                    "Gerenciar Cursos",
                    "Gerenciar Matrículas",
                    "Relatórios",
                    "Calcular Idade (Exemplo DateUtil)",
                    "Voltar ao menu principal"
            };
            ConsoleUI.exibirMenu("Menu Principal - ConsoleUI", menuPrincipal);
            int opcao = ConsoleUI.lerInteiro("");

            switch (opcao) {
                case 1:
                    // Aqui você pode chamar um método para interagir com alunos
                    System.out.println("Funcionalidade de Gerenciar Alunos - ainda não implementada.");
                    break;
                case 2:
                    System.out.println("Funcionalidade de Gerenciar Cursos - ainda não implementada.");
                    break;
                case 3:
                    System.out.println("Funcionalidade de Gerenciar Matrículas - ainda não implementada.");
                    break;
                case 4:
                    System.out.println("Funcionalidade de Relatórios - ainda não implementada.");
                    break;
                case 5:
                    // Exemplo de uso do DateUtil para calcular idade
                    LocalDate dataNascimento = ConsoleUI.lerData("Informe a data de nascimento para calcular a idade");
                    try {
                        int idade = DateUtil.calcularIdade(dataNascimento);
                        System.out.println("Idade calculada: " + idade + " anos.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    break;
                case 6:
                    continuar = false; // Voltar ao menu principal do programa
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }

        System.out.println("Saindo da interface de console.\n");
    }
}
