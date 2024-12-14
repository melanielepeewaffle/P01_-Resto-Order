package ch.hearc.ig.orderresto.business;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RESTAURANT")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RESTAURANT")
    @SequenceGenerator(name = "SEQ_RESTAURANT", sequenceName = "SEQ_RESTAURANT", allocationSize = 1)
    @Column(name = "NUMERO")
    private Long id;

    @Column(name = "NOM", nullable = false)
    private String name;

    @Transient
    private Set<Order> orders;

    @Embedded
    private Address address;

    @Transient
    private Set<Product> productsCatalog;

    public Restaurant() {}

    public Restaurant(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public Restaurant(Long id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.orders = new HashSet<>();
        this.productsCatalog = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    public Address getAddress() {
        return address;
    }

    public Set<Product> getProductsCatalog() {
        return productsCatalog;
    }

    public void registerProduct(Product p) {
        if (p.getRestaurant() != this) {
            throw new RuntimeException("Restaurant mismatch!");
        }
        this.productsCatalog.add(p);
    }
}