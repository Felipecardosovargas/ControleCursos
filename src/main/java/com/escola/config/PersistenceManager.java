package com.escola.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the JPA EntityManagerFactory and provides EntityManager instances.
 * This class follows the Singleton pattern to ensure that only one instance of
 * EntityManagerFactory is created and maintained throughout the application's lifecycle.
 * Database connection configurations are read from environment variables,
 * promoting flexibility and security for credentials.
 * <p>
 * It is crucial to call the {@link #close()} method when the application shuts down
 * to correctly release database resources and prevent leaks.
 * </p>
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public final class PersistenceManager {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class); // Initializes the logger
    private static EntityManagerFactory emf;
    private static final String PERSISTENCE_UNIT_NAME = "escolaPU";
    private static final String DEFAULT_HBM2DDL_AUTO = "update"; // Default value for hibernate.hbm2ddl.auto

    /**
     * Private constructor to enforce the Singleton pattern.
     * Prevents direct instantiation of this class.
     */
    private PersistenceManager() {}

    /**
     * Returns the unique instance of the EntityManagerFactory.
     * If the EntityManagerFactory has not yet been initialized, this method creates it
     * using environment variables for database connection configuration
     * and Hibernate properties.
     * <p>
     * This method is thread-safe due to the `synchronized` keyword, ensuring
     * that the EntityManagerFactory is initialized only once even under concurrent access.
     * </p>
     *
     * @return The singleton instance of EntityManagerFactory.
     * @throws RuntimeException if the environment variables DB_URL, DB_USER, or DB_PASSWORD are not defined,
     * or if a failure occurs during the creation of the EntityManagerFactory.
     */
    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            try {
                Map<String, String> props = new HashMap<>();

                // Reads environment variables for database connection configuration
                String url = System.getenv("DB_URL");
                String user = System.getenv("DB_USER");
                String password = System.getenv("DB_PASSWORD");

                if (url == null || url.isEmpty()) {
                    logger.error("Environment variable DB_URL is not defined or is empty.");
                    throw new RuntimeException("Environment variable DB_URL not defined or empty.");
                }
                if (user == null || user.isEmpty()) {
                    logger.error("Environment variable DB_USER is not defined or is empty.");
                    throw new RuntimeException("Environment variable DB_USER not defined or empty.");
                }

                String env = System.getenv("APP_ENV");

                // Special handling for DB_PASSWORD in 'dev' environment
                if (password == null || password.isEmpty()) {
                    if (!"dev".equals(env)) {
                        logger.error("Environment variable DB_PASSWORD is not defined for non-dev environment.");
                        throw new RuntimeException("Environment variable DB_PASSWORD is not defined.");
                    } else {
                        password = ""; // Allow empty password for 'dev' environment
                        logger.warn("DB_PASSWORD is not set. Using empty password for 'dev' environment.");
                    }
                }

                props.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
                props.put("javax.persistence.jdbc.url", url);
                props.put("javax.persistence.jdbc.user", user);
                props.put("javax.persistence.jdbc.password", password);

                // Hibernate configurations
                props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                // 'update' for development, 'validate' or 'none' for production
                String hbm2ddlAuto = System.getenv("HIBERNATE_HBM2DDL_AUTO");
                if (hbm2ddlAuto == null || hbm2ddlAuto.isEmpty()) {
                    hbm2ddlAuto = DEFAULT_HBM2DDL_AUTO; // Default to 'update' for development convenience
                    logger.info("HIBERNATE_HBM2DDL_AUTO not set. Defaulting to '{}'.", DEFAULT_HBM2DDL_AUTO);
                }
                props.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
                props.put("hibernate.show_sql", "true");
                props.put("hibernate.format_sql", "true"); // Adds formatting for displayed SQL

                logger.info("Attempting to create EntityManagerFactory with persistence unit: {}", PERSISTENCE_UNIT_NAME);
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
                logger.info("EntityManagerFactory created successfully.");
            } catch (Exception e) {
                // Logs the error and rethrows a more descriptive RuntimeException
                logger.error("Failed to create EntityManagerFactory: {}", e.getMessage(), e);
                throw new RuntimeException("Fatal error during persistence initialization.", e);
            }
        }
        return emf;
    }

    /**
     * Returns a new instance of EntityManager.
     * Each EntityManager is a unit of work and is not thread-safe.
     * It should be obtained for each persistence operation (or logical transaction)
     * and closed after use to release resources.
     *
     * @return A new instance of EntityManager.
     * @throws RuntimeException if the EntityManagerFactory cannot be obtained (e.g., failed to initialize).
     */
    public static EntityManager getEntityManager() {
        // Delegates to getEntityManagerFactory() to ensure EMF is initialized.
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Closes the EntityManagerFactory, releasing all associated database resources.
     * This method must be called when the application is shutting down to ensure a clean exit.
     * It is safe to call this method multiple times; it will only act if the EntityManagerFactory is open.
     * All open EntityManagers created from this factory will also be closed.
     */
    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null; // Sets emf to null to allow re-initialization if needed
            logger.info("EntityManagerFactory closed and resources released.");
        } else {
            logger.warn("Attempt to close EntityManagerFactory that is already closed or was not initialized.");
        }
    }
}