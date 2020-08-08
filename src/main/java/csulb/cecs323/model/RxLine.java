package csulb.cecs323.model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity
@Table (name = "rx_lines")
public class RxLine
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long rx_id;
    private double quantityRemaining;

    private boolean isRefill = false;
    private String directions;
    private GregorianCalendar date_filled;
    private double quantityWritten;
    private double quantityFilled;
    private int refills;
    private double strength;

    // ASSOCIATION(S)
    @ManyToOne
    private Prescription prescription;

    @ManyToOne
    private Drug drug;

    @ManyToOne
    private Pharmacist pharmacist;

    // CONSTRUCTORS
    public RxLine () {}
    public RxLine (Drug drug, Pharmacist rph, double strength, double quantityWritten, int refills)
    {
        setDrug(drug);
        setPharmacist(rph);

        setStrength(strength);
        setQuantityWritten(quantityWritten);
        setRefills(refills);

        calculateTotal();
    }
    public RxLine (RxLine other)
    {
        setIsRefill (other.isRefill());
        setDirections (other.getDirections());
        setDateFilled (other.getDateFilled());
        setQuantityWritten (other.getQuantityWritten());
        setQuantityFilled (other.getQuantityFilled());
        setRefills (other.getRefills());
        setStrength(other.getStrength());
        calculateTotal();

        setPrescription (other.getPrescription());
        setDrug (other.getDrug());
        setPharmacist (other.getPharmacist());
    }

    // ACCESSORS
    public boolean isRefill () { return this.isRefill; }
    public String getDirections () { return this.directions; }
    public GregorianCalendar getDateFilled () { return this.date_filled; }
    public double getQuantityWritten() {return this.quantityWritten; }
    public double getQuantityFilled() { return this.quantityFilled; }
    public double getQuantityRemaining() { return this.quantityRemaining; }
    public int getRefills () { return this.refills; }
    public double getStrength() { return this.strength; }

    public Prescription getPrescription () { return this.prescription; }
    public Drug getDrug () { return this.drug; }
    public Pharmacist getPharmacist () { return this.pharmacist; }

    // MUTATORS
    public void setIsRefill (boolean status) { this.isRefill = status; }
    public void setDirections (String directions) { this.directions = directions; }
    public void setDateFilled (GregorianCalendar date) { this.date_filled = date; }
    protected void setQuantityRemaining (double quantity) { this.quantityRemaining = quantity; }
    public void setQuantityWritten (double quantity) { this.quantityWritten = quantity; }
    public void setQuantityFilled(double quantity) { this.quantityFilled = quantity; }
    public void setRefills (int refills) { this.refills = refills; }
    public void setStrength (double strength) { this.strength = strength; }

    public void setPrescription (Prescription prescription)
    {
        this.prescription = prescription;
        prescription.addRxLine(this);
    }
    public void setPharmacist (Pharmacist pharmacist)
    {
        this.pharmacist = pharmacist;
        pharmacist.addRxLine(this);
    }
    public void setDrug (Drug drug)
    {
        this.drug = drug;
        drug.addRxLine(this);
    }

    // MISCELLANEOUS
    protected void calculateTotal () { quantityRemaining = quantityWritten + (refills * quantityWritten); }

    public RxLine createFill (double fillAmount)
    {
        RxLine fill = null;

        if (this.quantityRemaining > 0 && fillAmount <= this.quantityWritten && fillAmount > 0)
        {
            Calendar lookup = Calendar.getInstance();
            GregorianCalendar today = (GregorianCalendar) lookup;

            fill = new RxLine (this.drug, this.pharmacist, this.strength, this.quantityWritten, this.refills);
            if (!this.isRefill)
                fill.setIsRefill(true);
            fill.setQuantityFilled(fillAmount);
            fill.setDirections(this.directions);
            fill.setDateFilled(today);
            fill.setPrescription(this.prescription);

            if (this.quantityRemaining < fillAmount)
            {
                fill.setQuantityFilled(this.quantityRemaining);
                fill.setQuantityRemaining(0);
            }
            else
                fill.setQuantityRemaining (this.quantityRemaining - fillAmount);

            if (fill.quantityRemaining >= fill.quantityWritten)
                fill.refills = (int) (fill.quantityRemaining / fill.quantityWritten);
            else
                fill.refills = 0;
        }

        return fill;
    }
}

/*
//////// TESTING, SAVE UNTIL FURTHER NOTICE ////////////////

public class RxLine
{
    public double quantityRemain;

    public double quantityWrit;
    public double quantityFill;
    public int refills;

    public RxLine (double written, double fill, int refill)
    {
        quantityWrit = written;
        quantityFill = fill;
        refills = refill;

        // setRemaining();
        setTotal();
    }

    public void setRemaining ()
    {
        quantityRemain = (this.quantityWrit > this.quantityFill && refills == 0) ?
                        quantityWrit - quantityFill : (this.quantityWrit > this.quantityFill && refills > 0) ?
                        (this.quantityWrit + (refills * this.quantityWrit)) - this.quantityFill : (this.quantityWrit == this.quantityFill && refills > 0) ?
                        (this.quantityWrit + (refills * this.quantityWrit)) : 0;
    }

    public void setTotal ()
    {
        quantityRemain = this.quantityWrit + (refills * this.quantityWrit);
    }

    public RxLine createFill (double fillAmount)
    {
        RxLine refill = null;

        if (quantityRemain > 0 && fillAmount <= this.quantityWrit && fillAmount > 0)
        {
            refill = new RxLine (this.quantityWrit, fillAmount, this.refills);

            if (this.quantityRemain < fillAmount)
            {
                refill.quantityFill = quantityRemain;
                refill.quantityRemain = 0;
            }
            else
                refill.quantityRemain = this.quantityRemain - fillAmount;

            if (refill.quantityRemain >= quantityWrit)
                refill.refills = (int) (refill.quantityRemain / quantityWrit);
            else
                refill.refills = 0;
        }

        return refill;
    }

    public String toString ()
    {
        return String.format ("WRIT = %f\tFILL = %f\tREFILLS = %d\tQTY Remaining: %f",
                            quantityWrit, quantityFill, refills, quantityRemain);
    }
}
 */
