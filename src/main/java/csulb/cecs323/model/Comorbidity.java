package csulb.cecs323.model;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table (name = "comorbidities")
public class Comorbidity
{
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy -- hh:mm:ss");

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long com_id;

    private ZonedDateTime diagnose_date;
    private String state_name;
    private String stage;

    // ASSOCATION(S)
    @ManyToOne
    private Patient patient;

    // CONSTRUCTORS
    public Comorbidity () {}
    public Comorbidity (Patient patient, String name, ZonedDateTime ddate)
    {
        setPatient(patient);
        setStateName(name);
        setDiagnosedDate(ddate);
    }

    // ACCESSORS
    public ZonedDateTime getDiagnosedDate () { return this.diagnose_date; }
    public String getPrettyDate () { return diagnose_date.format(formatter); }
    public String getStateName () { return this.state_name; }
    public String getStage () { return this.stage; }
    public Patient getPatient () { return this.patient; }

    // MUTATORS
    public void setDiagnosedDate (ZonedDateTime ddate) { this.diagnose_date = ddate; }
    public void setStateName (String name) { this.state_name = name; }
    public void setStage (String stage) { this.stage = stage; }
    public void setPatient (Patient patient)
    {
        this.patient = patient;

        patient.addComorbidity(this);
    }
    public void unsetPatient (Patient patient)
    {
        this.patient = null;
        patient.removeComorbidity(this);
    }

    // MISCELLANEOUS
    @Override
    public String toString ()
    {
        return String.format ("STATE: %-20s\tSTAGE: %-15s\tDIAGNOSED: %-25s",
                this.state_name, this.stage, getPrettyDate());
    }
}
