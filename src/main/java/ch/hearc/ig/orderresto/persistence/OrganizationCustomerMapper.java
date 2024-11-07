package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;

import java.sql.*;

public class OrganizationCustomerMapper {
    public static final String OrganizationCustomerTypeDB = "O";

    public static OrganizationCustomer fromResultSet(ResultSet rs) throws SQLException {
        return new OrganizationCustomer(
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
                rs.getString("NOM"),
                rs.getString("FORME_SOCIALE")
        );
    }

    private void prepareStatementForOrganizationCustomer(PreparedStatement ps, OrganizationCustomer customer) throws SQLException {
        ps.setString(1, customer.getEmail());
        ps.setString(2, customer.getPhone());
        ps.setString(3, customer.getName());
        ps.setString(4, customer.getAddress().getPostalCode());
        ps.setString(5, customer.getAddress().getLocality());
        ps.setString(6, customer.getAddress().getStreet());
        ps.setString(7, customer.getAddress().getStreetNumber());
        ps.setString(8, customer.getAddress().getCountryCode());
        ps.setString(9, customer.getLegalForm());
        ps.setString(10, OrganizationCustomerTypeDB);
    }

    public void insert(OrganizationCustomer customer) throws SQLException {
        String sql = "INSERT INTO CLIENT (NUMERO, EMAIL, TELEPHONE, NOM, CODE_POSTAL, LOCALITE, RUE, NUM_RUE, PAYS, FORME_SOCIALE, TYPE) VALUES (SEQ_CLIENT.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForOrganizationCustomer(ps, customer);
            ps.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT SEQ_CLIENT.CURRVAL FROM DUAL")) {
                if (rs.next()) {
                    customer.setId(rs.getLong(1));
                }
            }
        }
    }

    public void update(OrganizationCustomer customer) throws SQLException {
        String sql = "UPDATE CLIENT SET EMAIL = ?, TELEPHONE = ?, NOM = ?, CODE_POSTAL = ?, LOCALITE = ?, RUE = ?, NUM_RUE = ?, PAYS = ?, FORME_SOCIALE = ?, TYPE = ? WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForOrganizationCustomer(ps, customer);
            ps.setLong(11, customer.getId());
            ps.executeUpdate();
        }
    }
}
