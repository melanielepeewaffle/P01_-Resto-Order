package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.mapper.OrderMapper;
import ch.hearc.ig.orderresto.persistence.util.DataBaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderMapper orderMapper;

    public OrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public void createOrder(Order order) throws SQLException {
        try (Connection connection = DataBaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            orderMapper.insert(order);
            connection.commit();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la création de la commande.", e);
        }
    }

    public List<Order> getOrdersByCustomerId(Long customerId) throws SQLException {
        return orderMapper.findOrdersByCustomerId(customerId);
    }
}
