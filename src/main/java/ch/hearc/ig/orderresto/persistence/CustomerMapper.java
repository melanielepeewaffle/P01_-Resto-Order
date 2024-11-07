package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ch.hearc.ig.orderresto.persistence.OrganizationCustomerMapper.OrganizationCustomerTypeDB;
import static ch.hearc.ig.orderresto.persistence.PrivateCustomerMapper.PrivateCustomerTypeDB;

public class CustomerMapper {


    private List<Customer> select(String field, String value) {
        try (PreparedStatement ps = prepareStatementSelect(field)) {
            ps.setString(1, value);
            return executeQuerySelect(ps);
        } catch (SQLException e) {
            System.err.println(e.getErrorCode() + ": " + e.getMessage());
            return null;
        }
    }

    private List<Customer> select(String field, int value) {
        try (PreparedStatement ps = prepareStatementSelect(field)) {
            ps.setInt(1, value);
            return executeQuerySelect(ps);
        } catch (SQLException e) {
            System.err.println(e.getErrorCode() + ": " + e.getMessage());
            return null;
        }
    }

    private PreparedStatement prepareStatementSelect(String field) throws SQLException {
        String sql = "SELECT * FROM client WHERE " + field + " = ?";
        Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        return ps;
    }

    private List<Customer> executeQuerySelect(PreparedStatement ps) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                switch (rs.getString("TYPE")) {
                    case PrivateCustomerTypeDB:
                        customers.add(PrivateCustomerMapper.fromResultSet(rs));
                    case OrganizationCustomerTypeDB:
                        customers.add(OrganizationCustomerMapper.fromResultSet(rs));
                }
            }
        }
        return customers;
    }

    public Customer findById(int id) {
        List<Customer> result = select("NUMERO", id);
        if (result == null) {
            return null;
        }
        return result.get(0);
    }

    public List<Customer> findByEmail(String email) {
        return select("EMAIL", email);
    }

    public void delete(Customer customer) throws SQLException {
        String sql = "DELETE FROM CLIENT WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customer.getId());
            ps.executeUpdate();
        }
    }
}
