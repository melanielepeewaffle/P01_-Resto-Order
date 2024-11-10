package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.mapper.OrganizationCustomerMapper;
import ch.hearc.ig.orderresto.persistence.mapper.PrivateCustomerMapper;
import ch.hearc.ig.orderresto.persistence.util.DataBaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomerService {

    private final PrivateCustomerMapper privateCustomerMapper;
    private final OrganizationCustomerMapper organizationCustomerMapper;

    public CustomerService(PrivateCustomerMapper privateCustomerMapper, OrganizationCustomerMapper organizationCustomerMapper) {
        this.privateCustomerMapper = privateCustomerMapper;
        this.organizationCustomerMapper = organizationCustomerMapper;
    }

    public void createCustomer(Customer customer) throws SQLException {
        try (Connection connection = DataBaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            if (customer instanceof PrivateCustomer) {
                privateCustomerMapper.insert((PrivateCustomer) customer);
            } else if (customer instanceof OrganizationCustomer) {
                organizationCustomerMapper.insert((OrganizationCustomer) customer);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la création du client.", e);
        }
    }

    public Customer findCustomerByEmail(String email) throws SQLException {
        Customer customer = privateCustomerMapper.findByEmail(email);
        if (customer == null) {
            customer = organizationCustomerMapper.findByEmail(email);
        }
        return customer;
    }
}