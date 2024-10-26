package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductMapper {

    public void insert(Product product) throws SQLException {
        String sql = "INSERT INTO PRODUIT (NUMERO, FK_RESTO, PRIX_UNITAIRE, NOM, DESCRIPTION) VALUES (SEQ_PRODUIT.NEXTVAL, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForProduct(ps, product);
            ps.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT SEQ_PRODUIT.CURRVAL FROM DUAL")) {
                if (rs.next()) {
                    product.setId(rs.getLong(1));
                }
            }
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
                    products.add(mapResultSetToProduct(rs));
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

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        // Utilisation de RestaurantMapper pour charger le restaurant associé via sa PK pour la relation entre
        // Product et Restaurant.
        Restaurant restaurant = new RestaurantMapper().findById(rs.getLong("FK_RESTO"));

        return new Product(
                rs.getLong("NUMERO"),
                rs.getString("NOM"),
                rs.getBigDecimal("PRIX_UNITAIRE"),
                rs.getString("DESCRIPTION"),
                restaurant
        );
    }
}
