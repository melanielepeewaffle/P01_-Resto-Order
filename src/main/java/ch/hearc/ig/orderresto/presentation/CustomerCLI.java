package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.service.CustomerService;

public class CustomerCLI extends AbstractCLI {
    private final CustomerService customerService;

    public CustomerCLI(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Customer getExistingCustomer() {
        this.ln("Quelle est votre adresse email?");
        String email = this.readEmailFromUser();

        Customer customer = customerService.findCustomerByEmail(email);
        if (customer == null) {
            this.ln("Client non trouvé.");
        }
        return customer;
    }

    public Customer createNewCustomer() {
        this.ln("Êtes-vous un client privé ou une organisation?");
        this.ln("0. Annuler");
        this.ln("1. Un client privé");
        this.ln("2. Une organisation");

        int customerTypeChoice = this.readIntFromUser(2);
        if (customerTypeChoice == 0) {
            return null;
        }

        this.ln("Quelle est votre addresse email?");
        String email = this.readEmailFromUser();
        this.ln("Quelle est votre numéro de téléphone?");
        String phone = this.readStringFromUser();
        Address address = (new AddressCLI()).getNewAddress();

        if (customerTypeChoice == 1) {
            this.ln("Êtes-vous un homme ou une femme (H/F) ?");
            String gender = this.readChoicesFromUser(new String[]{"H", "F"});
            this.ln("Quel est votre prénom ?");
            String firstName = this.readStringFromUser();
            this.ln("Quel est votre nom ?");
            String lastName = this.readStringFromUser();
            PrivateCustomer privateCustomer = new PrivateCustomer(null, phone, email, address, gender, firstName, lastName);
            customerService.createCustomer(privateCustomer);
            return privateCustomer;

        } else if (customerTypeChoice == 2) {
            this.ln("Quel est le nom de votre organisation ?");
            String name = this.readStringFromUser();
            this.ln(String.format("%s est une société anonyme (SA), une association (A) ou une fondation (F) ?", name));
            String legalForm = this.readChoicesFromUser(new String[]{"SA", "A", "F"});
            OrganizationCustomer organizationCustomer = new OrganizationCustomer(null, phone, email, address, name, legalForm);
            customerService.createCustomer(organizationCustomer);
            return organizationCustomer;
        }

        return null;
    }
}