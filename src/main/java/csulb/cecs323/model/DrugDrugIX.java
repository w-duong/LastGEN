package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "drug_drug_interactions")
@NamedQueries({
        @NamedQuery (name = DrugDrugIX.DELETE_BY_ONE, query = "DELETE FROM DrugDrugIX ddix WHERE ddix = :ixObject")
})
public class DrugDrugIX
{
    public static final String DELETE_BY_ONE = "DrugDrugIX.deleteOneIX";

    @EmbeddedId
    private DrugDrugIX_PK interaction_CPK;
    private int severityLevel = -1;
    
    // ASSOCIATION(S)
    @ManyToOne
    @MapsId ("base")
    private Drug base;
    
    @ManyToOne
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
    public Drug getOffender () { return this.offender; }
    public int getSeverityLevel () { return this.severityLevel; }
    public String getDescription () { return interaction_CPK.getDescription(); }
    
    // MUTATORS
    public void setSeverityLevel (int severityLevel) { this.severityLevel = severityLevel; }
    
    // MISCELLANEOUS
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