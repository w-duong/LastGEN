package csulb.cecs323.model;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Entity
@Table (name = "prescriptions")
public class Prescription
{
//    private static final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long rx_number;

//    private GregorianCalendar date_written;
    private ZonedDateTime date_written;

    // ASSOCIATION(S)
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    private List<RxLine> rxLines;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Prescriber prescriber;

    // CONSTRUCTORS
    public Prescription () {}
    public Prescription (Patient patient, Prescriber prescriber, ZonedDateTime date)
    {
        setPatient(patient);
        setPrescriber(prescriber);
        setDateWritten(date);

        rxLines = new ArrayList<>();
    }

    // ACCESSORS
    public long getRxNumber () { return this.rx_number; }
    public ZonedDateTime getDateWritten () { return this.date_written; }
    public String getPrettyDateWritten () { return this.date_written.format(formatter); }
    public Patient getPatient () { return this.patient; }
    public Prescriber getPrescriber() { return this.prescriber; }

    public List<RxLine> getRxLines () { return this.rxLines; }

    // MUTATORS
    public void setDateWritten (ZonedDateTime date) { this.date_written = date; }
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
