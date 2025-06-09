package com.escola.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit; // For clearer unit handling if needed

/**
 * Utility class for date-related operations within the application.
 * Provides static methods for calculating age, checking if a date falls within a recent period,
 * and other common date manipulations.
 * This class is designed to be a final utility class and cannot be instantiated or extended.
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public final class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     * All methods are static and should be called directly on the class.
     */
    private DateUtil() {
        // Utility classes should not be instantiated.
        // This private constructor prevents accidental instantiation.
    }

    /**
     * Calculates the age in the past years based on a given birthdate and the current date.
     * The current date is determined by {@link LocalDate#now()}.
     *
     * @param birthDate The {@link LocalDate} representing the person's birthdate. Must not be null or in the future.
     * @return The age in full years.
     * @throws IllegalArgumentException If {@code birthDate} is {@code null} or occurs after the current date.
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            logger.error("Attempted to calculate age with a null birthDate.");
            throw new IllegalArgumentException("A data de nascimento não pode ser nula."); // User-facing message
        }
        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today)) {
            logger.error("Attempted to calculate age with a future birthDate: {}. Current date: {}.", birthDate, today);
            throw new IllegalArgumentException("A data de nascimento não pode ser futura."); // User-facing message
        }

        int age = Period.between(birthDate, today).getYears();
        logger.debug("Calculated age: {} years for birthDate {}.", age, birthDate);
        return age;
    }

    /**
     * Checks if a given date falls within a specified number of past days relative to the current date.
     * The current date is determined by {@link LocalDate#now()}. The range includes the current date.
     * For example, if {@code daysAgo} is 30, it checks if the date is within the last 30 days, inclusive of today.
     *
     * @param dateToCheck The {@link LocalDate} to be verified. Must not be null.
     * @param daysAgo The number of days prior to the current date to consider for the period. Must not be negative.
     * @return {@code true} if {@code dateToCheck} is on or after (current_date - {@code daysAgo}) and on or before the current date;
     * {@code false} otherwise.
     * @throws IllegalArgumentException If {@code dateToCheck} is {@code null} or {@code daysAgo} is negative.
     */
    public static boolean isWithinLastDays(LocalDate dateToCheck, int daysAgo) {
        if (dateToCheck == null) {
            logger.error("Attempted to check date within last days with a null dateToCheck.");
            throw new IllegalArgumentException("A data não pode ser nula."); // User-facing message
        }
        if (daysAgo < 0) {
            logger.error("Attempted to check date within last days with negative daysAgo: {}.", daysAgo);
            throw new IllegalArgumentException("O número de dias anteriores não pode ser negativo."); // User-facing message
        }

        LocalDate today = LocalDate.now();
        // Calculate the start of the period (today minus daysAgo)
        // Using ChronoUnit.DAYS.addTo for explicit clarity, though minusDays also works
        LocalDate periodStart = today.minus(daysAgo, ChronoUnit.DAYS);

        // Check if dateToCheck is on or after the periodStart AND on or before today
        boolean isWithin = !dateToCheck.isBefore(periodStart) && !dateToCheck.isAfter(today);
        logger.debug("Checking if date '{}' is within last {} days. Period: [{}, {}]. Result: {}",
                dateToCheck, daysAgo, periodStart, today, isWithin);
        return isWithin;
    }

    // You can add other date utility methods here as needed, following the same pattern:
    // - Static methods
    // - Null/argument validation
    // - Use java.time classes
    // - Add comprehensive Javadocs
    // - Log internal operations
}