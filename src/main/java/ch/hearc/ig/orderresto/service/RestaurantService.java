package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class RestaurantService {

    public List<Restaurant> findAllRestaurants() {
        EntityManager em = HibernateUtil.getEntityManager();

        try {
            TypedQuery<Restaurant> query = em.createQuery(
                    "SELECT r FROM Restaurant r", Restaurant.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}