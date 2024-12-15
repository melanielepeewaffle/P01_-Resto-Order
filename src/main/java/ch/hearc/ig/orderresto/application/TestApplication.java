package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.persistence.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;

public class TestApplication {
    public static void main(String[] args) {
        System.out.println("Début des tests Hibernate...");
        //TestRestaurant();
        //TestCustomer();
        TestCustomerOrders();
        System.out.println("Fin des tests Hibernate...");
    }

    public static void TestCustomerOrders() {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // Créer un restaurant
            Restaurant restaurant = new Restaurant(
                    "Test Resto",
                    new Address("CH", "1000", "Lausanne", "Rue du Resto", "5")
            );
            em.persist(restaurant);

            // Créer un client
            PrivateCustomer customer = new PrivateCustomer(
                    null,
                    "0781234567",
                    "email@test.com",
                    new Address("CH", "2000", "Neuchâtel", "Rue du Client", "10"),
                    "O", // femme
                    "Jean",
                    "Dupont"
            );
            em.persist(customer);

            // Créer une commande pour ce client et ce restaurant
            Order order = new Order();
            order.setRestaurant(restaurant);
            order.setCustomer(customer);
            order.setTakeAway(true);
            order.setWhen(LocalDateTime.now());
            customer.addOrder(order);
            restaurant.addOrder(order);

            em.persist(order);

            transaction.commit();

            System.out.println("Restaurant créé avec succès : ID = " + restaurant.getId());
            System.out.println("Customer créé avec succès : ID = " + customer.getId());
            System.out.println("Order créée avec succès : ID = " + order.getId());

            // Charger le restaurant et vérifier les commandes
            Restaurant loadedRestaurant = em.find(Restaurant.class, restaurant.getId());
            System.out.println("Restaurant chargé : " + loadedRestaurant.getName());
            System.out.println("Nombre de commandes : " + loadedRestaurant.getOrders().size());

            // Charger le client et vérifier les commandes
            PrivateCustomer loadedCustomer = em.find(PrivateCustomer.class, customer.getId());
            System.out.println("Customer chargé : " + loadedCustomer.getFirstName() + " " + loadedCustomer.getLastName());
            System.out.println("Nombre de commandes : " + loadedCustomer.getOrders().size());

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