package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.service.OrderService;
import ch.hearc.ig.orderresto.service.CustomerService;
import ch.hearc.ig.orderresto.service.RestaurantService;
import ch.hearc.ig.orderresto.service.ProductService;

import java.sql.Connection;

public class MainCLI extends AbstractCLI {
    private final Connection conn;
    private final OrderService orderService;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;
    private final ProductService productService;

    public MainCLI(Connection conn, OrderService orderService, CustomerService customerService, RestaurantService restaurantService, ProductService productService) {
        this.conn = conn;
        this.orderService = orderService;
        this.customerService = customerService;
        this.restaurantService = restaurantService;
        this.productService = productService;
    }

    public void run() {
        this.ln("======================================================");
        this.ln("Que voulez-vous faire ?");
        this.ln("0. Quitter l'application");
        this.ln("1. Faire une nouvelle commande");
        this.ln("2. Consulter une commande");

        int userChoice = this.readIntFromUser(2);
        this.handleUserChoice(userChoice);
    }

    private void handleUserChoice(int userChoice) {
        if (userChoice == 0) {
            this.ln("Good bye!");
            return;
        }

        OrderCLI orderCLI = new OrderCLI(conn, orderService, customerService, restaurantService, productService);
        if (userChoice == 1) {
            orderCLI.createNewOrder();
        } else if (userChoice == 2) {
            Order existingOrder = orderCLI.selectOrder();
            if (existingOrder != null) {
                orderCLI.displayOrder(existingOrder);
            }
        }

        this.run();
    }
}