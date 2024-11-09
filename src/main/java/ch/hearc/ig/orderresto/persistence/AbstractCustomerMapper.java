package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractCustomerMapper {

    protected final IdentityMap<Customer> customerIdentityMap = new IdentityMap<>();
    protected final AddressMapper addressMapper = new AddressMapper();

    public abstract void insert(Customer customer) throws SQLException;

    public abstract void update(Customer customer) throws SQLException;

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM CLIENT WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
            customerIdentityMap.clear();
        }
    }

    public Customer findById(long id) throws SQLException {
        if (customerIdentityMap.contains(id)) {
            return customerIdentityMap.get(id);
        }

        String sql = "SELECT * FROM CLIENT WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = mapResultSetToCustomer(rs);
                    customerIdentityMap.put(id, customer);
                    return customer;
                }
            }
        }
        return null;
    }

    protected abstract Customer mapResultSetToCustomer(ResultSet rs) throws SQLException;

    protected void prepareStatementForCommonFields(PreparedStatement ps, Customer customer, int startIndex) throws SQLException {
        ps.setString(startIndex, customer.getEmail());
        ps.setString(startIndex + 1, customer.getPhone());
        ps.setString(startIndex + 2, customer.getAddress().getCountryCode());
        ps.setString(startIndex + 3, customer.getAddress().getPostalCode());
        ps.setString(startIndex + 4, customer.getAddress().getLocality());
        ps.setString(startIndex + 5, customer.getAddress().getStreet());
        ps.setString(startIndex + 6, customer.getAddress().getStreetNumber());
    }
}