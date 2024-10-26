package ch.hearc.ig.orderresto.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseUtils {
    /**
     * Méthode pour l'obtention de la clé générée.
     *
     * EX1 : Comment générer les identifiants techniques (PK) et faire en sorte qu'ils soient présents dans les objets
     *       après leur création ?
     * --> Centralisation de la récupération des clés générées afin de l'utiliser dans les différents Mapper pour
     *     simplifier et uniformiser la logique. Les ID générés sont ensuite mis à jour dans IdentityMap pour garantir
     *     l'unicité en mémoire.
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
