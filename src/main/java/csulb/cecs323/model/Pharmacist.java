package csulb.cecs323.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pharmacists")
@DiscriminatorValue("3")
public class Pharmacist extends Person
{
    private long npiNumber;
    private String license;
    private String position;

    // ASSOCIATION(S)
    @OneToMany(mappedBy = "pharmacist", cascade = CascadeType.PERSIST)
    private Set<RxLine> rxLines;

    // CONSTRUCTORS
    public Pharmacist () {}
    public Pharmacist (String first, String last, String license)
    {
        super(first, last);
        setLicense(license);
    }
    public Pharmacist (String first, String last, Address address, Phone phone, String license)
    {
        super(first, last, address, phone);
        setLicense(license);
    }

    // ACCESSORS
    public long getNpiNumber () { return this.npiNumber; }
    public String getLicense () { return this.license; }
    public String getPosition () { return this.position; }

    // MUTATORS
    public void setNpiNumber (long npiNumber) { this.npiNumber = npiNumber; }
    public void setLicense (String license) { this.license = license; }
    public void setPosition (String position) { this.position = position; }

    // MISCELLANEOUS
    public void addRxLine (RxLine rxLine)
    {
        if (this.rxLines == null)
            this.rxLines = new HashSet<>();

        if (!this.rxLines.contains(rxLine))
        {
            this.rxLines.add(rxLine);
            rxLine.setPharmacist(this);
        }
    }
}
