package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.service.ProductService;

import java.util.List;

public class ProductCLI extends AbstractCLI {
    private final ProductService productService;

    public ProductCLI(ProductService productService) {
        this.productService = productService;
    }

    public Product getRestaurantProduct(Restaurant restaurant) {

        List<Product> products = productService.findProductsByRestaurantId(restaurant.getId());

        this.ln(String.format("Bienvenue chez %s. Choisissez un de nos produits:", restaurant.getName()));
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            this.ln(String.format("%d. %s", i, product.getName()));
        }

        int index = this.readIntFromUser(products.size() - 1);
        return products.get(index);
    }
}