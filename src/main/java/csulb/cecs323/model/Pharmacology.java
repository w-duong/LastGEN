package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "pharmacology_profiles")
@NamedQueries({
        @NamedQuery (name = Pharmacology.GET_ALL_ENZYMES,
                     query = "SELECT pgy.clearance_enzyme FROM Pharmacology pgy"),
        @NamedQuery (name = Pharmacology.DELETE_BY_ONE, query = "DELETE FROM Pharmacology pgy WHERE pgy = :pkObject")
})
public class Pharmacology
{
    // QUERY STRING(S)
    public static final String GET_ALL_ENZYMES = "Pharmacology.getAllEnzymes";
    public static final String DELETE_BY_ONE = "Pharmacology.deleteIndividualPK";

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
    public Pharmacology (Drug drug, String organ, String enzyme, String elim_route)
    {
        setDrug(drug);
        setClearanceOrgan(organ);
        setClearanceEnzyme(enzyme);
        setEliminationRoute(elim_route);
    }

    // ACCESSORS
    public Drug getDrug () { return this.drug; }
    public String getClearanceOrgan () { return this.clearance_organ; }
    public String getClearanceEnzyme () { return this.clearance_enzyme; }
    public String getEliminationRoute () { return this.elimination_route; }

    // MUTATORS
    public void setDrug (Drug drug) { this.drug = drug; }
    public void setClearanceOrgan (String organ) { this.clearance_organ = organ; }
    public void setClearanceEnzyme (String enzyme) { this.clearance_enzyme = enzyme; }
    public void setEliminationRoute (String elim_route) { this.elimination_route = elim_route; }

    // MISCELLANEOUS
    @Override
    public String toString ()
    {
        String organ = (this.clearance_organ.length() > 20) ? this.clearance_organ.substring(0,17) + "..." : this.clearance_organ;
        String enzyme = (this.clearance_enzyme.length() > 20) ? this.clearance_enzyme.substring(0,17) + "..." : this.clearance_enzyme;
        String elim = (this.elimination_route.length() > 20) ? this.elimination_route.substring(0,17) + "..." : this.elimination_route;

        return String.format ("Organ: %-20s\tEnzyme: %-20s\tElimination: %-20s", organ, enzyme, elim);
    }

    public String fullString ()
    {
        return String.format ("Drug: %s\tOrgan: %s\tEnzyme: %s\tElimination: %s",
                drug.toString(), clearance_organ, clearance_enzyme, elimination_route);
    }
}
