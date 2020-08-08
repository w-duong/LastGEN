package csulb.cecs323.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Entity
@Table (name = "prescriptions")
public class Prescription
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long rx_number;

    private GregorianCalendar date_written;

    // ASSOCIATION(S)
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    private List<RxLine> rxLines;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Prescriber prescriber;

    // CONSTRUCTORS
    public Prescription () {}
    public Prescription (Patient patient, Prescriber prescriber, GregorianCalendar date)
    {
        setPatient(patient);
        setPrescriber(prescriber);
        setDateWritten(date);

        rxLines = new ArrayList<>();
    }

    // ACCESSORS
    public GregorianCalendar getDateWritten () { return this.date_written; }
    public Patient getPatient () { return this.patient; }
    public Prescriber getPrescriber() { return this.prescriber; }

    // MUTATORS
    public void setDateWritten (GregorianCalendar date) { this.date_written = date; }
    public void setPatient (Patient patient)
    {
        this.patient = patient;
        patient.addPrescription(this);
    }
    public void setPrescriber(Prescriber prescriber)
    {
        this.prescriber = prescriber;
        prescriber.addPrescription(this);
    }

    // MISCELLANEOUS
    public void addRxLine (RxLine rxLine)
    {
        if (this.rxLines == null)
            this.rxLines = new ArrayList<>();

        if (!this.rxLines.contains(rxLine))
        {
            this.rxLines.add(rxLine);

            rxLine.setPrescription(this);
        }
    }

}
