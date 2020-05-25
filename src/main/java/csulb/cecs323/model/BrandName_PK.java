package csulb.cecs323.model;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class BrandName_PK implements Serializable
{
    private long drug;
    private String name;

    // CONSTRUCTORS
    public BrandName_PK () {}
    public BrandName_PK (Drug drug, String name)
    {
        this.drug = drug.getDID();
        this.name = name;
    }

    // MISCELLANEOUS
    @Override
    public boolean equals (Object other)
    {
        if (other instanceof BrandName_PK)
        {
            BrandName_PK second = (BrandName_PK) other;

            return (this.drug == second.drug) && (this.name.equals (second.name));
        }
        else
            return false;
    }

    @Override
    public int hashCode () { return (int) drug; }

    public String getName () { return this.name; }
    public void setName (String name) { this.name = name; }
}
