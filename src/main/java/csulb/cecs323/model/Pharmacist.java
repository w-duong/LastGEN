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
    private String position;

    // ASSOCIATION(S)
    @OneToMany(mappedBy = "pharmacist", cascade = CascadeType.PERSIST)
    private Set<RxLine> rxLines;

    // CONSTRUCTORS
    public Pharmacist () {}
    public Pharmacist (String first, String last, ProviderCertification license)
    {
        super(first, last);
        super.addCertification(license);
    }
    public Pharmacist (String first, String last, Address address, Phone phone, String license, String state)
    {
        super(first, last, address, phone);
        ProviderCertification newCert = new ProviderCertification(this, "State License", license, state);
    }

    // ACCESSORS
    public String getPosition () { return this.position; }

    // MUTATORS
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
