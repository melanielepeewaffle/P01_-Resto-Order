package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class TestApplication {
    public static void main(String[] args) {
        System.out.println("Début des tests Hibernate...");
        //TestRestaurant();
        TestCustomer();
        System.out.println("Fin des tests Hibernate...");
    }

    public static void TestCustomer() {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // Créer un client privé
            PrivateCustomer privateCustomer = new PrivateCustomer(
                    null,
                    "0781234567",
                    "email@test.com",
                    new Address("CH", "2000", "Neuchâtel", "Rue du Client", "10"),
                    "O", // femme
                    "Jean",
                    "Dupont"
            );
            em.persist(privateCustomer);

            // Créer un client organisation
            OrganizationCustomer organizationCustomer = new OrganizationCustomer(
                    null,
                    "0214567890",
                    "contact@company.com",
                    new Address("CH", "1000", "Lausanne", "Avenue de l'Entreprise", "20"),
                    "Entreprise SA",
                    "SA" // forme sociale
            );
            em.persist(organizationCustomer);

            transaction.commit();

            System.out.println("PrivateCustomer créé avec succès : ID = " + privateCustomer.getId());
            System.out.println("OrganizationCustomer créé avec succès : ID = " + organizationCustomer.getId());

            // Charger les entités depuis la base
            PrivateCustomer loadedPrivate = em.find(PrivateCustomer.class, privateCustomer.getId());
            System.out.println("PrivateCustomer chargé : " + loadedPrivate.getFirstName() + " " + loadedPrivate.getLastName());

            OrganizationCustomer loadedOrganization = em.find(OrganizationCustomer.class, organizationCustomer.getId());
            System.out.println("OrganizationCustomer chargé : " + loadedOrganization.getName());

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

    public static void TestRestaurant() {
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