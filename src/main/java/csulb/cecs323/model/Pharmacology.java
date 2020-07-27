package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "pharmacology_profiles")
@NamedQueries({
        @NamedQuery (name = Pharmacology.GET_ALL_ENZYMES,
                     query = "SELECT pgy.clearance_enzyme FROM Pharmacology pgy")
})
public class Pharmacology
{
    // QUERY STRING(S)
    public static final String GET_ALL_ENZYMES = "Pharmacology.getAllEnzymes";

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long ppid;

    private String clearance_organ;
    private String clearance_enzyme;
    private String elimination_route;

    // ASSOCIATION(S)
    @ManyToOne
    private Drug drug;

    // CONSTRUCTORS
    public Pharmacology () {}
    public Pharmacology (String organ, String enzyme, String elim_route)
    {
        setClearanceOrgan(organ);
        setClearanceEnzyme(enzyme);
        setEliminationRoute(elim_route);
    }

    // ACCESSORS
    public String getClearanceOrgan () { return this.clearance_organ; }
    public String getClearanceEnzyme () { return this.clearance_enzyme; }
    public String getEliminationRoute () { return this.elimination_route; }

    // MUTATORS
    public void setClearanceOrgan (String organ) { this.clearance_organ = organ; }
    public void setClearanceEnzyme (String enzyme) { this.clearance_enzyme = enzyme; }
    public void setEliminationRoute (String elim_route) { this.elimination_route = elim_route; }

    // MISCELLANEOUS
}
