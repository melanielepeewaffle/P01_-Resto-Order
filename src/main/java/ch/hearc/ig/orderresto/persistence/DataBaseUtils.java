package ch.hearc.ig.orderresto.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseUtils {
    /**
     * Méthode pour l'obtention de la clé générée via la séquence spécifiée.
     * Permet de simplifier la logique pour récupérer les IDs.
     * @param conn
     * @param sequenceName
     * @return
     * @throws SQLException
     */
    public static long getGeneratedKey(Connection conn, String sequenceName) throws SQLException {
        String sql = "SELECT " + sequenceName + ".CURRVAL FROM DUAL";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("No generated key returned by the database.");
            }
        }
    }
}
