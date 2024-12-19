package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.persistence.mapper.*;
import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.service.*;

public class Main {

  public static void main(String[] args) {
    // Initialisation des mappers
    RestaurantMapper restaurantMapper = new RestaurantMapper();
    ProductMapper productMapper = new ProductMapper(restaurantMapper);
    OrderMapper orderMapper = new OrderMapper(restaurantMapper, productMapper);

    // Initialisation des services
    CustomerService customerService = new CustomerService();
    ProductService productService = new ProductService(productMapper, restaurantMapper);
    RestaurantService restaurantService = new RestaurantService(restaurantMapper);
    OrderService orderService = new OrderService(orderMapper);

    // Lancement de l'application via MainCLI
    (new MainCLI(orderService, customerService, restaurantService, productService)).run();
  }
}