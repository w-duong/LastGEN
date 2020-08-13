package csulb.cecs323.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.persistence.*;

@Entity
@Table (name = "phone_numbers", uniqueConstraints = @UniqueConstraint(columnNames = {"person","actual_number"}))
@NamedQueries({
        @NamedQuery (name = Phone.FIND_BY_NUMBER,
                query = "SELECT pt FROM Phone pn INNER JOIN pn.person pt WHERE pn.actual_number = :numberString")
})
public class Phone
{
    //QUERY STRING(S)
    public static final String FIND_BY_NUMBER = "Phone.findByNumber";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long phone_id;

    private boolean isDefaultNumber;
    private String type;
    private String actual_number;

    // ASSOCIATION(S)
    @ManyToOne
    private Person person;

    // CONSTRUCTORS
    public Phone() {}
    public Phone(Person person, String type, String number)
    {
        setPerson(person);
        setType(type);
        setNumber(number);
    }

    // ACCESSORS
    public boolean isDefaultNumber() { return this.isDefaultNumber; }
    public String getType () {return this.type; }
    public String getNumber() { return this.actual_number; }
    public String getPrettyNumber ()
    {
        return String.format ("(%s)%s-%s", actual_number.substring(0,3),
                actual_number.substring(3,6), actual_number.substring(6,10));
    }

    // MUTATORS
    public void setIsDefault (boolean isDefaultNumber) { this.isDefaultNumber = isDefaultNumber; }
    public void setType (String type) { this.type = type; }
    public void setNumber (String number) { this.actual_number = number; }
    public void setPerson (Person person)
    {
        this.person = person;
        person.addPhone(this);
    }

    // MISCELLANEOUS
    @Override
    public String toString () { return String.format("TYPE: %-10s\tNUMBER: %-15s", this.type, getPrettyNumber()); }
}
