package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Address;

import java.sql.*;

public class AddressMapper {

    // Méthode pour insérer une nouvelle adresse dans la base de données
    public void insert(Address address) throws SQLException {
        String sql = "INSERT INTO ADRESSE (PAYS, CODE_POSTAL, LOCALITE, RUE, NUM_RUE) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            prepareStatementForAddress(ps, address);
            ps.executeUpdate();

            // Récupération de l'identifiant généré
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long generatedId = rs.getLong(1);
                    // Si vous avez besoin de stocker cet ID dans Address, vous pouvez ajouter un setter pour l'ID dans Address
                }
            }
        }
    }

    // Méthode pour mettre à jour une adresse existante
    public void update(long addressId, Address address) throws SQLException {
        String sql = "UPDATE ADRESSE SET PAYS = ?, CODE_POSTAL = ?, LOCALITE = ?, RUE = ?, NUM_RUE = ? WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForAddress(ps, address);
            ps.setLong(6, addressId); // Assurez-vous de placer l'ID à la fin
            ps.executeUpdate();
        }
    }

    // Méthode pour trouver une adresse par son identifiant
    public Address findById(long addressId) throws SQLException {
        String sql = "SELECT * FROM ADRESSE WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, addressId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAddress(rs);
                }
            }
        }
        return null; // Retourne null si l'adresse n'est pas trouvée
    }

    // Méthode pour supprimer une adresse par son identifiant
    public void delete(long addressId) throws SQLException {
        String sql = "DELETE FROM ADRESSE WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, addressId);
            ps.executeUpdate();
        }
    }

    // Prépare le PreparedStatement pour l'insertion et la mise à jour
    private void prepareStatementForAddress(PreparedStatement ps, Address address) throws SQLException {
        ps.setString(1, address.getCountryCode());
        ps.setString(2, address.getPostalCode());
        ps.setString(3, address.getLocality());
        ps.setString(4, address.getStreet());
        ps.setString(5, address.getStreetNumber());
    }

    // Méthode pour mapper le ResultSet à un objet Address
    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        return new Address(
                rs.getString("PAYS"),
                rs.getString("CODE_POSTAL"),
                rs.getString("LOCALITE"),
                rs.getString("RUE"),
                rs.getString("NUM_RUE")
        );
    }
}
