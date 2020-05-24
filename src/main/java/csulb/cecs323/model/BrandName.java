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
    private Drug generic;
    
    // CONSTRUCTORS
    public BrandName () {}
    public BrandName (Drug generic, String name)
    {
        brand_CPK = new BrandName_PK (generic, name);
    }
    
    // ACCESSORS
    public String getName () { return brand_CPK.getName (); }
    
    // MUTATORS
    
    // MISCELLANEOUS
}