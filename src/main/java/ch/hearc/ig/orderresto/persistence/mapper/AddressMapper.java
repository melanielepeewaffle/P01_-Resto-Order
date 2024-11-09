package ch.hearc.ig.orderresto.persistence.mapper;

import ch.hearc.ig.orderresto.business.Address;

import java.sql.*;

public class AddressMapper {

    /**
     * Insérer ou mettre à jour les champs d'adresse dans une entité parente (Restaurant, Client)
     * @param ps
     * @param address
     * @param startIndex Pour définir les paramètres SQL nécessaire pour l'adresse des autres entités parentes.
     * @throws SQLException
     */
    public void prepareStatementForAddress(PreparedStatement ps, Address address, int startIndex) throws SQLException {
        ps.setString(startIndex, address.getCountryCode());
        ps.setString(startIndex + 1, address.getPostalCode());
        ps.setString(startIndex + 2, address.getLocality());
        ps.setString(startIndex + 3, address.getStreet());
        ps.setString(startIndex + 4, address.getStreetNumber());
    }

    /**
     * Mappe le ResultSet d'une entité parente (Restaurant, Client) pour remplir l'objet Address
     * @param rs
     * @param startIndex Pour définir les paramètres SQL nécessaire pour l'adresse des autres entités parentes.
     * @return Une nouvelle adresse
     * @throws SQLException
     */
    public Address mapResultSetToAddress(ResultSet rs, int startIndex) throws SQLException {
        return new Address(
                rs.getString(startIndex),
                rs.getString(startIndex + 1),
                rs.getString(startIndex + 2),
                rs.getString(startIndex + 3),
                rs.getString(startIndex + 4)
        );
    }
}
