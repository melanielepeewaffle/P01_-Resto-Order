package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class TestApplication {
    public static void main(String[] args) {
        System.out.println("Début des tests Hibernate...");

        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // Créer un restaurant simple
            Restaurant restaurant = new Restaurant("Restaurant Test", new Address("CH", "2000", "Neuchâtel", "Rue du Test", "5"));
            em.persist(restaurant);

            transaction.commit();
            System.out.println("Restaurant créé avec succès : ID = " + restaurant.getId());

            // Charger l'entité depuis la base
            Restaurant loadedRestaurant = em.find(Restaurant.class, restaurant.getId());
            System.out.println("Restaurant chargé depuis la base : " + loadedRestaurant.getName());
            System.out.println("Adresse chargée : " + loadedRestaurant.getAddress().getLocality());

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            HibernateUtil.shutdown();
        }
    }
}
