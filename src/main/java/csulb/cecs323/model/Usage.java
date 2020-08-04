package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "usages",
        uniqueConstraints = @UniqueConstraint (columnNames = {"indication", "dosage_range"})
)
@NamedQueries({
        @NamedQuery (name = Usage.DELETE_BY_ONE,
                query = "DELETE FROM Usage us WHERE us = :usageObj")
})
public class Usage
{
    public static final String DELETE_BY_ONE = "usage.DeleteOneUsage";

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long usid;

    private boolean fdaApproved;
    private String indication;
    private String dosage_range;

    // ASSOCIATION(S)
    @ManyToOne
    private Drug drug;

    // CONSTRUCTORS
    public Usage () {}
    public Usage (String indication, String dosage, boolean isApproved)
    {
        setApproval(isApproved);
        setIndication(indication);
        setDosageRange(dosage);
    }

    // ACCESSORS
    public boolean getApproval () { return this.fdaApproved; }
    public String getIndication () { return this.indication; }
    public String getDosageRange () { return this.dosage_range; }

    // MUTATORS
    public void setApproval (boolean isApproved) { this.fdaApproved = isApproved; }
    public void setIndication (String indication) { this.indication = indication; }
    public void setDosageRange (String dosage) { this.dosage_range = dosage; }

    // MISCELLANEOUS
    @Override
    public String toString ()
    {
        return String.format ("Indication: %-30s\tDose Range: %-20s", this.indication, this.dosage_range);
    }

}
