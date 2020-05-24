package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "brand_names"
        uniqueConstraints = @UniqueConstraint (columnNames = {"name", "maker"})        
)
public class BrandName
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long bid
    
    private String name;
    private String maker;
    
    // ASSOCIATION(S)
    @ManyToOne
    private Drug generic;
    
    // CONSTRUCTORS
    
    // ACCESSORS
    
    // MUTATORS
    
    // MISCELLANEOUS
}