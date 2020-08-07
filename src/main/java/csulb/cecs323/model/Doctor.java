package csulb.cecs323.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "doctors")
@DiscriminatorValue("2")
public class Doctor extends Person
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long md_id;

    private long npiNumber;
    private String deaNumber;
    private String specialty;

    // ASSOCIATION(S)
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.PERSIST)
    private Set<RxLine> rxLines;

    // CONSTRUCTORS
    public Doctor () {}

    // ACCESSORS
    public long getNpiNumber () { return this.npiNumber; }
    public String getDeaNumber () { return this.deaNumber; }
    public String getSpecialty () { return this.specialty; }

    // MUTATORS
    public void setNpiNumber (long npiNumber) { this.npiNumber = npiNumber; }
    public void setDeaNumber (String deaNumber) { this.deaNumber = deaNumber; }
    public void setSpecialty (String specialty) { this.specialty = specialty; }

    // MISCELLANEOUS
    public void addRxLine (RxLine rxLine)
    {
        if (this.rxLines == null)
            this.rxLines = new HashSet<>();

        if (!this.rxLines.contains(rxLine))
        {
            this.rxLines.add(rxLine);
            rxLine.setDoctor(this);
        }
    }
}
