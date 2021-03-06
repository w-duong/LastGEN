package csulb.cecs323.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="person_type", discriminatorType = DiscriminatorType.INTEGER)
abstract public class Person
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long person_id;

    private String firstName;
    private String middleName;
    private String lastName;

    // ASSOCIATION(S)
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phone> phoneList;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProviderCertification> licenses;

    // CONSTRUCTORS
    public Person () {}
    public Person (String first, String last)
    {
        setFirstName(first);
        setLastName(last);

        this.addresses = new ArrayList<>();
        this.phoneList = new ArrayList<>();
    }
    public Person (String first, String last, Address address, Phone phone)
    {
        setFirstName(first);
        setLastName(last);
        addAddress(address);
        addPhone(phone);
    }

    // ACCESSORS
    public String getFirstName () { return this.firstName; }
    public String getMiddleName () { return this.middleName; }
    public String getLastName () { return this.lastName; }

    public List<Phone> getPhoneList () { return this.phoneList; }
    public List<Address> getAddresses () { return this.addresses; }
    public List<ProviderCertification> getLicenses () { return this.licenses; }

    // MUTATORS
    public void setFirstName (String fname) { this.firstName = fname; }
    public void setMiddleName (String mname) { this.middleName = mname; }
    public void setLastName (String lname) { this.lastName = lname; }

    // MISCELLANEOUS
    public void addAddress (Address address)
    {
        if (this.addresses == null)
            this.addresses = new ArrayList<>();

        if (!this.addresses.contains(address))
        {
            this.addresses.add(address);

            address.setPerson(this);
        }
    }

    public void addPhone (Phone phone)
    {
        if (this.phoneList == null)
            this.phoneList = new ArrayList<>();

        if (!this.phoneList.contains(phone))
        {
            this.phoneList.add(phone);

            phone.setPerson(this);
        }
    }

    public void addCertification (ProviderCertification license)
    {
        if (this.licenses == null)
            this.licenses = new ArrayList<>();

        if (!this.licenses.contains(license))
        {
            this.licenses.add(license);

            license.setPerson(this);
        }
    }

    public String toString ()
    {
        return String.format ("FIRST: %-10s\tLAST: %-10s\tAddress > %-40s\tPhone > %s",
                firstName, lastName, addresses, phoneList);
    }
}
