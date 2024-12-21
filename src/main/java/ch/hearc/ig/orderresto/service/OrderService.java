package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.util.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class OrderService {

    public void createOrder(Order order) {
        EntityManager em = HibernateUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(order);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error creating order", e);
        }
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        EntityManager em = HibernateUtil.getEntityManager();

        try {
            List<Order> orders = em.createQuery(
                    "SELECT o FROM Order o JOIN FETCH o.restaurant JOIN FETCH o.products WHERE o.customer.id = :customerId", Order.class
            ).setParameter("customerId", customerId).getResultList();

            return orders;
        } finally {
            em.close();
        }
    }
}