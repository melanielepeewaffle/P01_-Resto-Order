package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static ch.hearc.ig.orderresto.persistence.OrganizationCustomerMapper.OrganizationCustomerTypeDB;
import static ch.hearc.ig.orderresto.persistence.PrivateCustomerMapper.PrivateCustomerTypeDB;

public class CustomerMapper {

    public Customer getCustomerByEmail(String email) {
        Customer customer = null;
        String sql = "SELECT * FROM client WHERE EMAIL = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    customer = generateCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getErrorCode() + ": " + e.getMessage());
        }
        return customer;
    }

    private Customer generateCustomer(ResultSet rs) throws SQLException {
        switch (rs.getString("TYPE")) {
            case PrivateCustomerTypeDB:
                return PrivateCustomerMapper.fromResultSet(rs);
            case OrganizationCustomerTypeDB:
                return OrganizationCustomerMapper.fromResultSet(rs);
            default:
                return null;
        }
    }
}
