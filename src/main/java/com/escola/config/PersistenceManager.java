package com.escola.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the JPA EntityManagerFactory and provides EntityManager instances.
 * This class follows the Singleton pattern to ensure only one instance of
 * EntityManagerFactory is created.
 * <p>
 * It is crucial to call {@link #close()} when the application shuts down
 * to release database resources.
 * </p>
 *
 * @version 1.0
 */
public final class PersistenceManager {

    private static EntityManagerFactory emf;
    private static final String PERSISTENCE_UNIT_NAME = "escolaPU";

    private PersistenceManager() {}

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            try {
                Map<String, String> props = new HashMap<>();

                // Lê as variáveis de ambiente
                String url = System.getenv("DB_URL");
                String user = System.getenv("DB_USER");
                String password = System.getenv("DB_PASSWORD");

                if (url == null || user == null || password == null) {
                    throw new RuntimeException("Variáveis de ambiente DB_URL, DB_USER ou DB_PASSWORD não definidas.");
                }

                props.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
                props.put("javax.persistence.jdbc.url", url);
                props.put("javax.persistence.jdbc.user", user);
                props.put("javax.persistence.jdbc.password", password);

                // Configurações Hibernate
                props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                props.put("hibernate.hbm2ddl.auto", "update");
                props.put("hibernate.show_sql", "true");

                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
            } catch (Exception e) {
                System.err.println("Falha ao criar EntityManagerFactory: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
            System.out.println("EntityManagerFactory fechado.");
        }
    }
}
