package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.mapper.ProductMapper;
import ch.hearc.ig.orderresto.persistence.mapper.RestaurantMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductMapper productMapper;
    private final RestaurantMapper restaurantMapper;

    public ProductService(ProductMapper productMapper, RestaurantMapper restaurantMapper) {
        this.productMapper = productMapper;
        this.restaurantMapper = restaurantMapper;
    }

    public List<Product> findProductsByRestaurantId(Connection conn, long restaurantId) throws SQLException {
        Restaurant restaurant = restaurantMapper.findById(conn, restaurantId);
        List<Product> products = productMapper.findProductsByRestaurantId(conn, restaurantId);

        for (Product product : products) {
            product.setRestaurant(restaurant);
        }
        return products;
    }
}