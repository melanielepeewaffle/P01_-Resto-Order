package ch.hearc.ig.orderresto.business;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, columnDefinition = "CHAR(1)")
@Table(name = "CLIENT")
public abstract class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CLIENT")
    @SequenceGenerator(name = "SEQ_CLIENT", sequenceName = "SEQ_CLIENT", allocationSize = 1)
    @Column(name = "NUMERO")
    private Long id;

    @Column(name = "TELEPHONE", nullable = false)
    private String phone;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();

    @Embedded
    private Address address;

    protected Customer() {}

    protected Customer(Long id, String phone, String email, Address address) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.orders = new HashSet<>();
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }
}