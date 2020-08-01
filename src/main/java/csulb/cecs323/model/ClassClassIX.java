package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "class_class_interactions")
public class ClassClassIX
{
    @EmbeddedId
    private ClassClassIX_PK interaction_CPK;
    private int severityLevel = -1;

    // ASSOCIATION(S)
    @ManyToOne
    @MapsId ("base")
    private DrugClass base;

    @ManyToOne
    @MapsId ("offender")
    private DrugClass offender;

    // CONSTRUCTORS
    public ClassClassIX () {}
    public ClassClassIX (DrugClass base, DrugClass offender, String describe, int severityLevel)
    {
        interaction_CPK = new ClassClassIX_PK(base, offender, describe);
        registerInteraction(base, offender);
        setSeverityLevel(severityLevel);
    }

    // ACCESSORS
    public ClassClassIX_PK getInteractionCPK () { return interaction_CPK; }
    public String getDescription() { return this.interaction_CPK.getDescription(); }
    public int getSeverityLevel() { return severityLevel; }
    public DrugClass getBase () { return this.base; }
    public DrugClass getOffender () { return this.offender; }

    // MUTATORS
    public void setSeverityLevel (int severityLevel) { this.severityLevel = severityLevel; }
    public void setBase (DrugClass base) { this.base = base; }
    public void setOffender (DrugClass offender) { this.offender = offender; }

    // MISCELLANEOUS
    public void registerInteraction (DrugClass object, DrugClass precipitate)
    {
        if (this.base == null)
            this.base = object;
        if (this.offender == null)
            this.offender = precipitate;

        if ((this.base == object && this.offender == precipitate) && (severityLevel == -1))
            object.addInterxAsBase(this);
    }
}
