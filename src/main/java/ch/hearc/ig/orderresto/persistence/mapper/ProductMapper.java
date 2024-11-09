package ch.hearc.ig.orderresto.persistence.mapper;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.util.DataBaseConnection;
import ch.hearc.ig.orderresto.persistence.util.DataBaseUtils;
import ch.hearc.ig.orderresto.persistence.util.IdentityMap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductMapper {

    private final IdentityMap<Product> productIdentityMap = new IdentityMap<>();
    private final RestaurantMapper restaurantMapper;

    /**
     * Reçoit une instance de RestaurantMapper pour éviter la création répétée d'instances dans chaque méthode.
     * @param restaurantMapper
     */
    public ProductMapper(RestaurantMapper restaurantMapper) {
        this.restaurantMapper = restaurantMapper;
    }

    public void insert(Product product) throws SQLException {
        String sql = "INSERT INTO PRODUIT (NUMERO, FK_RESTO, PRIX_UNITAIRE, NOM, DESCRIPTION) VALUES (SEQ_PRODUIT.NEXTVAL, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForProduct(ps, product);
            ps.executeUpdate();

            long generatedId = DataBaseUtils.getGeneratedKey(conn, "SEQ_PRODUIT");
            product.setId(generatedId);
            productIdentityMap.put(generatedId, product); // Màj. de l'identity Map
        }
    }

    /**
     * Méthode relationnelle afin de permettre de charger les produits associés à un restaurant.
     * @param restaurantId
     * @return
     * @throws SQLException
     */
    public List<Product> findProductsByRestaurantId(long restaurantId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUIT WHERE FK_RESTO = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, restaurantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long productId = rs.getLong("NUMERO");

                    // Vérification si le produit est déjà en cache
                    if (productIdentityMap.contains(productId)) {
                        products.add(productIdentityMap.get(productId));
                    } else {
                      Product product = mapResultSetToProductFromRestaurant(rs);
                      productIdentityMap.put(productId, product);
                      products.add(product);
                    }
                }
            }
        }
        return products;
    }

    public List<Product> findProductByOrderId(Long orderId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql  = "SELECT * FROM PRODUIT_COMMANDE WHERE FK_COMMANDE = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long productId = rs.getLong("FK_PRODUIT");

                    if (productIdentityMap.contains(productId)) {
                        products.add(productIdentityMap.get(productId));
                    } else {
                        Product product = mapResultSetToProductFromRestaurant(rs);
                        productIdentityMap.put(productId, product);
                        products.add(product);
                    }
                }
            }
        }
        return products;
    }

    private void prepareStatementForProduct(PreparedStatement ps, Product product) throws SQLException {
        ps.setLong(1, product.getRestaurant().getId());
        ps.setBigDecimal(2, product.getUnitPrice());
        ps.setString(3, product.getName());
        ps.setString(4, product.getDescription());
    }

    private Product mapResultSetToProductFromRestaurant(ResultSet rs) throws SQLException {
        // Utilisation de RestaurantMapper pour charger le restaurant associé via sa PK pour la relation entre
        // Product et Restaurant.
        Restaurant restaurant = restaurantMapper.findById(rs.getLong("FK_RESTO"));

        return new Product(
                rs.getLong("NUMERO"),
                rs.getString("NOM"),
                rs.getBigDecimal("PRIX_UNITAIRE"),
                rs.getString("DESCRIPTION"),
                restaurant
        );
    }
}