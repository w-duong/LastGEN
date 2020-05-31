package csulb.cecs323.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "drug_classes",
        uniqueConstraints = @UniqueConstraint (columnNames = {"abbr", "name"})
)
@NamedQueries({
        @NamedQuery (name = DrugClass.LIST_OF_SUPERS,
                     query = "SELECT dc1 FROM DrugClass dc1 INNER JOIN dc1.superclass dc2 WHERE dc1.name = :className"),
        @NamedQuery (name = DrugClass.LIST_OF_SUBS,
                     query = "SELECT dc1 FROM DrugClass dc1 INNER JOIN dc1.subclass dc2 WHERE dc1.name = :className")
})
public class DrugClass
{
    // QUERY STRING(S)
    public static final String LIST_OF_SUPERS = "DrugClass.listOfSupers";
    public static final String LIST_OF_SUBS = "DrugClass.listOfSubs";
    public static final String LIST_OF_DRUGS = "DrugClass.listOfDrugs";

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long cid;
    private String abbr;
    private String name;

    // ASSOCIATION(S)
    @ManyToMany
    @JoinTable (name = "class_parent_children")
    private List<DrugClass> subclass;
    
    @ManyToMany
    @JoinTable (name = "class_drug_tree")
    private List<Drug> drugs;

    @ManyToMany (mappedBy = "subclass")
    private List<DrugClass> superclass;

    @OneToMany (mappedBy = "base", cascade = CascadeType.PERSIST)
    private List<ClassClassIX> interxAsBase;

    @OneToMany (mappedBy = "offender", cascade = CascadeType.PERSIST)
    private List<ClassClassIX> interxAsOffender;

    // CONSTRUCTORS
    public DrugClass () { }
    public DrugClass (String abbreviation, String name)
    {
        setAbbreviation(abbreviation);
        setName(name);
    }
    public DrugClass (DrugClass copy)
    {
        setCID(copy.getCID());
        setAbbreviation(copy.getAbbreviation());
        setName(copy.getName());
    }

    // ACCESSORS
    public long getCID () { return this.cid; }
    public String getAbbreviation () { return this.abbr; }
    public String getName () { return this.name; }

    public List<Drug> getDrugs () { return this.drugs; }
    public List<DrugClass> getSubclasses () { return this.subclass; }
    public List<DrugClass> getSuperclasses () { return this.superclass; }
    public List<ClassClassIX> getInterxAsBase () { return this.interxAsBase; }
    public List<ClassClassIX> getInterxAsOffender () { return this.interxAsOffender; }

    // MUTATORS
    public void setCID (long cid) { this.cid = cid; }
    public void setAbbreviation (String abbr) { this.abbr = abbr; }
    public void setName (String name) { this.name = name; }

    // MISCELLANEOUS
    public void addSubclass (DrugClass child)
    {
        if (this.subclass == null)
            subclass = new ArrayList<>();

        if (! this.subclass.contains (child))
            this.subclass.add (child);

        if ((child.getSuperclasses() == null) || (! child.getSuperclasses().contains (this)))
            child.addSuperclass (this);
    }

    public void addSuperclass (DrugClass parent)
    {
        if (this.superclass == null)
            superclass = new ArrayList<>();

        if (! this.superclass.contains (parent))
            this.superclass.add (parent);

        if ((parent.getSubclasses() == null) || (! parent.getSubclasses().contains (this)))
            parent.addSubclass(this);
    }

    public void addInterxAsBase (ClassClassIX interaction)
    {
        if (this.interxAsBase == null)
            interxAsBase = new ArrayList<>();

        interxAsBase.add (interaction);
    }

    public void addInterxAsOffender (ClassClassIX interaction)
    {
        if (this.interxAsOffender == null)
            interxAsOffender = new ArrayList<>();

        interxAsOffender.add (interaction);
    }

    public void addDrug (Drug drug)
    {
        if (this.drugs == null)
            this.drugs = new ArrayList<>();

        if (! this.drugs.contains (drug))
            this.drugs.add (drug);

        if ((drug.getDrugClass() == null) || (! drug.getDrugClass().contains (this)))
            drug.addDrugClass (this);
    }

    public void removeDrug (Drug drug)
    {
        if ((! (this.drugs == null)) && (this.drugs.contains (drug)))
        {
            this.drugs.remove (drug);

            drug.removeDrugClass (this);
        }
    }
}

