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

    public void addEditClass (DrugClass workingCopy)
    {
        int userChoice = 0;

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
        QueryDB query = new QueryDB(entityManager);
        List<Drug> results = query.genericSelections_isMultiple(true, new Drug());

        for (Drug entry : results)
            if (! workingCopy.getDrugs().contains(entry))
                workingCopy.addDrug(entry);
    }

    public void addSuperSubToClass (DrugClass workingCopy, boolean isSuper)
    {
        QueryDB query = new QueryDB(entityManager);
        List<DrugClass> results = query.genericSelections_isMultiple(true, new DrugClass());

        for (DrugClass entry : results)
        {
            //<-- TO DO: add check to ensure 'workingCopy' does not have a superclass AND subclass that are the same-->//

            if ((isSuper) && (! workingCopy.getSuperclasses().contains (entry)))
                workingCopy.addSuperclass(entry);
            else if (! workingCopy.getSubclasses().contains(entry))
                workingCopy.addSubclass(entry);
        }
    }

}
