package ch.hearc.ig.orderresto.business;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "COMMANDE")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_COMMANDE")
    @SequenceGenerator(name = "SEQ_COMMANDE", sequenceName = "SEQ_COMMANDE", allocationSize = 1)
    @Column(name = "NUMERO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CLIENT", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_RESTO", nullable = false)
    private Restaurant restaurant;

    @ManyToMany
    @JoinTable(
            name = "PRODUIT_COMMANDE",
            joinColumns = @JoinColumn(name = "FK_COMMANDE"),
            inverseJoinColumns = @JoinColumn(name = "FK_PRODUIT")
    )
    private Set<Product> products = new HashSet<>();

    @Column(name = "A_EMPORTER", nullable = false, columnDefinition = "CHAR(1)")
    private String takeAway;

    @Column(name = "QUAND", nullable = false)
    private LocalDateTime when;

    @Transient
    private BigDecimal totalAmount;

    public Order() {}

    public Order(Long id, Customer customer, Restaurant restaurant, Boolean takeAway, LocalDateTime when) {
        this.id = id;
        this.customer = customer;
        this.restaurant = restaurant;
        this.products = new HashSet<>();
        this.takeAway = takeAway != null && takeAway ? "O" : "N";
        this.totalAmount = new BigDecimal(0);
        this.when = when;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public Set<Product> getProducts() {
        return products;
    }

    public Boolean getTakeAway() {
        return "O".equals(this.takeAway);
    }

    public void setTakeAway(Boolean takeAway) { this.takeAway = takeAway != null && takeAway ? "O" : "N"; }

    public LocalDateTime getWhen() {
        return when;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setWhen(LocalDateTime when) { this.when = when; }

    public void addProduct(Product product) {
        this.products.add(product);
        this.totalAmount = this.totalAmount.add(product.getUnitPrice());
    }
}