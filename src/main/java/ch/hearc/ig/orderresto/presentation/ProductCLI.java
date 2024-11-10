package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.service.ProductService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductCLI extends AbstractCLI {
    private final Connection conn;
    private final ProductService productService;

    public ProductCLI(Connection conn, ProductService productService) {
        this.conn = conn;
        this.productService = productService;
    }

    public Product getRestaurantProduct(Restaurant restaurant) {
        try {
            List<Product> products = productService.findProductsByRestaurantId(conn, restaurant.getId());

            this.ln(String.format("Bienvenue chez %s. Choisissez un de nos produits:", restaurant.getName()));
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                this.ln(String.format("%d. %s", i, product.getName()));
            }

            int index = this.readIntFromUser(products.size() - 1);
            return products.get(index);
        } catch (SQLException e) {
            handleSQLException(e, "Erreur lors de la récupération des produits.");
            return null;
        }
    }
}
