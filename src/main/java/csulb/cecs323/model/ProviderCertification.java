package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table(name = "provider_certifications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"issuer","actual_number"}))
public class ProviderCertification
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long pc_id;

    private String type;
    private String actual_number;
    private String issuer;

    // ASSOCIATION(S)
    @ManyToOne
    private Person person;

    // CONSTRUCTORS
    public ProviderCertification () {}
    public ProviderCertification (Person person, String type, String actual_number, String issuer)
    {
        setPerson(person);
        setType(type);
        setNumber(actual_number);
        setIssuer(issuer);
    }

    // ACCESSORS
    public String getType () { return this.type; }
    public String getNumber () { return this.actual_number; }
    public String getIssuer () { return this.issuer; }
    public Person getPerson () { return this.person; }

    // MUTATORS
    public void setType (String type) { this.type = type; }
    public void setNumber (String number) { this.actual_number = number; }
    public void setIssuer (String issuer) { this.issuer = issuer; }
    public void setPerson (Person person)
    {
        this.person = person;
        person.addCertification(this);
    }

    // MISCELLANEOUS
}
