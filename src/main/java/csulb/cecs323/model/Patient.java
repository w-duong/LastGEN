package csulb.cecs323.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Table (name = "patients")
@DiscriminatorValue("1")
@NamedQueries({
        @NamedQuery (name = Patient.FIND_ALL, query = "SELECT pt FROM Patient pt"),
        @NamedQuery (name = Patient.FIND_ALL_BY_DOB, query = "SELECT pt FROM Patient pt WHERE pt.dateOfBirth = :ptDOB"),
        @NamedQuery (name = Patient.FIND_ALL_BY_NAME,
        query = "SELECT pt FROM Patient pt WHERE LOWER (pt.lastName) LIKE LOWER (CONCAT ('%', :ptLastName, '%')) AND " +
                "LOWER (pt.firstName) LIKE LOWER (CONCAT ('%', :ptFirstName, '%'))"),
        @NamedQuery (name = Patient.FIND_ALL_BY_SPEC,
        query = "SELECT pt FROM Patient pt WHERE LOWER (pt.lastName) LIKE LOWER (CONCAT ('%', :ptLastName, '%')) AND " +
                "LOWER (pt.firstName) LIKE LOWER (CONCAT ('%', :ptFirstName, '%')) AND " +
                "pt.dateOfBirth = :ptDOB")
})
public class Patient extends Person
{
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

//    @Convert(converter = ZonedDateTimeConverter.class)
//    private ZonedDateTime dateOfBirth;
    private LocalDate dateOfBirth;

    // QUERY STRING(S)
    public static final String FIND_ALL = "Patient.findAllPatients";
    public static final String FIND_ALL_BY_NAME = "Patient.findByName";
    public static final String FIND_ALL_BY_DOB = "Patient.findByDOB";
    public static final String FIND_ALL_BY_SPEC = "Patient.findByNameDOB";

    // ASSOCIATION(S)
    /* Unidirectional: the Drug does not need to keep track of which patient is allergic to it */
    @OneToMany(cascade = CascadeType.ALL)
//    @JoinColumn(name = "did", referencedColumnName = "person_id") //<---doesn't work, would be more efficient if it DID
    private List<Drug> drugAllergyList;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.PERSIST)
    private List<Comorbidity> comorbidities;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.PERSIST)
    private List<Prescription> prescriptions;

    // CONSTRUCTORS
    public Patient () {}
    public Patient (String first, String last, LocalDate birth)
    {
        super(first, last);
        setDateOfBirth(birth);

        drugAllergyList = new ArrayList<>();
    }

    // ACCESSORS
    public LocalDate getDateOfBirth () { return this.dateOfBirth; }
    public String getPrettyDOB () { return dateOfBirth.format(formatter); }
    public List<Drug> getDrugAllergyList () { return this.drugAllergyList; }
    public List<Comorbidity> getComorbidities () { return this.comorbidities; }

    // MUTATORS
    public void setDateOfBirth (LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

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
    public void removeComorbidity (Comorbidity comorbidity)
    {
        if (this.comorbidities != null && this.comorbidities.contains(comorbidity))
        {
            this.comorbidities.remove(comorbidity);
            comorbidity.unsetPatient(this);
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
        String defaultPhone = "", defaultAddress = "";
        for (Phone number : this.getPhoneList())
            if (number.isDefaultNumber())
                defaultPhone = number.getPrettyNumber();
        for (Address street : this.getAddresses())
            if (street.isDefaultAddress())
                defaultAddress = street.toString();

        return (String.format ("FULL NAME: %-30s\tADDRESS: %-40s\tPHONE: %-15s",
                this.getFirstName() + " " + this.getLastName(), defaultAddress, defaultPhone));
    }

    public String fullString()
    {
        String dateFormatted = dateOfBirth.format(formatter);
        return super.toString() + String.format ("\tDOB: %-15s\nAllergies: %s", dateFormatted, drugAllergyList);
    }
}
