package csulb.cecs323.model.Menu;

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

    public void displayObject (Drug workingCopy)
    {
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
        String.format  ("**  < 4 > Enter Drug Dosage(s)                      > %-50s**\n", workingCopy.getDosage()));

    }

}