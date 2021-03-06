package csulb.cecs323.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "drug_classes",
        uniqueConstraints = @UniqueConstraint (columnNames = {"abbr", "name"})
)
@NamedQueries({
        @NamedQuery (name = DrugClass.FIND_ALL_BY_NAME,
                     query = "SELECT dc FROM DrugClass dc WHERE LOWER (dc.name) LIKE LOWER (CONCAT ('%',:searchString,'%') )"),
        @NamedQuery (name = DrugClass.LIST_OF_SUPERS,
                     query = "SELECT dc1 FROM DrugClass dc1 INNER JOIN dc1.superclass dc2 WHERE dc1.name = :className"),
        @NamedQuery (name = DrugClass.LIST_OF_SUBS,
                     query = "SELECT dc1 FROM DrugClass dc1 INNER JOIN dc1.subclass dc2 WHERE dc1.name = :className")
})
public class DrugClass
{
    // QUERY STRING(S)
    public static final String FIND_ALL_BY_NAME = "DrugClass.findAll";
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

    @OneToMany (mappedBy = "base", cascade = CascadeType.ALL)
    private List<ClassClassIX> interxAsBase;

    // CONSTRUCTORS
    public DrugClass () { }
    public DrugClass (String abbreviation, String name)
    {
        setAbbreviation(abbreviation);
        setName(name);

        this.drugs = new ArrayList<>();
        this.subclass = new ArrayList<>();
        this.superclass = new ArrayList<>();
        this.interxAsBase = new ArrayList<>();
    }
    public DrugClass (DrugClass copy)
    {
        setAbbreviation(copy.getAbbreviation());
        setName(copy.getName());

        if (copy.getDrugs().size() == 0)
            this.drugs = new ArrayList<>();
        else
            for (Drug drug : copy.getDrugs())
                this.addDrug(drug);

        if (copy.getSuperclasses().size() == 0)
            this.superclass = new ArrayList<>();
        else
            for (DrugClass parent : copy.getSuperclasses())
                this.addSuperclass(parent);

        if (copy.getSubclasses().size() == 0)
            this.subclass = new ArrayList<>();
        else
            for (DrugClass child : copy.getSubclasses())
                this.addSubclass(child);

        if (copy.getInterxAsBase().size() == 0)
            this.interxAsBase = new ArrayList<>();
        else
            for (ClassClassIX interx : copy.getInterxAsBase())
                this.addInterxAsBase(interx.getOffender(), interx.getDescription(), interx.getSeverityLevel());
    }

    // ACCESSORS
    public long getCID () { return this.cid; }
    public String getAbbreviation () { return this.abbr; }
    public String getName () { return this.name; }

    public List<Drug> getDrugs () { return this.drugs; }
    public List<DrugClass> getSubclasses () { return this.subclass; }
    public List<DrugClass> getSuperclasses () { return this.superclass; }
    public List<ClassClassIX> getInterxAsBase () { return this.interxAsBase; }

    // MUTATORS
    public void setCID (long cid) { this.cid = cid; }
    public void setAbbreviation (String abbr) { this.abbr = abbr; }
    public void setName (String name) { this.name = name; }
    public void setDrugs (ArrayList<Drug> drugs) { this.drugs = drugs; }

    // MISCELLANEOUS
    @Override
    public String toString ()
    {
        return String.format ("CID > %-5d\tClass Name > %-30s", this.cid, this.name);
    }

    public void addSubclass (DrugClass child) // TO DO: if Super/Sub class have drug list, import list (???)
    {
        if (this.subclass == null)
            subclass = new ArrayList<>();

        if (! this.subclass.contains (child))
            this.subclass.add (child);

        if ((child.getSuperclasses() == null) || (! child.getSuperclasses().contains (this)))
            child.addSuperclass (this);
    }

    public void removeSubclass (DrugClass child)
    {
        if (this.subclass.contains (child))
        {
            this.subclass.remove (child);

            child.removeSuperclass(this);
        }
    }

    public void addSuperclass (DrugClass parent) // TO DO: if Super/Sub class have drug list, import list (???)
    {
        if (this.superclass == null)
            superclass = new ArrayList<>();

        if (! this.superclass.contains (parent))
            this.superclass.add (parent);

        if ((parent.getSubclasses() == null) || (! parent.getSubclasses().contains (this)))
            parent.addSubclass(this);
    }

    public void removeSuperclass (DrugClass parent)
    {
        if (this.superclass.contains (parent))
        {
            this.superclass.remove (parent);

            parent.removeSubclass (this);
        }
    }

    public void addInterxAsBase (DrugClass other, String description, int severityLevel)
    {
        ClassClassIX interaction = new ClassClassIX (this, other, description, severityLevel);
        ClassClassIX reciprocate = new ClassClassIX (other, this, description, severityLevel);
    }

    protected void addInterxAsBase (ClassClassIX interaction)
    {
        if (this.interxAsBase == null)
            interxAsBase = new ArrayList<>();

        if (!this.interxAsBase.contains(interaction))
            interxAsBase.add (interaction);
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

