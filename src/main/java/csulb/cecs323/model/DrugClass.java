package csulb.cecs323.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "drug_classes",
        uniqueConstraints = @UniqueConstraint (columnNames = {"abbr", "name"})
)
public class DrugClass
{
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
}

