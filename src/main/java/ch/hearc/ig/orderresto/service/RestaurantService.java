package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.mapper.RestaurantMapper;
import ch.hearc.ig.orderresto.persistence.util.DataBaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RestaurantService {

    private final RestaurantMapper restaurantMapper;

    public RestaurantService(RestaurantMapper restaurantMapper) {
        this.restaurantMapper = restaurantMapper;
    }

    public List<Restaurant> findAllRestaurants() throws SQLException {
        try (Connection conn = DataBaseConnection.getConnection()) {
            return restaurantMapper.findAll(conn);
        }
    }
}