package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.mapper.OrganizationCustomerMapper;
import ch.hearc.ig.orderresto.persistence.mapper.PrivateCustomerMapper;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomerService {

    private final PrivateCustomerMapper privateCustomerMapper;
    private final OrganizationCustomerMapper organizationCustomerMapper;

    public CustomerService(PrivateCustomerMapper privateCustomerMapper, OrganizationCustomerMapper organizationCustomerMapper) {
        this.privateCustomerMapper = privateCustomerMapper;
        this.organizationCustomerMapper = organizationCustomerMapper;
    }

    public void createCustomer(Connection conn, Customer customer) throws SQLException {
        try {
            conn.setAutoCommit(false);

            if (customer instanceof PrivateCustomer) {
                privateCustomerMapper.insert(conn, customer);
            } else if (customer instanceof OrganizationCustomer) {
                organizationCustomerMapper.insert(conn, customer);
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException("Erreur lors de la création du client.", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public Customer findCustomerByEmail(Connection conn, String email) throws SQLException {
        Customer customer = privateCustomerMapper.findByEmail(conn, email);
        if (customer == null) {
            customer = organizationCustomerMapper.findByEmail(conn, email);
        }
        return customer;
    }
}