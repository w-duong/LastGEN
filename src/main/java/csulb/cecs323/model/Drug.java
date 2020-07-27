package csulb.cecs323.model;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name = "drugs")
@NamedQueries({
        @NamedQuery (name = Drug.FIND_ALL_BY_NAME,
                     query = "SELECT d FROM Drug d WHERE LOWER (d.chemical_name) LIKE LOWER (CONCAT ('%', :searchString, '%'))" )
})
public class Drug
{
    // QUERY STRING(S)
    public static final String FIND_ALL_BY_NAME = "Drug.findAllByName";

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long did;

    private String chemical_name;
    private String description;

    //<--TO DO: more attributes --> //
    // -contraindication ?
    // -side-effects ?

    // ASSOCIATION(S)
    @ManyToOne (cascade = CascadeType.PERSIST)
    private DEA_Class schedule;

    @OneToMany (mappedBy = "drug", cascade = CascadeType.PERSIST)
    private List<Pharmacology> PK_profiles;

    @OneToMany (mappedBy = "drug", cascade = CascadeType.PERSIST)
    private List<Usage> usages;
    
    @OneToMany (mappedBy = "generic", cascade = CascadeType.PERSIST)
    private List<BrandName> brand_names;
    
    @OneToMany (mappedBy = "base", cascade = CascadeType.PERSIST)
    private List<DrugDrugIX> interxAsBase;
    
    @ManyToMany (mappedBy = "drugs")
    private List<DrugClass> classes;
    
    // CONSTRUCTORS
    public Drug () {}
    public Drug (String chemical_name, String description)
    {
        setChemicalName (chemical_name);
        setDescription (description);
    }
    public Drug (String chemical_name, String brand_name, String description, DrugClass parent_class)
    {
        setChemicalName(chemical_name);
        addBrandName(brand_name);
        setDescription(description);
        addDrugClass(parent_class);
    }

    // ACCESSORS
    public long getDID () { return this.did; }
    public String getChemical_name () { return this.chemical_name; }
    public String getDescription () { return this.description; }
    public DEA_Class getDrugSchedule () { return this.schedule; }

    public List<BrandName> getBrandNames () { return this.brand_names; }
    public List<DrugClass> getDrugClass () { return this.classes; }
    public List<Pharmacology> getPharmacology () { return this.PK_profiles; }
    public List<DrugDrugIX> getDrugInteractions () { return this.interxAsBase; }

    // MUTATORS
    public void setChemicalName (String chemical_name) { this.chemical_name = chemical_name; }
    public void setDescription (String description) { this.description = description; }
    public void setDrugSchedule (DEA_Class schedule)
    {
        DEA_Class temp = null;

        if (this.schedule == null)
            this.schedule = schedule;
        else
        {
            temp = this.schedule;
            temp.getDrugs().remove (this);
            this.schedule = schedule;
        }

        if ((schedule.getDrugs () == null) || (! schedule.getDrugs().contains (this)))
            schedule.addDrug (this);
    }

    public void setPharmacology (List<Pharmacology> pharmacology)
    {
        for (Pharmacology pgy : pharmacology)
            this.addPKProfile(pgy);
    }
    public void setBrandNames (List<BrandName> brands)
    {
        for (BrandName bns : brands)
            this.addBrandname(bns);
    }
    public void setDDInterx (List<DrugDrugIX> interactions)
    {
        for (DrugDrugIX ddix : interactions)
            this.addInterxAsBase(ddix);
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
    public void addBrandname (BrandName brand)
    {
        if (this.brand_names == null)
            this.brand_names = new ArrayList<>();

        if (!this.brand_names.contains(brand))
            this.brand_names.add(brand);
    }

    public void addUsage (String indication, String doseRange, boolean isApproved)
    {
        Usage newIndication = new Usage (indication, doseRange, isApproved);

        addUsage(newIndication);
    }
    public void addUsage (Usage newIndication)
    {
        if (this.usages == null)
            this.usages = new ArrayList<>();

        usages.add(newIndication);
    }

    public void addPKProfile (String organ, String enzyme, String elim_route)
    {
        Pharmacology profile = new Pharmacology(organ, enzyme, elim_route);

        addPKProfile(profile);
    }
    public void addPKProfile (Pharmacology pharmacology)
    {
        if (this.PK_profiles == null)
            PK_profiles = new ArrayList<>();

        if (!PK_profiles.contains(pharmacology))
            PK_profiles.add(pharmacology);
    }

    public void addInterxAsBase (Drug other, String description, int severityLevel)
    {
        DrugDrugIX interaction = new DrugDrugIX (this, other, description, severityLevel);
        DrugDrugIX reciprocate = new DrugDrugIX (other, this,description, severityLevel);
    }
    public void addInterxAsBase (DrugDrugIX interaction)
    {
        if (this.interxAsBase == null)
            interxAsBase = new ArrayList<>();

        if (!this.interxAsBase.contains(interaction))
            interxAsBase.add (interaction);
    }

    public void addDrugClass (DrugClass parent)
    {
        if (this.classes == null)
            this.classes = new ArrayList<>();

        if (! this.classes.contains (parent))
            this.classes.add (parent);

        if ((parent.getDrugs() == null) || (! parent.getDrugs().contains (this)))
            parent.addDrug(this);
    }

    public void removeDrugClass (DrugClass parent)
    {
        if ((! (this.classes == null)) && (this.classes.contains (parent)))
        {
            this.classes.remove (parent);

            parent.removeDrug (this);
        }
    }

    @Override
    public String toString ()
    {
        /*
        *  NECESSARY, 'List' are not instantiated on invokation of 'toString'
        *  (i.e. will not trigger a database read) -- needs workaround
        * */
        List<BrandName> temp = new ArrayList<>(this.brand_names);

        return (String.format ("Chemical: %-15s\tBrand: %-10s\tSchedule: %-3s",
                                chemical_name, brand_names.toString(), schedule.getSchedule()) );
    }
}
