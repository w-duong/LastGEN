package csulb.cecs323.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "prescribers")
@DiscriminatorValue("2")
public class Prescriber extends Person
{
    private String specialty;

    // ASSOCIATION(S)
    @OneToMany(mappedBy = "prescriber", cascade = CascadeType.PERSIST)
    private Set<Prescription> prescriptions;

    // CONSTRUCTORS
    public Prescriber() {}
    public Prescriber(String first, String last, ProviderCertification certification)
    {
        super(first, last);
        super.addCertification(certification);
    }
    public Prescriber(String first, String last, Address address, Phone phone, String dea)
    {
        super(first, last, address, phone);
        ProviderCertification newDea = new ProviderCertification(this, "DEA", dea, "DEA");
    }

    // ACCESSORS
    public String getSpecialty () { return this.specialty; }

    // MUTATORS
    public void setSpecialty (String specialty) { this.specialty = specialty; }

    // MISCELLANEOUS
    public void addPrescription (Prescription prescription)
    {
        if (this.prescriptions == null)
            this.prescriptions = new HashSet<>();

        if (!this.prescriptions.contains(prescription))
        {
            this.prescriptions.add(prescription);
            prescription.setPrescriber(this);
        }
    }
}
