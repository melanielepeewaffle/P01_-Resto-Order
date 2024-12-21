package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.service.*;

public class Main {

  public static void main(String[] args) {

    // Initialisation des services
    CustomerService customerService = new CustomerService();
    ProductService productService = new ProductService();
    RestaurantService restaurantService = new RestaurantService();
    OrderService orderService = new OrderService();

    // Lancement de l'application via MainCLI
    (new MainCLI(orderService, customerService, restaurantService, productService)).run();
  }
}