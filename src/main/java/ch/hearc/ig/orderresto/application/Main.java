package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.persistence.RestaurantMapper;
import ch.hearc.ig.orderresto.presentation.MainCLI;

public class Main {

  public static void main(String[] args) {
    RestaurantMapper restaurantMapper = new RestaurantMapper();

    (new MainCLI(restaurantMapper)).run();
  }
}

/*


package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.persistence.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Main {

  public static void main(String[] args) {
    try {
      // Création de mappers
      AddressMapper addressMapper = new AddressMapper();
      RestaurantMapper restaurantMapper = new RestaurantMapper();
      ProductMapper productMapper = new ProductMapper();
      OrderMapper orderMapper = new OrderMapper();


      CustomerMapper customerMapper = new CustomerMapper(); // Supposons que vous avez CustomerMapper

      // Création et insertion d'une adresse
      Address address = new Address("FR", "75001", "Paris", "Rue de Rivoli", "1");
      addressMapper.insert(address);
      System.out.println("Adresse insérée : " + address);

      // Création et insertion d'un client
      Customer customer = new Customer("0123456789", "client@example.com", address); // Supposons que Customer a un constructeur
      customerMapper.insert(customer); // Insérez le client dans la base de données
      System.out.println("Client inséré : " + customer);


      // Création et insertion d'un restaurant
      Restaurant restaurant = new Restaurant(null, "Le Bon Resto", address);
      restaurantMapper.insert(restaurant);
      System.out.println("Restaurant inséré : " + restaurant);

      // Création et insertion de produits
      Product product1 = new Product(null, "Pizza Margherita", new BigDecimal("8.50"), "Délicieuse pizza", restaurant);
      Product product2 = new Product(null, "Salade César", new BigDecimal("6.00"), "Salade fraîche et croquante", restaurant);
      productMapper.insert(product1);
      productMapper.insert(product2);
      System.out.println("Produits insérés : " + product1 + ", " + product2);


      // Création et insertion d'une commande avec produits
      Set<Product> products = new HashSet<>();
      products.add(product1);
      products.add(product2);
      Order order = new Order(null, customer, restaurant, false, LocalDateTime.now());
      order.setProducts(products);
      orderMapper.insert(order); // Insertion de la commande
      System.out.println("Commande insérée : " + order);

      // Récupération et affichage d'une commande par ID
      Order retrievedOrder = orderMapper.findById(order.getId());
      System.out.println("Commande récupérée : " + retrievedOrder);

      // Mettre à jour une commande
      order.setTakeAway(true);
      orderMapper.update(order);
      System.out.println("Commande mise à jour : " + order);

      // Suppression de la commande
      orderMapper.delete(order.getId());
      System.out.println("Commande supprimée : " + order.getId());

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
