package csulb.cecs323.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

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
    @Column(length = 1000)
    private String description;

    //<--TO DO: more attributes --> //
    // -contraindication ?
    // -side-effects ?

    // ASSOCIATION(S)
    @ManyToOne (cascade = CascadeType.PERSIST)
    private DEA_Class schedule;

    @OneToMany (mappedBy = "drug", cascade = CascadeType.ALL)
    private List<Pharmacology> PK_profiles;

    @OneToMany (mappedBy = "drug", cascade = CascadeType.ALL)
    private List<Usage> usages;
    
    @OneToMany (mappedBy = "generic", cascade = CascadeType.ALL)
    private List<BrandName> brand_names;

    // consider switching to Set<DrugDrugIX> (???)
    @OneToMany (mappedBy = "base", cascade = CascadeType.PERSIST)
    private List<DrugDrugIX> interxAsBase;
    
    @ManyToMany (mappedBy = "drugs")
    private List<DrugClass> classes;

    @OneToMany (mappedBy = "drug", cascade = CascadeType.PERSIST)
    private Set<RxLine> rxLines;
    
    // CONSTRUCTORS
    public Drug () {}
    public Drug (String chemical_name, String description)
    {
        setChemicalName (chemical_name);
        setDescription (description);

        this.schedule = null;
        this.brand_names = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.PK_profiles = new ArrayList<>();
        this.usages = new ArrayList<>();
        this.interxAsBase = new ArrayList<>();
    }
    public Drug (String chemical_name, String brand_name, String description, DrugClass parent_class)
    {
        setChemicalName(chemical_name);
        addBrandName(brand_name);
        setDescription(description);
        addDrugClass(parent_class);
    }
    public Drug (Drug copy)
    {
        setChemicalName(copy.getChemical_name());
        setDescription(copy.getDescription());
        setDrugSchedule(copy.getDrugSchedule());

        if (copy.getBrandNames().size() == 0)
            this.brand_names = new ArrayList<>();
        else
            for (BrandName bns : copy.getBrandNames())
                addBrandName(bns);

        if (copy.getDrugClass().size() == 0)
            this.classes = new ArrayList<>();
        else
            for (DrugClass parent : copy.getDrugClass())
                addDrugClass(parent);

        if (copy.getDrugInteractions().size() == 0)
            this.interxAsBase = new ArrayList<>();
        else
            for (DrugDrugIX interx: copy.getDrugInteractions())
                addInterxAsBase(interx.getOffender(), interx.getDescription(), interx.getSeverityLevel());

        if (copy.getUsages().size() == 0)
            this.usages = new ArrayList<>();
        else
            for (Usage indication: copy.getUsages())
                addUsage(indication.getIndication(), indication.getDosageRange(), indication.getApproval());

        if (copy.getPharmacology().size() == 0)
            this.PK_profiles = new ArrayList<>();
        else
            for (Pharmacology profile: copy.getPharmacology())
                addPKProfile(profile.getClearanceOrgan(), profile.getClearanceEnzyme(), profile.getEliminationRoute());
    }

    // ACCESSORS
    public long getDID () { return this.did; }
    public String getChemical_name () { return this.chemical_name; }
    public String getDescription () { return this.description; }
    public DEA_Class getDrugSchedule () { return this.schedule; }

    public List<BrandName> getBrandNames () { return this.brand_names; }
    public List<DrugClass> getDrugClass () { return this.classes; }
    public List<Pharmacology> getPharmacology () { return this.PK_profiles; }
    public List<Usage> getUsages () { return this.usages; }
    public List<DrugDrugIX> getDrugInteractions () { return this.interxAsBase; }

    // MUTATORS
    public void setChemicalName (String chemical_name) { this.chemical_name = chemical_name; }
    public void setDescription (String description) { this.description = description; }
    public void setDrugSchedule (DEA_Class schedule)
    {
        DEA_Class temp = null;

        /*
        *  If this Drug has a Schedule currently assigned, swap it out for the incoming parameter. Then have the
        *  old Schedule remove this Drug from it's list of Drugs.
        */
        if (this.schedule == null)
            this.schedule = schedule;
        else
        {
            temp = this.schedule;
            temp.getDrugs().remove (this);
            this.schedule = schedule;
        }

        /* New assigned Schedule should add this Drug to it's records */
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
            this.addBrandName(bns);
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
    public void removeBrandName (String name)
    {
        if (this.brand_names != null)
            this.brand_names.removeIf(bns -> bns.toString().equals(name));
    }
    protected void addBrandName (BrandName brand)
    {
        if (this.brand_names == null)
            this.brand_names = new ArrayList<>();

        if (!this.brand_names.contains(brand))
            this.brand_names.add(brand);
    }

    public void addUsage (String indication, String doseRange, boolean isApproved)
    {
        Usage newIndication = new Usage (this, indication, doseRange, isApproved);

        addUsage(newIndication);
    }
    protected void addUsage (Usage newIndication)
    {
        if (this.usages == null)
            this.usages = new ArrayList<>();

        if (!usages.contains(newIndication))
            usages.add(newIndication);
    }

    public void addPKProfile (String organ, String enzyme, String elim_route)
    {
        Pharmacology profile = new Pharmacology(this, organ, enzyme, elim_route);

        addPKProfile(profile);
    }
    protected void addPKProfile (Pharmacology pharmacology)
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

    public void addRxLine (RxLine rxLine)
    {
        if (this.rxLines == null)
            this.rxLines = new HashSet<>();

        if (!this.rxLines.contains(rxLine))
        {
            this.rxLines.add(rxLine);
            rxLine.setDrug(this);
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
