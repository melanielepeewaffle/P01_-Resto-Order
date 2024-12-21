package ch.hearc.ig.orderresto.business;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PRODUIT")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRODUIT")
    @SequenceGenerator(name = "SEQ_PRODUIT", sequenceName = "SEQ_PRODUIT", allocationSize = 1)
    @Column(name = "NUMERO")
    private Long id;

    @Column(name = "NOM", nullable = false)
    private String name;

    @Column(name = "PRIX_UNITAIRE", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @ManyToMany(mappedBy = "products")
    private Set<Order> orders = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_RESTO", nullable = false)
    private Restaurant restaurant;

    public Product() {}

    public Product(Long id, String name, BigDecimal unitPrice, String description, Restaurant restaurant) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.description = description;
        this.orders = new HashSet<>();
        setRestaurant(restaurant);
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;

        if (restaurant != null) {
            restaurant.registerProduct(this);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "%s - %.2f de chez %s: %s",
                this.getName(),
                this.getUnitPrice(),
                this.getRestaurant().getName(),
                this.getDescription()
        );
    }
}