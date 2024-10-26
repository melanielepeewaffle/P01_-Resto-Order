package ch.hearc.ig.orderresto.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseUtils {
    /**
     * Méthode pour l'obtention de la clé générée
     * @param rs
     * @return
     * @throws SQLException
     */
    public static long getGeneratedKey(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return rs.getLong(1);
        } else {
            throw new SQLException("No generated key returned by the database.");
        }
    }
}
