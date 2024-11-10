package ch.hearc.ig.orderresto.persistence.mapper;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.business.Customer;
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

    public void insert(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO COMMANDE (NUMERO, FK_CLIENT, FK_RESTO, A_EMPORTER, QUAND) VALUES (SEQ_COMMANDE.NEXTVAL, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            prepareStatementForOrder(ps, order, 1);
            ps.executeUpdate();

            long generatedId = DataBaseUtils.getGeneratedKey(conn, "SEQ_COMMANDE");
            order.setId(generatedId);
            orderIdentityMap.put(generatedId, order);
        }

        insertOrderProducts(conn, order);
    }

    private void insertOrderProducts(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO PRODUIT_COMMANDE (FK_COMMANDE, FK_PRODUIT) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Product product : order.getProducts()) {
                ps.setLong(1, order.getId());
                ps.setLong(2, product.getId());
                ps.executeUpdate();
            }
        }
    }

    public void update(Connection conn, Order order) throws SQLException {
        String sql = "UPDATE COMMANDE SET FK_CLIENT = ?, FK_RESTO = ?, A_EMPORTER = ?, QUAND = ? WHERE NUMERO = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int nextIndex = prepareStatementForOrder(ps, order, 1);
            ps.setLong(nextIndex, order.getId());
            ps.executeUpdate();

            orderIdentityMap.put(order.getId(), order);
        }

        updateOrderProducts(conn, order);
    }

    private void updateOrderProducts(Connection conn, Order order) throws SQLException {
        String sql = "DELETE FROM PRODUIT_COMMANDE WHERE FK_COMMANDE = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, order.getId());
            ps.executeUpdate();
        }

        insertOrderProducts(conn, order);
    }

    public void delete(Connection conn, Long id) throws SQLException {
        String sql = "DELETE FROM COMMANDE WHERE NUMERO = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();

            orderIdentityMap.remove(id);
        }
    }

    public List<Order> findOrdersByCustomerId(Connection conn, long customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM COMMANDE WHERE FK_CLIENT = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long orderId = rs.getLong("NUMERO");

                    if (orderIdentityMap.contains(orderId)) {
                        orders.add(orderIdentityMap.get(orderId));
                    } else {
                        Order order = mapResultSetToOrder(conn, rs);
                        orderIdentityMap.put(orderId, order);
                        orders.add(order);
                    }
                }
            }
        }

        return orders;
    }

    public List<Order> findAll(Connection conn) throws SQLException {
        List<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM COMMANDE";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long orderId = rs.getLong("NUMERO");
                if (orderIdentityMap.contains(orderId)) {
                    orders.add(orderIdentityMap.get(orderId));
                } else {
                    Order order = mapResultSetToOrder(conn, rs);
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

    private Order mapResultSetToOrder(Connection conn, ResultSet rs) throws SQLException {
        long clientId = rs.getLong("FK_CLIENT");
        String clientType = privateCustomerMapper.getClientType(conn, clientId);
        Customer customer = "P".equals(clientType) ?
                privateCustomerMapper.findById(conn, clientId) :
                organizationCustomerMapper.findById(conn, clientId);

        long restaurantId = rs.getLong("FK_RESTO");
        Restaurant restaurant = restaurantMapper.findById(conn, restaurantId);

        Order order = new Order(
                rs.getLong("NUMERO"),
                customer,
                restaurant,
                "O".equals(rs.getString("A_EMPORTER")),
                rs.getTimestamp("QUAND").toLocalDateTime()
        );

        List<Product> products = productMapper.findProductByOrderId(conn, rs.getLong("NUMERO"));
        for (Product product : products) {
            order.addProduct(product);
        }

        return order;
    }
}