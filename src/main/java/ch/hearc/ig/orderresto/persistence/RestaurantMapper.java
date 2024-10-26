package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantMapper {
    public void insert(Restaurant restaurant) throws SQLException {
        String sql = "INSERT INTO RESTAURANT (NOM, CODE_POSTAL, LOCALITE, RUE, NUM_RUE, PAYS) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForRestaurant(ps, restaurant);
            ps.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT SEQ_RESTAURANT.CURRVAL FROM DUAL")) {
                if (rs.next()) {
                    restaurant.setId(rs.getLong(1));
                }
            }
        }
    }

    public void update(Restaurant restaurant) throws SQLException {
        String sql = "UPDATE RESTAURANT SET NOM = ?, CODE_POSTAL = ?, LOCALITE = ?, RUE = ?, NUM_RUE = ?, PAYS = ? WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForRestaurant(ps, restaurant);
            ps.setLong(7, restaurant.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM RESTAURANT WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public Restaurant findById(long id) throws SQLException {
        String sql = "SELECT * FROM RESTAURANT WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRestaurant(rs);
                }
            }
        }
        return null; // Si le restaurant n'est pas trouvé
    }

    public List<Restaurant> findAll() throws SQLException {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM RESTAURANT";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                restaurants.add(mapResultSetToRestaurant(rs));
            }
        }
        return restaurants;
    }

    /**
     * Mapper interactif : facilitation de l'obtention d'un produit associés à un restaurant depuis RestaurantMapper
     * en déléguant le chargement à ProductMapper.
     * @param restaurantId
     * @return
     * @throws SQLException
     */
    public List<Product> getProductsForRestaurant(long restaurantId) throws SQLException {
        ProductMapper productMapper = new ProductMapper();
        return productMapper.findProductsByRestaurantId(restaurantId);
    }

    /**
     * Préparation de la requête pour insert/update d'un Restaurant.
     * @param ps
     * @param restaurant
     * @throws SQLException
     */
    private void prepareStatementForRestaurant(PreparedStatement ps, Restaurant restaurant) throws SQLException {
        ps.setString(1, restaurant.getName());
        ps.setString(2, restaurant.getAddress().getPostalCode());
        ps.setString(3, restaurant.getAddress().getLocality());
        ps.setString(4, restaurant.getAddress().getStreet());
        ps.setString(5, restaurant.getAddress().getStreetNumber());
        ps.setString(6, restaurant.getAddress().getCountryCode());
    }

    /**
     * Conversion d'une ligne de ResultSet en un objet Restaurant.
     * --> Création d'une instance de Address à partir des données du ResultSet et l'utilise pour initialiser un objet
     *     Restaurant.
     *     Cela permet de simplifier le code en cas d'adaptation dans la structure de Restaurant ou Address
     * @param rs
     * @return
     * @throws SQLException
     */
    private Restaurant mapResultSetToRestaurant(ResultSet rs) throws SQLException {
        Address address = new Address(
                rs.getString("PAYS"),
                rs.getString("CODE_POSTAL"),
                rs.getString("LOCALITE"),
                rs.getString("RUE"),
                rs.getString("NUM_RUE")
        );
        return new Restaurant(
                rs.getLong("NUMERO"),
                rs.getString("NOM"),
                address
        );
    }
}
