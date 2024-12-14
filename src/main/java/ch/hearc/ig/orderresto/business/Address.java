package ch.hearc.ig.orderresto.business;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "PAYS", nullable = false)
    private String countryCode;

    @Column(name = "CODE_POSTAL", nullable = false)
    private String postalCode;

    @Column(name = "LOCALITE", nullable = false)
    private String locality;

    @Column(name = "RUE", nullable = false)
    private String street;

    @Column(name = "NUM_RUE")
    private String streetNumber;

    public Address() {}

    public Address(String countryCode, String postalCode, String locality, String street, String streetNumber) {
        this.countryCode = countryCode;
        this.postalCode = postalCode;
        this.locality = locality;
        this.street = street;
        this.streetNumber = streetNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getLocality() {
        return locality;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }
}