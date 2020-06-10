package csulb.cecs323.model;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name = "drug_schedules")
@NamedQueries({
        @NamedQuery (name = DEA_Class.FIND_ALL, query = "SELECT ds FROM DEA_Class ds"),
        @NamedQuery (name = DEA_Class.SCHEDULE_OF_DRUGS,
                     query = "SELECT d FROM DEA_Class ds INNER JOIN ds.drugs d WHERE ds.symbol = :drugSchedule")
})
public class DEA_Class
{
    // QUERY STRING(S)
    public static final String FIND_ALL = "DEA_Class.findAll";
    public static final String SCHEDULE_OF_DRUGS = "DEA_Class.scheduleOfDrugs";

    public static enum DEA {RX, INV, I, II, III, IV, V, F, OTC}

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long dea_id;
    private DEA symbol;
    private String description;

    // ASSOCIATION(S)
    @OneToMany (mappedBy = "schedule", cascade = CascadeType.PERSIST)
    private List<Drug> drugs;

    // CONSTRUCTORS
    public DEA_Class () {}
    public DEA_Class (DEA symbol, String description)
    {
        setSymbol(symbol);
        setDescription(description);
    }

    // ACCESSORS
    public DEA_Class.DEA getSymbol () { return this.symbol; }
    public int getSchedule () { return this.symbol.ordinal(); }
    public String getDescription () { return this.description; }
    public List<Drug> getDrugs () { return this.drugs; }

    // MUTATORS
    public void setSymbol (DEA symbol) { this.symbol = symbol; }
    public void setDescription (String description) { this.description = description; }

    // MISCELLANEOUS
    public void addDrug (Drug drug)
    {
        if (this.drugs == null)
            drugs = new ArrayList<>();
            
        if (! this.drugs.contains (drug))
            drugs.add (drug);
    }

    @Override
    public String toString ()
    {
        return (String.format("Schedule %-3s\tDescription: %s", symbol.toString(), description) );
    }
}
