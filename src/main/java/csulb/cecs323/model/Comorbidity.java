package csulb.cecs323.model;

import javax.persistence.*;

@Entity
@Table (name = "comorbidities")
public class Comorbidity
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long com_id;

    private String state_name;
    private int stage;

    // ASSOCATION(S)
    @ManyToOne
    private Patient patient;

    // CONSTRUCTORS
    public Comorbidity () {}
    public Comorbidity (String name) { setStateName(name); }

    // ACCESSORS
    public String getStateName () { return this.state_name; }
    public int getStage () { return this.stage; }
    public Patient getPatient () { return this.patient; }

    // MUTATORS
    public void setStateName (String name) { this.state_name = name; }
    public void setStage (int stage) { this.stage = stage; }
    public void setPatient (Patient patient)
    {
        this.patient = patient;

        patient.addComorbidity(this);
    }

    // MISCELLANEOUS
}
