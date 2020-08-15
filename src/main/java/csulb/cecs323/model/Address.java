package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "addresses")
public class Address
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long add_id;
    private boolean isDefaultAddress;

    private String type;
    private String street;
    private String zipcode;
    private String city;
    private String state;

    // ASSOCIATION(S)
    @ManyToOne
    private Person person;

    // CONSTRUCTORS
    public Address () {}
    public Address (Person person, String street, String zipcode)
    {
        setPerson(person);
        setStreet(street);
        setZipcode(zipcode);
    }
    public Address (Person person, String type, String street, String zipcode)
    {
        setPerson(person);
        setType(type);
        setStreet(street);
        setZipcode(zipcode);
    }
    public Address (Person person, String type, String street, String zipcode, String city, String state)
    {
        setPerson(person);
        setType(type);
        setStreet(street);
        setZipcode(zipcode);
        setCity(city);
        setState(state);
    }

    // ACCESSORS
    public boolean isDefaultAddress () { return this.isDefaultAddress; }
    public String getType () { return this.type; }
    public String getStreet () { return this.street; }
    public String getZipcode () { return this.zipcode; }
    public String getCity () { return this.city; }
    public String getState () { return this.state; }
    public Person getPerson () { return this.person; }

    // MUTATORS
    public void setIsDefaultAddress (boolean isDefaultAddress) { this.isDefaultAddress = isDefaultAddress; }
    public void setType (String type) { this.type = type; }
    public void setStreet (String street) { this.street = street; }
    public void setZipcode (String zipcode) { this.zipcode = zipcode; }
    public void setCity (String city) { this.city = city; }
    public void setState (String state) { this.state = state; }
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
