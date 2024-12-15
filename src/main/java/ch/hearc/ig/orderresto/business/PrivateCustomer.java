package ch.hearc.ig.orderresto.business;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("P")
public class PrivateCustomer extends Customer {

    @Column(name = "EST_UNE_FEMME", nullable = true, columnDefinition = "CHAR(1)")
    private String gender;

    @Column(name = "PRENOM", nullable = true)
    private String firstName;

    @Column(name = "NOM", nullable = false)
    private String lastName;

    public PrivateCustomer() {}

    public PrivateCustomer(Long id, String phone, String email, Address address, String gender, String firstName, String lastName) {
        super(id, phone, email, address);
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}