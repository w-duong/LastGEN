package csulb.cecs323.model;

import csulb.cecs323.model.Person;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

@Entity
@Table (name = "patients")
@DiscriminatorValue("1")
public class Patient extends Person
{
    private GregorianCalendar dateOfBirth;
    private String allergies;
    private String comorbidities;

    // CONSTRUCTORS
    public Patient () {}
    public Patient (String first, String last, GregorianCalendar birth)
    {
        super(first, last);
        setDateOfBirth(birth);
    }

    // ACCESSORS

    // MUTATORS
    public void setDateOfBirth (GregorianCalendar dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setAllergies (String allergies) { this.allergies = allergies; }
    public void setComorbidities (String comorbidities) { this.comorbidities = comorbidities; }

    // MISCELLANEOUS
    @Override
    public String toString ()
    {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy");
        fmt.setCalendar(dateOfBirth);
        String dateFormatted = fmt.format(dateOfBirth.getTime());

        return super.toString() + String.format ("\tDOB: %-15s", dateFormatted);
    }
}
