package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Classe de mappage pour gérer les opérations CRUD sur les commandes (Order).
 */
public class OrderMapper {

    private final IdentityMap<Order> orderIdentityMap = new IdentityMap<>();

    // Insertion d'une commande sans produits
    public void insert(Order order) throws SQLException {
        String sql  = "INSERT INTO COMMANDE (NUMERO, FK_CLIENT, FK_RESTO, A_EMPORTER, QUAND, TOTALAMOUNT) VALUES (SEQ_ORDER.NEXTVAL, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForOrder(ps, order);
            ps.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT SEQ_ORDER.CURRVAL FROM DUAL")) {
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                }
            }
        }
    }

    // Insertion d'une commande avec une liste de produits
    public void insert(Order order, List<Product> products) throws SQLException {
        insert(order); // Insère d'abord la commande

        // Associe chaque produit à la commande dans la table PRODUIT_COMMANDE
        for (Product p : products) {
            insertProductToOrder(p, order);
        }
    }

    // Insertion d'un produit dans une commande
    public void insertProductToOrder(Product product, Order order) throws SQLException {
        String sql = "INSERT INTO PRODUIT_COMMANDE (FK_COMMANDE, FK_PRODUCT) VALUES (?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            prepareStatementForProductToOrder(ps, product, order);
            ps.executeUpdate();
        }
    }

    // Mise à jour de la commande
    public void update(Order order) throws SQLException {
        String sql = "UPDATE COMMANDE SET FK_CLIENT = ?, FK_RESTO = ?, A_EMPORTER = ?, QUAND = ?, TOTALAMOUNT = ? WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            prepareStatementForOrder(ps, order);
            ps.setLong(7, order.getId());
            ps.executeUpdate();
        }
    }

    // Suppression de la commande
    public void delete(long orderId) throws SQLException {
        String sql = "DELETE FROM COMMANDE WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.executeUpdate();
            orderIdentityMap.clear();
        }
    }

    // Recherche de commande par ID
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

    private void prepareStatementForOrder(PreparedStatement ps, Order order) throws SQLException {
        ps.setLong(1, order.getCustomer().getId());
        ps.setLong(2, order.getRestaurant().getId());
        ps.setBoolean(3, order.getTakeAway());
        // Conversion de LocalDateTime en java.sql.Date
        ps.setDate(4, java.sql.Date.valueOf(order.getWhen().toLocalDate()));
        ps.setBigDecimal(5, order.getTotalAmount());
    }

    private void prepareStatementForProductToOrder(PreparedStatement ps, Product product, Order order) throws SQLException {
        ps.setLong(1, order.getId());
        ps.setLong(2, product.getId());
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        long orderId = rs.getLong("NUMERO");
        boolean takeAway = rs.getBoolean("A_EMPORTER");
        java.sql.Date sqlDate = rs.getDate("QUAND");
        LocalDateTime orderDate = sqlDate.toLocalDate().atStartOfDay();


        long customerId = rs.getLong("FK_CLIENT");
        Customer customer = new CustomerMapper().findById(customerId);

        long restaurantId = rs.getLong("FK_RESTO");
        Restaurant restaurant = new RestaurantMapper().findById(restaurantId);

        Order order = new Order(orderId, customer, restaurant, takeAway, orderDate);

        ProductMapper productMapper = new ProductMapper();
        List<Product> productsList = productMapper.findProductByOrderId(orderId);
        Set<Product> productsSet = new HashSet<>(productsList);
        order.setProducts(productsSet);

        orderIdentityMap.put(orderId, order);
        return order;
    }
}
