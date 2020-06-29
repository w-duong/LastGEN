package csulb.cecs323.model.Menu;

import csulb.cecs323.model.Drug;
import csulb.cecs323.model.DrugClass;

import java.lang.reflect.Array;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.*;

public class QueryDB
{
    private EntityManager entityManager;
    private static Scanner cin = new Scanner (System.in);

    public QueryDB (EntityManager entityManager) { this.entityManager = entityManager; }

    public <Type> ArrayList<Type> genericSelections_isMultiple (boolean isMultiple, Type template)
    {
        int options = 1;
        ArrayList<Type> results = new ArrayList<>();
        List<Type> possibleMatches = null;

        System.out.print ("\nSEARCH BY NAME. ENTER QUERY (any substring) > ");
        String searchString = cin.nextLine ();

        if (template instanceof Drug)
        {
            TypedQuery query = entityManager.createNamedQuery(Drug.FIND_ALL_BY_NAME, Drug.class).setParameter("searchString", searchString);
            possibleMatches = query.getResultList();
        }
        else if (template instanceof DrugClass)
        {
            TypedQuery query = entityManager.createNamedQuery(DrugClass.FIND_ALL_BY_NAME, DrugClass.class).setParameter("searchString", searchString);
            possibleMatches = query.getResultList();
        }

        System.out.println ("\nSELECT FROM FOLLOWING LIST: ");
        if (template instanceof Drug)
            for (Type choice : possibleMatches)
            {
                Drug temp = (Drug) choice;
                int endIDX = (temp.getChemical_name().length() > 45) ? 45 : temp.getChemical_name().length();

                System.out.println (String.format ("<%4d>\t%-45s", options, temp.getChemical_name().substring(0, endIDX)) );

                options++;
            }
        else if (template instanceof DrugClass)
            for (Type choice : possibleMatches)
            {
                DrugClass temp = (DrugClass) choice;
                System.out.println (String.format ("<%4d>\t%-45s", options, temp.getName()) );

                options++;
            }

        if (isMultiple)
        {
            System.out.print ("\nINPUT SELECTION(S). SELECT MULTIPLE BY SEPARATING WITH SPACE(S) > ");
            String [] userChoices = cin.nextLine ().split (" ");

            for (String option : userChoices)
            {
                if (option.equals("") || option == null)
                    continue;

                int temp = Integer.parseInt (option) - 1;

                if (! results.contains (possibleMatches.get(temp)))
                    results.add (possibleMatches.get(temp));
            }
        }
        else
        {
            System.out.print ("\nINPUT SELECTION > ");
            String userChoice = cin.nextLine ();

            // <-- TO DO: add user validation --> //

            int temp = Integer.parseInt (userChoice) - 1;

            results.add (possibleMatches.get(temp));
        }

        return results;
    }
}