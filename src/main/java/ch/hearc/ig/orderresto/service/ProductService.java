package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProductService {

    public List<Product> findProductsByRestaurantId(long restaurantId) {
        EntityManager em = HibernateUtil.getEntityManager();

        try {
            Restaurant restaurant = em.find(Restaurant.class, restaurantId);

            if (restaurant == null) {
                throw new RuntimeException("restaurant not found");
            }

            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.restaurant.id = :restaurantId", Product.class
            );
            query.setParameter("restaurantId", restaurantId);

            return query.getResultList();
        } finally {
            em.close();
        }
    }
}