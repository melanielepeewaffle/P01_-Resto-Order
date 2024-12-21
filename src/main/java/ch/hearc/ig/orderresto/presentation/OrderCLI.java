package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.service.CustomerService;
import ch.hearc.ig.orderresto.service.OrderService;
import ch.hearc.ig.orderresto.service.ProductService;
import ch.hearc.ig.orderresto.service.RestaurantService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderCLI extends AbstractCLI {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;
    private final ProductService productService;

    public OrderCLI(OrderService orderService, CustomerService customerService, RestaurantService restaurantService, ProductService productService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.restaurantService = restaurantService;
        this.productService = productService;
    }

    public Order createNewOrder() {
        this.ln("======================================================");
        Restaurant restaurant = (new RestaurantCLI(restaurantService)).getExistingRestaurant();
        if (restaurant == null) {
            this.ln("Aucun restaurant sélectionné. Annulation de la commande.");
            return null;
        }

        Product product = (new ProductCLI(productService)).getRestaurantProduct(restaurant);
        if (product == null) {
            this.ln("Aucun produit sélectionné. Annulation de la commande.");
            return null;
        }

        this.ln("======================================================");
        this.ln("0. Annuler");
        this.ln("1. Je suis un client existant");
        this.ln("2. Je suis un nouveau client");

        int userChoice = this.readIntFromUser(2);
        if (userChoice == 0) {
            return null;
        }

        CustomerCLI customerCLI = new CustomerCLI(customerService);
        Customer customer = null;

        if (userChoice == 1) {
            customer = customerCLI.getExistingCustomer();
            if (customer == null) {
                this.ln("Client non trouvé. Annulation de la commande.");
                return null;
            }
        } else if (userChoice == 2) {
            customer = customerCLI.createNewCustomer();
            if (customer == null) {
                this.ln("Erreur lors de la création du client. Annulation de la commande.");
                return null;
            }
        }

        // Possible improvements:
        // - ask whether it's a takeAway order or not?
        // - Ask user for multiple products?
        Order order = new Order(null, customer, restaurant, false, LocalDateTime.now());
        order.addProduct(product);

        // Actually place the order (this could/should be in a different method?)
        try {
            orderService.createOrder(order);
            this.ln("Merci pour votre commande !");
        } catch (RuntimeException e) {
            this.ln("Erreur lors de la création de la commande.");
        }

        return order;
    }

    public Order selectOrder() {
        Customer customer = (new CustomerCLI(customerService)).getExistingCustomer();
        if (customer == null) {
            this.ln("Désolé, nous ne connaissons pas cette personne.");
            return null;
        }

        List<Order> orders;
        orders = orderService.getOrdersByCustomerId(customer.getId());

        if (orders.isEmpty()) {
            this.ln(String.format("Désolé, il n'y a aucune commande pour %s", customer.getEmail()));
            return null;
        }

        this.ln("Choisissez une commande:");
        for (int i = 0 ; i < orders.size() ; i++) {
            Order order = orders.get(i);
            LocalDateTime when = order.getWhen();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à hh:mm");
            this.ln(String.format("%d. %.2f, le %s chez %s.", i, order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));
        }

        int index = this.readIntFromUser(orders.size() - 1);
        return orders.get(index);
    }

    public void displayOrder(Order order) {
        LocalDateTime when = order.getWhen();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à hh:mm");
        this.ln(String.format("Commande %.2f, le %s chez %s.:", order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));
        int index = 1;

        for (Product product: order.getProducts()) {
            this.ln(String.format("%d. %s", index, product));
            index++;
        }
    }
}