package ch.hearc.ig.orderresto.persistence.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseConnection {
    // Dans le cadre d'un projet collaboratif, les variables d'environnement paraissent plus appropriés.
    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USERNAME");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    // Instance de connexion unique - Singleton
    private static Connection connection;

    // Constructeur privé pour empêcher l'instanciation de cette classe
    private DataBaseConnection() {}

    /**
     * Obtention de la connexion, avec la gestion d'une seule instance.
     * Si la connexion est null ou close, elle en crée une nouvelle. Sinon elle réutilise la connexion existante.
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.put("user", USER);
        props.put("password", PASSWORD);

        // Permet la résolution des interruptions automatiques.
        props.put("oracle.net.CONNECT_TIMEOUT", "5000"); // Timeout pour connexion initiale
        props.put("oracle.jdbc.ReadTimeout", "10000");   // Timeout pour les lectures
        return DriverManager.getConnection(URL, props);
    }
}
