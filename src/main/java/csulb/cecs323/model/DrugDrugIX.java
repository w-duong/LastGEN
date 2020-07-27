package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "drug_drug_interactions")
public class DrugDrugIX
{
    @EmbeddedId
    private DrugDrugIX_PK interaction_CPK;
    private int severityLevel = -1;
    
    // ASSOCIATION(S)
    @ManyToOne (cascade = CascadeType.PERSIST)
    @MapsId ("base")
    private Drug base;
    
    @ManyToOne (cascade = CascadeType.PERSIST)
    @MapsId ("offender")
    private Drug offender;
    
    // CONSTRUCTORS
    public DrugDrugIX () {}
    public DrugDrugIX (Drug base, Drug offender, String describe, int severityLevel)
    {
        interaction_CPK = new DrugDrugIX_PK (base, offender, describe);
        registerInteraction (base, offender);
        setSeverityLevel (severityLevel);
    }
    
    // ACCESSORS
    
    // MUTATORS
    public void setSeverityLevel (int severityLevel) { this.severityLevel = severityLevel; }
    
    // MISCELLANEOUS
//    public void registerInteraction (Drug object, Drug precipitate)
//    {
//        if (this.base == null)
//            this.base = object;
//        if (this.offender == null)
//            this.offender = precipitate;
//
//        if ((this.base == object && this.offender == precipitate) && (severityLevel == -1))
//        {
//            object.addInterxAsBase(this);
//            precipitate.addInterxAsOffender(this);
//        }
//    }

    public void registerInteraction (Drug object, Drug precipitate)
    {
        if (this.base == null)
            this.base = object;
        if (this.offender == null)
            this.offender = precipitate;

        if ((this.base == object && this.offender == precipitate) && (severityLevel == -1))
            object.addInterxAsBase(this);
    }

    @Override
    public String toString ()
    {
        return String.format ("Base: %-20s\tOffender: %-20s\tSVL > %-3d", base.getChemical_name(),
                offender.getChemical_name(), severityLevel);
    }

}