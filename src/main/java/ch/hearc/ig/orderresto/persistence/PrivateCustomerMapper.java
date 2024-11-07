package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.PrivateCustomer;

import java.sql.*;

public class PrivateCustomerMapper {

    public static final String PrivateCustomerTypeDB = "P";

    public static PrivateCustomer fromResultSet(ResultSet rs) throws SQLException {
        return new PrivateCustomer(
                rs.getLong("NUMERO"),
                rs.getString("TELEPHONE"),
                rs.getString("EMAIL"),
                new Address(
                        rs.getString("PAYS"),
                        rs.getString("CODE_POSTAL"),
                        rs.getString("LOCALITE"),
                        rs.getString("RUE"),
                        rs.getString("NUM_RUE")
                ),
                rs.getString("EST_UNE_FEMME"),
                rs.getString("PRENOM"),
                rs.getString("NOM")
        );
    }

    private void prepareStatementForPrivateCustomer(PreparedStatement ps, PrivateCustomer customer) throws SQLException {
        ps.setString(1, customer.getEmail());
        ps.setString(2, customer.getPhone());
        ps.setString(3, customer.getLastName());
        ps.setString(4, customer.getAddress().getPostalCode());
        ps.setString(5, customer.getAddress().getLocality());
        ps.setString(6, customer.getAddress().getStreet());
        ps.setString(7, customer.getAddress().getStreetNumber());
        ps.setString(8, customer.getAddress().getCountryCode());
        ps.setString(9, customer.getGender());
        ps.setString(10, customer.getFirstName());
        ps.setString(11, PrivateCustomerTypeDB);
    }


    public void insert(PrivateCustomer customer) throws SQLException {
        String sql = "INSERT INTO CLIENT (NUMERO, EMAIL, TELEPHONE, NOM, CODE_POSTAL, LOCALITE, RUE, NUM_RUE, PAYS, EST_UNE_FEMME, PRENOM, TYPE) VALUES (SEQ_CLIENT.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForPrivateCustomer(ps, customer);
            ps.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT SEQ_CLIENT.CURRVAL FROM DUAL")) {
                if (rs.next()) {
                    customer.setId(rs.getLong(1));
                }
            }
        }
    }

    public void update(PrivateCustomer customer) throws SQLException {
        String sql = "UPDATE CLIENT SET EMAIL = ?, TELEPHONE = ?, NOM = ?, CODE_POSTAL = ?, LOCALITE = ?, RUE = ?, NUM_RUE = ?, PAYS = ?, EST_UNE_FEMME = ?, PRENOM = ?, TYPE = ? WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForPrivateCustomer(ps, customer);
            ps.setLong(12, customer.getId());
            ps.executeUpdate();
        }
    }

}
