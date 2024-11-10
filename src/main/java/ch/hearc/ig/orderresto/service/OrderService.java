package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.mapper.OrderMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderMapper orderMapper;

    public OrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public void createOrder(Connection conn, Order order) throws SQLException {
        try {
            conn.setAutoCommit(false);
            orderMapper.insert(conn, order);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException("Erreur lors de la création de la commande.", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Order> getOrdersByCustomerId(Connection conn, Long customerId) throws SQLException {
        return orderMapper.findOrdersByCustomerId(conn, customerId);
    }
}