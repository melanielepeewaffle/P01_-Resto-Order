package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.persistence.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class CustomerService {

    public void createCustomer(Customer customer) {
        EntityManager em = HibernateUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(customer); // Hibernate pour persister l'entité
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error creating customer", e);
        } finally {
            em.close(); // Pour libérer les ressources
        }
    }

    public Customer findCustomerByEmail(String email) {
        EntityManager em = HibernateUtil.getEntityManager();

        try {
            TypedQuery<Customer> query = em.createQuery(
                    "SELECT c FROM Customer c WHERE c.email = :email", Customer.class
            );
            query.setParameter("email", email);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}