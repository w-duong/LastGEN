package csulb.cecs323.model.Menu;

import csulb.cecs323.model.BrandName;
import csulb.cecs323.model.Drug;
import csulb.cecs323.model.DrugClass;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DrugDBsubmodule
{
    private static final Scanner cin = new Scanner (System.in);
    private EntityManager entityManager;

    public DrugDBsubmodule (EntityManager entityManager) { this.entityManager = entityManager; }

    public static ArrayList<String> paragraphSplitter (String longstring, int min, int max)
    {
        ArrayList<String> paragraph = null;
        String [] temp = longstring.split (" ");
        String keep = "";

        for (int i = 0; i < temp.length; ++i)
            if (temp [i].length() + keep.length() < max)
            {
                keep += temp [i] + " ";
                if (keep.length() > min)
                {
                    paragraph.add (keep + "\n");
                    keep = "";
                }
            }
            else
            {
                paragraph.add (keep + "\n");
                keep = "";
                --i;
            }

        if (keep != "")
            paragraph.add (keep);

        return paragraph;
    }

    public void addEditDrug (Drug workingCopy)
    {
        int userChoice = 0;

        do
        {
            displayObject(workingCopy);

            userChoice = Integer.parseInt(cin.nextLine());

            switch (userChoice)
            {
                case 1:
                    System.out.print ("\nEnter Drug Name > ");
                    workingCopy.setChemicalName (cin.nextLine());
                    break;
                case 2:
                    System.out.print ("\nEnter Drug Description > ");
                    //<-- TO DO: implement mini text editor for this option -->//
                    break;
                case 3:
                    //<-- TO DO: implement mini text editor for this option -->//
                    break;
                case 4:
                    addSuperSubToClass(workingCopy, false);
                    break;
                case 5:
                    addDrugToClass(workingCopy);
                    break;
            }
        }
        while (userChoice != 7);

        // <--TO DO: persist 'workingCopy' here after user confirmation --> //
    }

    public void displayObject (Drug workingCopy)
    {
        //<-- TO DO: add 'drugCount' functionality -->//
        int drugCount = 0;
        int maxSize = (workingCopy.getChemical_name().length() > 45) ? 45 : workingCopy.getChemical_name().length ();
        ArrayList<String> paragraph_describe = paragraphSplitter(workingCopy.getDescription(), 42, 50);
        ArrayList<String> paragraph_clinical = paragraphSplitter(workingCopy.getPharmacology(), 42, 50);

        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.print
                (       "********************************************************************************************************\n" +
                        "**                                                                                                    **\n" +
                        "**                                             -- LastGEN --                                          **\n" +
                        "**                                  **   DRUG DATABASE MAINTENANCE   **                               **\n" +
                        "**                                                                                                    **\n" +
        String.format  ("**                                   CURRENT DRUG COUNT : %-5d                                       **\n", drugCount) +
                        "**                                                                                                    **\n" +
                        "**                                                                    -- PENDING VALUES --            **\n" +
                        "**                                                                                                    **\n" +
        String.format  ("**  < 1 > Enter Drug Name                           > %-50s**\n", workingCopy.getChemical_name().substring(0, maxSize)) +
                        "**  < 2 > Enter Drug Description                    >                                                 **\n");
                for (String line : paragraph_describe)
                    System.out.print ( String.format ("**%-49s>%-50s**", "", line) );
        System.out.print
                (       "**  < 3 > Enter Clinical Pharmacology               >                                                 **\n");
                for (String line : paragraph_clinical)
                    System.out.print ( String.format ("**%-49s>%-50s**", "", line) );
        System.out.print (
        String.format  ("**  < 4 > Add Drug Dosage(s)                        > %-50s**\n", workingCopy.getDosage()) +
        String.format  ("**  < 5 > Add Parent Class                          > %-50s**\n", "-- CURRENT LIST --"));
            for (DrugClass drugClass : workingCopy.getDrugClass())
            {
                int endIDX = (drugClass.getName().length() > 40) ? 40 : drugClass.getName().length();

                System.out.println (String.format ("**%-52s%-48s**", "", drugClass.getName().substring (0, endIDX)) );
            }
        System.out.print
                (
        String.format  ("**  < 6 > Add a Brand Name                          > %-50s**\n", "-- CURRENT LIST --"));
            for (BrandName brand : workingCopy.getBrandNames())
            {
                int endIDX = (brand.toString().length() > 40) ? 40 : brand.toString().length();

                System.out.println (String.format ("**%-52s%-48s**", "", brand.toString().substring (0, endIDX)) );
            }
        System.out.print
                (
                        "**  < 7 > Return to previous menu                                                                     **\n" +
                        "**                                                                                                    **\n" +
                        "********************************************************************************************************\n" +
                        "                                          CHOICE (0 - 7): "
                );
    }