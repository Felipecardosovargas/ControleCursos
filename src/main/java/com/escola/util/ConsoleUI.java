package com.escola.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException; // Specific exception for scanner
import java.util.Scanner;

/**
 * Utility class for console interactions in the application.
 * Provides static methods to display menus, read various types of user input
 * (strings, integers, longs, dates), format dates, and serialize objects to JSON
 * for console display/debugging.
 * This class is designed to be a singleton utility and cannot be instantiated.
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public final class ConsoleUI {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleUI.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Register module for Java 8 Date/Time API support
            .enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty-printing JSON

    /**
     * Private constructor to prevent instantiation of this utility class.
     * All methods are static and can be called directly on the class.
     */
    private ConsoleUI() {
        // Private constructor to enforce non-instantiability.
        // Utility classes should not be instantiated.
    }

    /**
     * Displays a menu of options to the user on the console.
     * Each option is numbered sequentially starting from 1.
     *
     * @param title The title of the menu to be displayed at the top.
     * @param options An array of strings, where each string represents a menu option.
     */
    public static void displayMenu(String title, String[] options) {
        System.out.println("\n" + title); // Add a newline for better readability
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s%n", i + 1, options[i]); // Prints option number and text
        }
        System.out.print("Escolha uma opção: "); // User prompt for selection
        //logger.debug("Menu displayed: '{}' with {} options.", title, options.length);
    }

    /**
     * Reads a line of text input from the user.
     * The method trims leading and trailing whitespace from the input.
     *
     * @param prompt The message to display to the user before awaiting input.
     * @return The string entered by the user.
     */
    public static String readString(String prompt) {
        System.out.print(prompt + "> ");
        String input = scanner.nextLine().trim();
        logger.debug("Read string input: '{}'", input);
        return input;
    }

    /**
     * Reads an integer number from the user.
     * The method includes a loop to repeatedly prompt the user until a valid integer is entered.
     * Non-integer inputs are caught and a warning message is displayed.
     *
     * @param prompt The message to display to the user before awaiting input.
     * @return The valid integer number entered by the user.
     */
    public static int readInt(String prompt) {
        System.out.print(prompt + "> ");
        int number;
        while (true) { // Loop until a valid integer is read
            try {
                number = scanner.nextInt();
                scanner.nextLine(); // Consume the rest of the line (newline character)
                logger.debug("Read integer input: {}", number);
                return number;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número inteiro."); // User-facing error
                logger.warn("Invalid integer input received. Prompting again.", e);
                scanner.nextLine(); // Consume the invalid input to prevent infinite loop
                System.out.print(prompt + "> ");
            }
        }
    }

    /**
     * Reads a long integer number from the user.
     * The method includes a loop to repeatedly prompt the user until a valid long is entered.
     * Non-long inputs are caught and a warning message is displayed.
     *
     * @param prompt The message to display to the user before awaiting input.
     * @return The valid long number entered by the user.
     */
    public static long readLong(String prompt) {
        System.out.print(prompt + ": ");
        long number;
        while (true) { // Loop until a valid long is read
            try {
                number = scanner.nextLong();
                scanner.nextLine(); // Consume the rest of the line (newline character)
                logger.debug("Read long input: {}", number);
                return number;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número."); // User-facing error
                logger.warn("Invalid long input received. Prompting again.", e);
                scanner.nextLine(); // Consume the invalid input to prevent infinite loop
                System.out.print(prompt + "> ");
            }
        }
    }

    /**
     * Reads a date from the user in the "dd/MM/yyyy" format.
     * The method repeatedly prompts the user until a valid date in the specified format is entered.
     * Uses {@link DateTimeFormatter} for parsing and {@link DateTimeParseException} for validation.
     *
     * @param prompt The message to display to the user before awaiting input.
     * @return A {@link LocalDate} object representing the parsed date.
     */
    public static LocalDate readDate(String prompt) {
        System.out.print(prompt + " (dd/MM/yyyy): ");
        LocalDate date = null;
        while (date == null) { // Loop until a valid date is parsed
            String dateString = scanner.nextLine().trim();
            try {
                date = LocalDate.parse(dateString, dateFormatter);
                logger.debug("Read date input: '{}'", date);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Use dd/MM/yyyy."); // User-facing error
                logger.warn("Invalid date format received: '{}'. Prompting again.", dateString, e);
                System.out.print(prompt + " (dd/MM/yyyy): ");
            }
        }
        return date;
    }

    /**
     * Formats a {@link LocalDate} object into a string representation using the "dd/MM/yyyy" pattern.
     * If the input date is null, an empty string is returned.
     *
     * @param date The {@link LocalDate} object to be formatted.
     * @return A string representing the formatted date, or an empty string if the input date is null.
     */
    public static String formatDate(LocalDate date) {
        if (date != null) {
            String formattedDate = date.format(dateFormatter);
            logger.debug("Formatted date '{}' to '{}'.", date, formattedDate);
            return formattedDate;
        }
        logger.debug("Attempted to format a null date. Returning empty string.");
        return "";
    }

    /**
     * Converts any Java object to its pretty-printed JSON string representation.
     * This utility uses Jackson's ObjectMapper configured with JavaTimeModule for LocalDate/LocalDateTime support
     * and indentation for readability.
     *
     * @param object The Java object to be converted to JSON.
     * @return A formatted JSON string representing the object.
     * @throws RuntimeException If an {@link IOException} occurs during the serialization process,
     * wrapping the original exception.
     */
    public static String toJson(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            logger.debug("Converted object of type {} to JSON.", object.getClass().getSimpleName());
            return json;
        } catch (IOException e) {
            logger.error("Error converting object to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao converter objeto para JSON", e); // User-facing message in Portuguese
        }
    }

    /**
     * Closes the {@link Scanner} instance used for console input.
     * This method should be called when the console UI interaction is finished
     * to release system resources associated with the scanner.
     */
    public static void closeScanner() {
        scanner.close();
        logger.info("Console scanner closed.");
    }
}