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
    private String clinical_pharmacology;
    private String dosage;

    //<--TO DO: more attributes --> //
    // -contraindication ?
    // -side-effects ?

    // ASSOCIATION(S)
    @ManyToOne (cascade = CascadeType.PERSIST)
    private DEA_Class schedule;
    
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
    public String getPharmacology () { return this.clinical_pharmacology; }
    public String getDosage () { return this.dosage; }
    public DEA_Class getDrugSchedule () { return this.schedule; }
    public List<BrandName> getBrandNames () { return this.brand_names; }
    public List<DrugClass> getDrugClass () { return this.classes; }

    public List<DrugDrugIX> getInterxAsBase () { return this.interxAsBase; }

    // MUTATORS
    public void setChemicalName (String chemical_name) { this.chemical_name = chemical_name; }
    public void setDescription (String description) { this.description = description; }
    public void setPharmacology (String pharmacology) { this.clinical_pharmacology = pharmacology; }
    public void setDosage (String dosageGuide) { this.dosage = dosageGuide; }
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
    
    // MISCELLANEOUS
    public void addBrandName (String name)
    {
        if (this.brand_names == null)
            this.brand_names = new ArrayList<>();

        BrandName newLabel = new BrandName(this, name);

        if (! this.brand_names.contains(newLabel))
            this.brand_names.add (newLabel);
    }

    public void addInterxAsBase (Drug other, String description, int severityLevel)
    {
        DrugDrugIX interaction = new DrugDrugIX (this, other, description, severityLevel);
    }

    public void addInterxAsBase (DrugDrugIX interaction)
    {
        if (this.interxAsBase == null)
            interxAsBase = new ArrayList<>();

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
        return (String.format ("Chemical: %-15s\tBrand: %-10s\tSchedule: %-3s",
                                chemical_name, brand_names.toString(), schedule.getSchedule()) );
    }
}
