package csulb.cecs323.model;

import javax.persistence.*;
import java.util.GregorianCalendar;

@Entity
@Table (name = "rx_lines")
public class RxLine
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long rx_id;

    private String directions;
    private GregorianCalendar date_written;
    private double quantity;
    private int refills;
    private double strength;

    // ASSOCIATION(S)
    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Drug drug;

    @ManyToOne
    private Doctor doctor;

    // CONSTRUCTORS
    public RxLine () {}
    public RxLine (Patient patient, Doctor doctor, Drug drug, GregorianCalendar date, double strength, double quantity)
    {


        setDateWritten(date);
        setStrength(strength);
        setQuantity(quantity);
    }

    // ACCESSORS

    // MUTATORS
    public void setDirections (String directions) { this.directions = directions; }
    public void setDateWritten (GregorianCalendar date) { this.date_written = date; }
    public void setQuantity (double quantity) { this.quantity = quantity; }
    public void setRefills (int refills) { this.refills = refills; }
    public void setStrength (double strength) { this.strength = strength; }

    public void setPatient (Patient patient)
    {
        this.patient = patient;
        patient.addRxLine(this);
    }
    public void setDoctor (Doctor doctor)
    {
        this.doctor = doctor;
        doctor.addRxLine(this);
    }
    public void setDrug (Drug drug)
    {
        this.drug = drug;
        drug.addRxLine(this);
    }

    // MISCELLANEOUS
}
