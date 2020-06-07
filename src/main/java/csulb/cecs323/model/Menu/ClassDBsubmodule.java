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
        int userChoice = 0;
        DrugClass workingCopy = new DrugClass ("AAAA", "DEFAULT");

        //<-- TO DO: functionality to add/edit drugs using a copy constructor -->//

        do
        {
            displayObject(workingCopy);

            userChoice = Integer.parseInt(cin.nextLine());

            switch (userChoice)
            {
                case 1:
                    System.out.print ("\nEnter Drug Class Name > ");
                    workingCopy.setName (cin.nextLine());
                    break;
                case 2:
                    System.out.print ("\nEnter Drug Class Abbreviation > ");
                    workingCopy.setAbbreviation(cin.nextLine());
                    break;
                case 3:
                    addSuperSubToClass(workingCopy, true);
                    break;
                case 4:
                    addSuperSubToClass(workingCopy, false);
                    break;
                case 5:
                    addDrugToClass(workingCopy);
                    break;
            }
        }
        while (userChoice != 6);

        // <--TO DO: persist 'workingCopy' here after user confirmation --> //
    }

    public void displayObject (DrugClass workingCopy)
    {
        int classCount = 0;
        int maxSize = (workingCopy.getName().length() > 45) ? 45 : workingCopy.getName().length ();

        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.print
                (       "********************************************************************************************************\n" +
                        "**                                                                                                    **\n" +
                        "**                                             -- LastGEN --                                          **\n" +
                        "**                                  **   DRUG DATABASE MAINTENANCE   **                               **\n" +
                        "**                                                                                                    **\n" +
        String.format  ("**                                   CURRENT DRUG CLASS COUNT : %-5d                                 **\n", classCount) +
                        "**                                                                                                    **\n" +
                        "**                                                                    -- PENDING VALUES --            **\n" +
                        "**                                                                                                    **\n" +
        String.format  ("**  < 1 > Enter Drug Class Name                   > %-50s**\n", workingCopy.getName().substring(0, maxSize)) +
        String.format  ("**  < 2 > Enter Drug Class Abbreviation           > %-5s                                             **\n", workingCopy.getAbbreviation()) +
        String.format  ("**  < 3 > Add Parent Class                        > %-50s**\n", "-- CURRENT LIST --"));
            for (DrugClass drugClass : workingCopy.getSuperclasses())
            {
                int endIDX = (drugClass.getName().length() > 40) ? 40 : drugClass.getName().length();

                System.out.println (String.format ("**%-52s%-48s**", "", drugClass.getName().substring (0, endIDX)) );
            }
        System.out.print
                (
        String.format  ("**  < 4 > Add Child Class                         > %-50s**\n", "-- CURRENT LIST --"));
            for (DrugClass drugClass : workingCopy.getSubclasses())
            {
                int endIDX = (drugClass.getName().length() > 40) ? 40 : drugClass.getName().length();

                System.out.println (String.format ("**%-52s%-48s**", "", drugClass.getName().substring (0, endIDX)) );
            }
        System.out.print
                (
        String.format  ("**  < 5 > Add a DRUG to this Class                > %-50s**\n", "-- CURRENT LIST --"));
            for (Drug drug : workingCopy.getDrugs())
            {
                int endIDX = (drug.getChemical_name().length() > 40) ? 40 : drug.getChemical_name().length();

                System.out.println (String.format ("**%-52s%-48s**", "", drug.getChemical_name().substring (0, endIDX)) );
            }
        System.out.print
                (
                        "**  < 6 > Return to previous menu                                                                     **\n" +
                        "**                                                                                                    **\n" +
                        "********************************************************************************************************\n" +
                        "                                          CHOICE (0 - 6): "
                );
    }

    public void addDrugToClass (DrugClass workingCopy)
    {
        int options = 1;

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

            int temp = Integer.parseInt(selection) - 1;

            if (! workingCopy.getDrugs().contains (results.get(temp)))
            {
                workingCopy.addDrug (results.get(temp));
            }
        }
    }

    public void addSuperSubToClass (DrugClass workingCopy, boolean isSuper)
    {
        int options = 1;

        System.out.print ("\nSEARCH FOR CLASS BY NAME. ENTER QUERY (any substring) > ");
        String searchString = cin.nextLine ();

        TypedQuery query = entityManager.createNamedQuery(DrugClass.FIND_ALL_BY_NAME, DrugClass.class).setParameter("searchString", searchString);
        List<DrugClass> results = query.getResultList();

        System.out.println ("\nSELECT OPTIONS FROM FOLLOWING LIST: ");
        for (DrugClass drugClass : results)
        {
            System.out.println (String.format ("<%4d>\t%-45s", options, drugClass.getName()) );

            options++;
        }

        System.out.print ("\nINPUT SELECTION(S). SELECT MULTIPLE BY SEPARATING WITH SPACE(S) > ");
        String [] userChoices = cin.nextLine ().split (" ");

        for (String option : userChoices)
        {
            if (option.equals ("") || option == null)
                continue;

            int temp = Integer.parseInt(option) - 1;

            //<-- TO DO: add check to ensure 'workingCopy' does not have a superclass AND subclass that are the same-->//

            if ((isSuper) && (! workingCopy.getSuperclasses().contains (results.get(temp))))
                workingCopy.addSuperclass(results.get(temp));
            else if (! workingCopy.getSubclasses().contains(results.get(temp)))
                workingCopy.addSubclass(results.get(temp));
        }
    }

}
