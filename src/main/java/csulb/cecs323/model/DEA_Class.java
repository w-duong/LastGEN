package csulb.cecs323.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "drug_schedules")
public class DEA_Class
{
    public static enum DEA {I, II, III, IV, V}

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

    // MUTATORS
    public void setSymbol (DEA symbol) { this.symbol = symbol; }
    public void setDescription (String description) { this.description = description; }

    // MISCELLANEOUS
}
