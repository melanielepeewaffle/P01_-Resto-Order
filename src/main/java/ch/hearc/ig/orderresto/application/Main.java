package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.persistence.RestaurantMapper;
import ch.hearc.ig.orderresto.presentation.MainCLI;

public class Main {

  public static void main(String[] args) {
    RestaurantMapper restaurantMapper = new RestaurantMapper();

    (new MainCLI(restaurantMapper)).run();
  }
}

