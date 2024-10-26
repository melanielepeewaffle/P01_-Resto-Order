package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.FakeDb;
import ch.hearc.ig.orderresto.persistence.RestaurantMapper;

import java.sql.SQLException;
import java.util.List;

public class RestaurantCLI extends AbstractCLI {

    private final RestaurantMapper restaurantMapper;

    /**
     * Prend désormais le RestaurantMapper en paramètre afin de permettre une interaction directe avec la DB.
     * @param restaurantMapper
     */
    public RestaurantCLI(RestaurantMapper restaurantMapper) {
        this.restaurantMapper = restaurantMapper;
    }

    public Restaurant getExistingRestaurant() {
        try {
            this.ln("Choisissez un restaurant:");
            // Une List est plus appropriée pour la taille dynamique lors de la récupération dans la DB.
            List<Restaurant> allRestaurants = restaurantMapper.findAll();

            for (int i = 0; i < allRestaurants.size(); i++) {
                Restaurant restaurant = allRestaurants.get(i);
                this.ln(String.format("%d. %s.", i, restaurant.getName()));
            }

            int index = this.readIntFromUser(allRestaurants.size() -1);
            return allRestaurants.get(index);

        } catch (SQLException e) { // Gestion des exceptions SQL afin d'informer l'utilisateur en cas d'erreur
            e.printStackTrace();
            this.ln("Erreur lors de la récupération des restaurants.");
            return null;
        }
    }
}
