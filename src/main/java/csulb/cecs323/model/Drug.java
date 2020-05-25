package csulb.cecs323.model;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name = "drugs")
public class Drug
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long did;

    private String chemical_name;
    private String description;

    // ASSOCIATION(S)
    @ManyToOne
    private DEA_Class schedule;
    
    @OneToMany (mappedBy = "generic", cascade = CascadeType.PERSIST)
    private List<BrandName> brand_names;
    
    @OneToMany (mappedBy = "base", cascade = CascadeType.PERSIST)
    private List<DrugDrugIX> interxAsBase;
    
    @OneToMany (mappedBy = "offender", cascade = CascadeType.PERSIST)
    private List<DrugDrugIX> interxAsOffender;
    
    @ManyToMany (mappedBy = "drugs")
    private List<DrugClass> classes;
    
    // CONSTRUCTORS
    public Drug () {}
    public Drug (String chemical_name, String description)
    {
        setChemicalName (chemical_name);
        setDescription (description);
    }
    
    // ACCESSORS
    public long getDID () { return this.did; }
    public DEA_Class getDrugSchedule () { return this.schedule; }
    public List<BrandName> getBrandNames () { return this.brand_names; }
    public List<DrugClass> getDrugClass () { return this.classes; }
    
    public List<DrugDrugIX> getInterxAsBase () { return this.interxAsBase; }
    public List<DrugDrugIX> getInterxAsOffender () { return this.interxAsOffender; }
    
    // MUTATORS
    public void setChemicalName (String chemical_name) { this.chemical_name = chemical_name; }
    public void setDescription (String description) { this.description = description; }
    public void setDrugSchedule (DEA_Class schedule)
    {
        if (this.schedule == null)
            this.schedule = schedule;
            
        if (schedule.getDrugs () == null)
            schedule.addDrug (this);
            
        if (! schedule.getDrugs().contains (this))
            schedule.addDrug (this);
    }
    
    // MISCELLANEOUS
    public void addBrandName (String name)
    {
        if (this.brand_names == null)
            this.brand_names = new ArrayList<>();

        BrandName newLabel = new BrandName(this, name);

        if (! this.brand_names.contains(newLabel))
            this.brand_names.add (newLabel);
    }

    public void addInterxAsBase (DrugDrugIX interaction)
    {
        if (this.interxAsBase == null)
            interxAsBase = new ArrayList<>();

        interxAsBase.add (interaction);
    }

    public void addInterxAsOffender (DrugDrugIX interaction)
    {
        if (this.interxAsOffender == null)
            interxAsOffender = new ArrayList<>();

        interxAsOffender.add (interaction);
    }
}
