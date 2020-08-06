package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "addresses")
public class Address
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long add_id;

    private String type;
    private String street;
    private String zipcode;

    // ASSOCIATION(S)
    @ManyToOne
    private Person person;

    // CONSTRUCTORS
    public Address () {}
    public Address (Person person, String type, String street, String zipcode)
    {
        setPerson(person);
        setType(type);
        setStreet(street);
        setZipcode(zipcode);
    }

    // ACCESSORS

    // MUTATORS
    public void setType (String type) { this.type = type; }
    public void setStreet (String street) { this.street = street; }
    public void setZipcode (String zipcode) { this.zipcode = zipcode; }
    public void setPerson (Person person)
    {
        this.person = person;

        person.addAddress(this);
    }

    // MISCELLANEOUS
    @Override
    public String toString ()
    {
        return String.format ("TYPE: %-10s\tSTREET: %-20s\tZIP: %-10s", type, street, zipcode);
    }
}
