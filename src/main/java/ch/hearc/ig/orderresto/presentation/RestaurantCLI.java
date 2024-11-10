package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.service.RestaurantService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RestaurantCLI extends AbstractCLI {
    private final Connection conn;
    private final RestaurantService restaurantService;

    public RestaurantCLI(Connection conn, RestaurantService restaurantService) {
        this.conn = conn;
        this.restaurantService = restaurantService;
    }

    public Restaurant getExistingRestaurant() {
        try {
            List<Restaurant> restaurants = restaurantService.findAllRestaurants(conn);
            this.ln("Choisissez un restaurant:");
            for (int i = 0; i < restaurants.size(); i++) {
                Restaurant restaurant = restaurants.get(i);
                this.ln(String.format("%d. %s.", i, restaurant.getName()));
            }

            int index = this.readIntFromUser(restaurants.size() - 1);
            return restaurants.get(index);
        } catch (SQLException e) {
            handleSQLException(e, "Erreur lors de la récupération des restaurants.");
            return null;
        }
    }
}