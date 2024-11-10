package ch.hearc.ig.orderresto.persistence.mapper;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.persistence.util.DataBaseConnection;
import ch.hearc.ig.orderresto.persistence.util.DataBaseUtils;
import ch.hearc.ig.orderresto.persistence.util.IdentityMap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    private final IdentityMap<Order> orderIdentityMap = new IdentityMap<>();
    private final PrivateCustomerMapper privateCustomerMapper = new PrivateCustomerMapper();
    private final OrganizationCustomerMapper organizationCustomerMapper = new OrganizationCustomerMapper();
    private final RestaurantMapper restaurantMapper;
    private final ProductMapper productMapper;

    public OrderMapper(RestaurantMapper restaurantMapper, ProductMapper productMapper) {
        this.restaurantMapper = restaurantMapper;
        this.productMapper = productMapper;
    }

    public void insert(Order order) throws SQLException {
        String sql = "INSERT INTO COMMANDE (NUMERO, FK_CLIENT, FK_RESTO, A_EMPORTER, QUAND) VALUES (SEQ_COMMANDE.NEXTVAL, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForOrder(ps, order, 1);
            ps.executeUpdate();

            long generatedId = DataBaseUtils.getGeneratedKey(conn, "SEQ_COMMANDE");
            order.setId(generatedId);
            orderIdentityMap.put(generatedId, order);

            insertOrderProducts(order, conn);
        }
    }

    private void insertOrderProducts(Order order, Connection conn) throws SQLException {
        String sql = "INSERT INTO PRODUIT_COMMANDE (FK_COMMANDE, FK_PRODUIT) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Product product : order.getProducts()) {
                ps.setLong(1, order.getId());
                ps.setLong(2, product.getId());
                ps.executeUpdate();
            }
        }
    }

    public void update(Order order) throws SQLException {
        String sql = "UPDATE COMMANDE SET FK_CLIENT = ?, FK_RESTO = ?, A_EMPORTER = ?, QUAND = ? WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int nextIndex = prepareStatementForOrder(ps, order, 1);
            ps.setLong(nextIndex, order.getId());
            ps.executeUpdate();

            updateOrderProducts(order, conn);
            orderIdentityMap.put(order.getId(), order);
        }
    }

    private void updateOrderProducts(Order order, Connection conn) throws SQLException {
        String deleteSql = "DELETE FROM PRODUIT_COMMANDE WHERE FK_COMMANDE = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setLong(1, order.getId());
            ps.executeUpdate();
        }
        insertOrderProducts(order, conn);
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM COMMANDE WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
            orderIdentityMap.clear();
        }
    }

    public Order findById(long id) throws SQLException {
        if (orderIdentityMap.contains(id)) {
            return orderIdentityMap.get(id);
        }

        String sql = "SELECT * FROM COMMANDE WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    orderIdentityMap.put(id, order);
                    return order;
                }
            }
        }
        return null;
    }

    public List<Order> findOrdersByCustomerId(long customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM COMMANDE WHERE FK_CLIENT = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long orderId = rs.getLong("NUMERO");

                    if (orderIdentityMap.contains(orderId)) {
                        orders.add(orderIdentityMap.get(orderId));
                    } else {
                        Order order = mapResultSetToOrder(rs);
                        orderIdentityMap.put(orderId, order);
                        orders.add(order);
                    }
                }
            }
        }
        return orders;
    }

    public List<Order> findAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM COMMANDE";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long orderId = rs.getLong("NUMERO");
                if (orderIdentityMap.contains(orderId)) {
                    orders.add(orderIdentityMap.get(orderId));
                } else {
                    Order order = mapResultSetToOrder(rs);
                    orderIdentityMap.put(orderId, order);
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    private int prepareStatementForOrder(PreparedStatement ps, Order order, int startIndex) throws SQLException {
        ps.setLong(startIndex, order.getCustomer().getId());
        ps.setLong(startIndex + 1, order.getRestaurant().getId());
        ps.setString(startIndex + 2, order.getTakeAway() ? "O" : "N");
        ps.setTimestamp(startIndex + 3, Timestamp.valueOf(order.getWhen()));
        return startIndex + 4;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        long clientId = rs.getLong("FK_CLIENT");
        String clientType = getClientType(clientId);
        Customer customer = "P".equals(clientType) ?
                privateCustomerMapper.findById(clientId) :
                organizationCustomerMapper.findById(clientId);

        long restaurantId = rs.getLong("FK_RESTO");
        Restaurant restaurant = restaurantMapper.findById(restaurantId);

        Order order = new Order(
                rs.getLong("NUMERO"),
                customer,
                restaurant,
                "O".equals(rs.getString("A_EMPORTER")),
                rs.getTimestamp("QUAND").toLocalDateTime()
        );

        List<Product> products = productMapper.findProductByOrderId(rs.getLong("NUMERO"));
        for (Product product : products) {
            order.addProduct(product);
        }

        return order;
    }

    private String getClientType(long clientId) throws SQLException {
        String sql = "SELECT TYPE FROM CLIENT WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("TYPE");
                }
            }
        }
        return null;
    }
}