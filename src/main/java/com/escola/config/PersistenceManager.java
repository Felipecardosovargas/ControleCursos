package com.escola.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gerencia o JPA EntityManagerFactory e fornece instâncias de EntityManager.
 * Esta classe segue o padrão Singleton para garantir que apenas uma instância de
 * EntityManagerFactory seja criada e mantida durante o ciclo de vida da aplicação.
 * As configurações de conexão com o banco de dados são lidas a partir de variáveis de ambiente,
 * promovendo a flexibilidade e segurança das credenciais.
 * <p>
 * É crucial chamar o método {@link #close()} quando a aplicação é encerrada
 * para liberar corretamente os recursos do banco de dados e evitar vazamentos.git push
 * </p>
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public final class PersistenceManager {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class); // Inicializa o logger
    private static EntityManagerFactory emf;
    private static final String PERSISTENCE_UNIT_NAME = "escolaPU";

    /**
     * Construtor privado para enforcement do padrão Singleton.
     * Impede a criação de instâncias diretas da classe.
     */
    private PersistenceManager() {}

    /**
     * Retorna a instância única do EntityManagerFactory.
     * Se o EntityManagerFactory ainda não foi inicializado, este método o cria
     * usando as variáveis de ambiente para configuração da conexão com o banco de dados
     * e as propriedades do Hibernate.
     *
     * @return A instância do EntityManagerFactory.
     * @throws RuntimeException se as variáveis de ambiente DB_URL, DB_USER ou DB_PASSWORD não estiverem definidas,
     * ou se ocorrer uma falha durante a criação do EntityManagerFactory.
     */
    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            try {
                Map<String, String> props = new HashMap<>();

                // Lê as variáveis de ambiente para configuração da conexão com o banco de dados
                String url = System.getenv("DB_URL");
                String user = System.getenv("DB_USER");
                String password = System.getenv("DB_PASSWORD");

                if (url == null || url.isEmpty()) {
                    throw new RuntimeException("Variável de ambiente DB_URL não definida ou vazia.");
                }
                if (user == null || user.isEmpty()) {
                    throw new RuntimeException("Variável de ambiente DB_USER não definida ou vazia.");
                }

                String env = System.getenv("APP_ENV");

                if (password == null || password.isEmpty()) {
                    if (!"dev".equals(env)) {
                        throw new RuntimeException("A variável de ambiente DB_PASSWORD não está definida.");
                    } else {
                        password = "";
                    }
                }

                props.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
                props.put("javax.persistence.jdbc.url", url);
                props.put("javax.persistence.jdbc.user", user);
                props.put("javax.persistence.jdbc.password", password);

                // Configurações do Hibernate
                props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                // 'update' para desenvolvimento, 'validate' ou 'none' para produção
                String hbm2ddlAuto = System.getenv("HIBERNATE_HBM2DDL_AUTO");
                if (hbm2ddlAuto == null || hbm2ddlAuto.isEmpty()) {
                    hbm2ddlAuto = "update"; // Padrão para desenvolvimento
                }
                props.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
                props.put("hibernate.show_sql", "true");
                props.put("hibernate.format_sql", "true"); // Adiciona formatação para SQL exibido

                logger.info("Tentando criar EntityManagerFactory com a unidade de persistência: {}", PERSISTENCE_UNIT_NAME);
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
                logger.info("EntityManagerFactory criado com sucesso.");
            } catch (Exception e) {
                logger.error("Falha ao criar EntityManagerFactory: {}", e.getMessage(), e);
                throw new RuntimeException("Erro fatal na inicialização da persistência.", e);
            }
        }
        return emf;
    }

    /**
     * Retorna uma nova instância de EntityManager.
     * Cada EntityManager é uma unidade de trabalho e não é thread-safe.
     * Ele deve ser obtido para cada operação de persistência e fechado após o uso.
     *
     * @return Uma nova instância de EntityManager.
     * @throws RuntimeException se o EntityManagerFactory não puder ser obtido.
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Fecha o EntityManagerFactory, liberando todos os recursos associados ao banco de dados.
     * Este método deve ser chamado quando a aplicação é encerrada para garantir um desligamento limpo.
     * É seguro chamar este método múltiplas vezes; ele só agirá se o EntityManagerFactory estiver aberto.
     */
    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
            logger.info("EntityManagerFactory fechado e recursos liberados.");
        } else {
            logger.warn("Tentativa de fechar EntityManagerFactory que já está fechado ou não foi inicializado.");
        }
    }
}