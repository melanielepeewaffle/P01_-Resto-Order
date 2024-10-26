package ch.hearc.ig.orderresto.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    // Dans le cadre d'un projet collaboratif, les variables d'environnement paraissent plus appropriés.
    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USERNAME");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    // Instance de connexion unique
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
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public static void commitTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
            connection.setAutoCommit(true); // Pour les prochaines opérations
        }
    }

    public static void rollbackTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
            connection.setAutoCommit(true); // Pour les prochaines opérations
        }
    }

    /**
     * Ferme explicitement la connexion et la réinitialise à null. Cela permet de garantir que toutes les ressources
     * sont libérées lorsque la connexion n'est plus nécessaire.
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }
}
