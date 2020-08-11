package csulb.cecs323.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "prescribers")
@DiscriminatorValue("2")
public class Prescriber extends Person
{
    private long npiNumber;
    private String deaNumber;
    private String specialty;

    // ASSOCIATION(S)
    @OneToMany(mappedBy = "prescriber", cascade = CascadeType.PERSIST)
    private Set<Prescription> prescriptions;

    // CONSTRUCTORS
    public Prescriber() {}
    public Prescriber(String first, String last, String dea)
    {
        super(first, last);
        setDeaNumber(dea);
    }
    public Prescriber(String first, String last, Address address, Phone phone, String dea)
    {
        super(first, last, address, phone);
        setDeaNumber(dea);
    }

    // ACCESSORS
    public long getNpiNumber () { return this.npiNumber; }
    public String getDeaNumber () { return this.deaNumber; }
    public String getSpecialty () { return this.specialty; }

    // MUTATORS
    public void setNpiNumber (long npiNumber) { this.npiNumber = npiNumber; }
    public void setDeaNumber (String deaNumber) { this.deaNumber = deaNumber; }
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
