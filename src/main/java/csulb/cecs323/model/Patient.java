package csulb.cecs323.model;

import csulb.cecs323.model.Person;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Table (name = "patients")
@DiscriminatorValue("1")
public class Patient extends Person
{
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
    private GregorianCalendar dateOfBirth;

    // ASSOCIATION(S)
    /* Unidirectional: the Drug does not need to keep track of which patient is allergic to it */
    @OneToMany(cascade = CascadeType.PERSIST)
//    @JoinColumn(name = "did", referencedColumnName = "person_id") //<---doesn't work, would be more efficient if it DID
    private List<Drug> drugAllergyList;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.PERSIST)
    private List<Comorbidity> comorbidities;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.PERSIST)
    private List<Prescription> prescriptions;

    // CONSTRUCTORS
    public Patient () {}
    public Patient (String first, String last, GregorianCalendar birth)
    {
        super(first, last);
        setDateOfBirth(birth);
    }

    // ACCESSORS
    public GregorianCalendar getDateOfBirth () { return this.dateOfBirth; }
    public String getPrettyDOB ()
    {
        formatter.setCalendar(dateOfBirth);
        return formatter.format(dateOfBirth.getTime());
    }
    public List<Drug> getDrugAllergyList () { return this.drugAllergyList; }
    public List<Comorbidity> getComorbidities () { return this.comorbidities; }

    // MUTATORS
    public void setDateOfBirth (GregorianCalendar dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    // MISCELLANEOUS
    public void addDrugAllergy (Drug allergy)
    {
        if (this.drugAllergyList == null)
            this.drugAllergyList = new ArrayList<>();

        if (!this.drugAllergyList.contains(allergy))
            this.drugAllergyList.add(allergy);
    }

    public void addComorbidity (Comorbidity comorbidity)
    {
        if (this.comorbidities == null)
            this.comorbidities = new ArrayList<>();

        if (!this.comorbidities.contains(comorbidity))
        {
            this.comorbidities.add(comorbidity);

            comorbidity.setPatient(this);
        }
    }

    public void addPrescription (Prescription prescription)
    {
        if (this.prescriptions == null)
            this.prescriptions = new ArrayList<>();

        if (!this.prescriptions.contains(prescription))
        {
            this.prescriptions.add(prescription);
            prescription.setPatient(this);
        }
    }

    @Override
    public String toString ()
    {
        formatter.setCalendar(dateOfBirth);
        String dateFormatted = formatter.format(dateOfBirth.getTime());

        return super.toString() + String.format ("\tDOB: %-15s\nAllergies: %s", dateFormatted, drugAllergyList);
    }
}
