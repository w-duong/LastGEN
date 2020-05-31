package csulb.cecs323.model.Menu;

import csulb.cecs323.model.Drug;
import csulb.cecs323.model.DrugClass;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClassDBsubmodule
{
    private static final Scanner cin = new Scanner (System.in);
    private EntityManager entityManager;

    public ClassDBsubmodule (EntityManager entityManager) { this.entityManager = entityManager; }

    public void addClass ()
    {
        int userChoice = 0, classCount = 0, maxSize = 1;
        String tempAbbr = "AAAA", tempName = "DEFAULT";

        ArrayList<DrugClass> tempSupers = new ArrayList<>();
        ArrayList<DrugClass> tempSubs = new ArrayList<>();
        ArrayList<Drug> tempDrugs = new ArrayList<>();

        DrugClass workingCopy = new DrugClass (tempAbbr, tempName);

        do
        {
            maxSize = (tempName.length() > 45) ? 45 : tempName.length ();

            System.out.println("\n\n\n\n\n\n\n\n\n\n");
            System.out.print
                    (   "********************************************************************************************************\n" +
                        "**                                                                                                    **\n" +
                        "**                                             -- LastGEN --                                          **\n" +
                        "**                                  **   DRUG DATABASE MAINTENANCE   **                               **\n" +
                        "**                                                                                                    **\n" +
        String.format  ("**                                   CURRENT DRUG CLASS COUNT : %-5d                             **\n", classCount) +
                        "**                                                                                                    **\n" +
                        "**                                                                         -- PENDING VALUES --       **\n" +
                        "**                                                                                                    **\n" +
        String.format  ("**  < 1 > Enter Drug Class Name                   > %-50s**\n", tempName.substring(0, maxSize)) +
        String.format  ("**  < 2 > Enter Drug Class Abbreviation           > %-5s                                         **\n", tempAbbr) +
        String.format  ("**  < 3 > Add Parent Class                        > %-50s**\n", "-- CURRENT LIST --"));
                            for (DrugClass drugClass : tempSupers)
                            {
                                int endIDX = (drugClass.getName().length() > 45) ? 45 : drugClass.getName().length();

                                System.out.println (String.format ("**%-50s%-50s**", "", drugClass.getName().substring (0, endIDX)) );
                            }
            System.out.print
                    (
        String.format  ("**  < 4 > Add Child Class                         > %-50s**\n", "-- CURRENT LIST --"));
                            for (DrugClass drugClass : tempSubs)
                            {
                                int endIDX = (drugClass.getName().length() > 45) ? 45 : drugClass.getName().length();

                                System.out.println (String.format ("**%-50s%-50s**", "", drugClass.getName().substring (0, endIDX)) );
                            }
            System.out.print
                    (
        String.format  ("**  < 5 > Add a DRUG to this Class                > %-50s**\n", "-- CURRENT LIST --"));
                            for (Drug drug : tempDrugs)
                            {
                                int endIDX = (drug.getChemical_name().length() > 45) ? 45 : drug.getChemical_name().length();

                                System.out.println (String.format ("**%-50s%-50s**", "", drug.getChemical_name().substring (0, endIDX)) );
                            }
            System.out.print
                    (
                        "**  < 6 > Return to previous menu                                                                     **\n" +
                        "**                                                                                                    **\n" +
                        "********************************************************************************************************\n" +
                        "                                          CHOICE (0 - 6): "
                    );

            userChoice = Integer.parseInt(cin.nextLine());

            switch (userChoice)
            {
                case 1:
                    System.out.print ("\nEnter Drug Class Name > ");
                    tempName = cin.nextLine ();
                    break;
                case 2:
                    System.out.print ("\nEnter Drug Class Abbreviation > ");
                    tempAbbr = cin.nextLine ();
                    break;
                case 5:
                    tempDrugs = addDrugToClass(workingCopy);
                    break;
            }
        }
        while (userChoice != 6);
    }

    public ArrayList<Drug> addDrugToClass (DrugClass workingCopy)
    {
        int options = 1;
        ArrayList<Drug> drugsToAttach = new ArrayList<>();

        System.out.print ("\nSEARCH FOR DRUG BY NAME. ENTER QUERY (any substring) > ");
        String searchString = cin.nextLine ();

        TypedQuery query = entityManager.createNamedQuery(Drug.FIND_ALL_BY_NAME, Drug.class).setParameter("searchString", searchString);
        List<Drug> results = query.getResultList();

        System.out.println ("\nSELECT OPTIONS FROM FOLLOWING LIST: ");
        for (Drug drug : results)
        {
            int endIDX = (drug.getChemical_name().length() > 45) ? 45 : drug.getChemical_name().length();

            System.out.println (String.format ("<%4d>\t%-45s", options, drug.getChemical_name().substring(0, endIDX)) );

            options++;
        }

        System.out.print ("\nINPUT SELECTION(S). SELECT MULTIPLE BY SEPARATING WITH SPACE(S) > ");
        String [] userChoices = cin.nextLine ().split (" ");

        for (String selection : userChoices)
        {
            if (selection.equals ("") || selection == null)
                continue;

            int temp = Integer.parseInt(selection);

            workingCopy.addDrug (results.get(temp));
            drugsToAttach.add (results.get(temp));
        }

        return drugsToAttach;
    }

}
