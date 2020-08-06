package csulb.cecs323.model;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class DrugDrugIX_PK implements Serializable
{
    private long base;
    private long offender;
    @Column(length = 500)
    private String description;

    // CONSTRUCTORS
    public DrugDrugIX_PK () {}
    public DrugDrugIX_PK (Drug base, Drug offender, String description)
    {
        this.base = base.getDID();
        this.offender = offender.getDID();
        this.description = description;
    }
    
    // ACCESSORS
    public String getDescription () { return this.description; }

    // MISCELLANEOUS
    @Override
    public boolean equals (Object other)
    {
        if (other instanceof DrugDrugIX_PK)
        {
            DrugDrugIX_PK second = (DrugDrugIX_PK) other;

            return (this.base == second.base) && (offender == second.offender) && (this.description.equals (description));
        }
        else
            return false;
    }

    public String fullString ()
    {
        return String.format ("BaseID: %d\tOffenderID: %d\tDescription: %s", base, offender, description);
    }

    @Override
    public int hashCode () { return (int) (base + offender); }
}
