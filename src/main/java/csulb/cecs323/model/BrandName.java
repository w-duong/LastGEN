package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "brand_names")
public class BrandName
{
    @EmbeddedId
    private BrandName_PK brand_CPK;
    
    // ASSOCIATION(S)
    @ManyToOne
    @MapsId ("drug")
    private Drug generic;
    
    // CONSTRUCTORS
    public BrandName () {}
    public BrandName (Drug generic, String name)
    {
        brand_CPK = new BrandName_PK (generic, name);
        this.generic = generic;
    }
    
    // ACCESSORS
    public String getInfo ()
    {
        return (String.format ("CHEMICAL: %-20s\tLabel: %-15s", generic.getChemical_name(), this.toString()) );
    }
    
    // MUTATORS
    public void setName (String name) { this.brand_CPK.setName(name); }
    
    // MISCELLANEOUS
//    @Override
//    public boolean equals (Object other)
//    {
//        if (other instanceof BrandName)
//        {
//            BrandName second = (BrandName) other;
//            return (this.brand_CPK == second.brand_CPK);
//        }
//        else
//            return false;
//    }

    @Override
    public String toString () { return brand_CPK.getName (); }
}