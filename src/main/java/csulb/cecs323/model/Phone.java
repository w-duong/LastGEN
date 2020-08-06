package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "phone_numbers", uniqueConstraints = @UniqueConstraint(columnNames = {"person","actual_number"}))
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long phone_id;

    private String type;
    private String actual_number;

    // ASSOCIATION(S)
    @ManyToOne
    private Person person;

    // CONSTRUCTORS
    public Phone() {
    }

    public Phone(Person person, String type, String number)
    {
        setPerson(person);
        setType(type);
        setNumber(number);
    }

    // ACCESSORS

    // MUTATORS
    public void setType (String type) { this.type = type; }
    public void setNumber (String number) { this.actual_number = number; }
    public void setPerson (Person person) { this.person = person; }

    // MISCELLANEOUS
}
