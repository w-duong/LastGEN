package csulb.cecs323.model;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class ClassClassIX_PK implements Serializable
{
    private long base;
    private long offender;
    @Column(length = 500)
    private String description;

    // CONSTRUCTORS
    public ClassClassIX_PK () {}
    public ClassClassIX_PK (DrugClass base, DrugClass offender, String description)
    {
        this.base = base.getCID();
        this.offender = offender.getCID();
        this.description = description;
    }
    
    // ACCESSORS
    public String getDescription () { return this.description; }

    // MISCELLANEOUS
    @Override
    public boolean equals (Object other)
    {
        if (other instanceof ClassClassIX_PK)
        {
            ClassClassIX_PK second = (ClassClassIX_PK) other;

            return (this.base == second.base) && (offender == second.offender) && (this.description.equals (description));
        }
        else
            return false;
    }

    @Override
    public int hashCode () { return (int) (base + offender); }
}
