package com.escola.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Classe utilitária para interações com o console.
 * Fornece métodos para exibir menus, ler entrada do usuário e formatar datas.
 * Esta classe é final, pois não se pretende que seja estendida.
 */
public final class ConsoleUI {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Construtor privado para evitar a instanciação da classe utilitária.
     */
    private ConsoleUI() {
        // Classe utilitária não deve ser instanciada.
    }

    /**
     * Exibe um menu de opções para o usuário.
     *
     * @param titulo   O título do menu.
     * @param opcoes Uma array de strings contendo as opções do menu.
     */
    public static void exibirMenu(String titulo, String[] opcoes) {
        System.out.println("\n" + titulo);
        for (int i = 0; i < opcoes.length; i++) {
            System.out.printf("%d. %s%n", i + 1, opcoes[i]);
        }
        System.out.print("Escolha uma opção: ");
    }

    /**
     * Lê uma entrada de texto do usuário.
     *
     * @param mensagem A mensagem a ser exibida para o usuário antes da entrada.
     * @return A string digitada pelo usuário.
     */
    public static String lerString(String mensagem) {
        System.out.print(mensagem + ": ");
        return scanner.nextLine().trim();
    }

    /**
     * Lê um número inteiro do usuário. Valida se a entrada é um número válido.
     *
     * @param mensagem A mensagem a ser exibida para o usuário antes da entrada.
     * @return O número inteiro digitado pelo usuário.
     * @throws NumberFormatException Se a entrada do usuário não for um número inteiro válido.
     */
    public static int lerInteiro(String mensagem) {
        System.out.print(mensagem + ": ");
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada inválida. Digite um número inteiro.");
            System.out.print(mensagem + ": ");
            scanner.next(); // Consome a entrada inválida
        }
        int numero = scanner.nextInt();
        scanner.nextLine(); // Consome a quebra de linha
        return numero;
    }

    /**
     * Lê um número Long do usuário. Valida se a entrada é um número válido.
     *
     * @param mensagem A mensagem a ser exibida para o usuário antes da entrada.
     * @return O número Long digitado pelo usuário.
     * @throws NumberFormatException Se a entrada do usuário não for um número Long válido.
     */
    public static long lerLong(String mensagem) {
        System.out.print(mensagem + ": ");
        while (!scanner.hasNextLong()) {
            System.out.println("Entrada inválida. Digite um número.");
            System.out.print(mensagem + ": ");
            scanner.next(); // Consome a entrada inválida
        }
        long numero = scanner.nextLong();
        scanner.nextLine(); // Consome a quebra de linha
        return numero;
    }

    /**
     * Lê uma data de nascimento do usuário no formato dd/MM/yyyy. Valida o formato da data.
     *
     * @param mensagem A mensagem a ser exibida para o usuário antes da entrada.
     * @return Um objeto {@link LocalDate} representando a data de nascimento.
     * @throws DateTimeParseException Se a entrada do usuário não estiver no formato dd/MM/yyyy.
     */
    public static LocalDate lerData(String mensagem) {
        System.out.print(mensagem + " (dd/MM/yyyy): ");
        String dataString;
        LocalDate data = null;
        boolean dataValida = false;
        while (!dataValida) {
            dataString = scanner.nextLine().trim();
            try {
                data = LocalDate.parse(dataString, dateFormatter);
                dataValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Use dd/MM/yyyy.");
                System.out.print(mensagem + " (dd/MM/yyyy): ");
            }
        }
        return data;
    }

    /**
     * Formata um objeto {@link LocalDate} para exibição no formato dd/MM/yyyy.
     *
     * @param data O objeto {@link LocalDate} a ser formatado.
     * @return Uma string representando a data no formato dd/MM/yyyy.
     */
    public static String formatarData(LocalDate data) {
        if (data != null) {
            return data.format(dateFormatter);
        }
        return "";
    }

    /**
     * Converte um objeto Java para sua representação JSON formatada.
     *
     * @param objeto O objeto Java a ser convertido.
     * @return Uma string JSON formatada representando o objeto.
     * @throws RuntimeException Se ocorrer um erro durante a serialização para JSON.
     */
    public static String toJson(Object objeto) {
        try {
            return objectMapper.writeValueAsString(objeto);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao converter objeto para JSON", e);
        }
    }

    /**
     * Fecha o scanner utilizado para leitura do console.
     * É importante chamar este método ao finalizar o uso da interface de console.
     */
    public static void fecharScanner() {
        scanner.close();
    }
}