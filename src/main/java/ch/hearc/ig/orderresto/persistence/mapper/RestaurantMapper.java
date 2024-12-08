package ch.hearc.ig.orderresto.persistence.mapper;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.util.DataBaseUtils;
import ch.hearc.ig.orderresto.persistence.util.IdentityMap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantMapper {

    private final IdentityMap<Restaurant> restaurantIdentityMap = new IdentityMap<>();
    private final AddressMapper addressMapper = new AddressMapper();

    public void insert(Connection conn, Restaurant restaurant) throws SQLException {
        String sql = "INSERT INTO RESTAURANT (NOM, CODE_POSTAL, LOCALITE, RUE, NUM_RUE, PAYS) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, restaurant.getName());
            // Préparation de l'adresse à partir de l'index 2 pour lire les champs d'adresse du ResultSet
            addressMapper.prepareStatementForAddress(ps, restaurant.getAddress(), 2);
            ps.executeUpdate();

            long generatedId = DataBaseUtils.getGeneratedKey(conn, "SEQ_RESTAURANT");
            restaurant.setId(generatedId);
            restaurantIdentityMap.put(generatedId, restaurant); // Màj. de l'identity Map
        }
    }

    public void update(Connection conn, Restaurant restaurant) throws SQLException {
        String sql = "UPDATE RESTAURANT SET NOM = ?, CODE_POSTAL = ?, LOCALITE = ?, RUE = ?, NUM_RUE = ?, PAYS = ? WHERE NUMERO = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, restaurant.getName());
            addressMapper.prepareStatementForAddress(ps, restaurant.getAddress(), 2); // Préparation de l'adresse
            ps.setLong(7, restaurant.getId());
            ps.executeUpdate();

            restaurantIdentityMap.put(restaurant.getId(), restaurant);  // Màj. de l'identity Map
        }
    }

    public void delete(Connection conn, long id) throws SQLException {
        String sql = "DELETE FROM RESTAURANT WHERE NUMERO = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();

            restaurantIdentityMap.remove(id);
        }
    }

    public Restaurant findById(Connection conn, long id) throws SQLException {
        // 1. Vérification si l'objet est déjà dans l'identity Map
        if (restaurantIdentityMap.contains(id)) {
            return restaurantIdentityMap.get(id); // Si oui, retourne l'objet directement
        }

        // 2. Requête SQL pour charger l'objet depuis la DB
        String sql = "SELECT * FROM RESTAURANT WHERE NUMERO = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Restaurant restaurant = mapResultSetToRestaurant(rs);
                    restaurantIdentityMap.put(id, restaurant); // Ajoute dans l'identity map
                    return restaurant;
                }
            }
        }

        return null; // Si le restaurant n'est pas trouvé
    }

    public List<Restaurant> findAll(Connection conn) throws SQLException {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM RESTAURANT";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long restaurantId = rs.getLong("NUMERO");

                if (restaurantIdentityMap.contains(restaurantId)) { // Vérifie si l'identity Map a déjà cet objet
                    restaurants.add(restaurantIdentityMap.get(restaurantId));
                } else {
                    Restaurant restaurant = mapResultSetToRestaurant(rs);
                    restaurantIdentityMap.put(restaurantId, restaurant);
                    restaurants.add(restaurant);
                }
            }
        }

        return restaurants;
    }

    /**
     * Conversion d'une ligne de ResultSet en un objet Restaurant.
     * Utilise AddressMapper pour créer l'instance d'Address à partir des données du ResultSet.
     * @param rs
     * @return
     * @throws SQLException
     */
    private Restaurant mapResultSetToRestaurant(ResultSet rs) throws SQLException {
        Address address = addressMapper.mapResultSetToAddress(rs, 2); // Mapping de l'addresse à partir du ResultSet

        return new Restaurant(
                rs.getLong("NUMERO"),
                rs.getString("NOM"),
                address
        );
    }
}