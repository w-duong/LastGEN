package csulb.cecs323.model;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name = "drug_schedules")
public class DEA_Class
{
    public static enum DEA {I, II, III, IV, V, F, OTC}

    @Id
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
}
