package csulb.cecs323.model.Menu;

import csulb.cecs323.model.DrugClass;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.*;

public class QueryDB
{
    private EntityManager entityManager;
    private static Scanner cin = new Scanner (System.in);
    
    public QueryDB (EntityManager entityManager) { this.entityManager = entityManager; }

    public ArrayList<DrugClass> forDrugClass_getSelections (boolean isMultiple)
    {
        int options = 1;
        ArrayList<DrugClass> results = new ArrayList<>();

        System.out.print ("\nSEARCH FOR CLASS BY NAME. ENTER QUERY (any substring) > ");
        String searchString = cin.nextLine ();

        TypedQuery query = entityManager.createNamedQuery(DrugClass.FIND_ALL_BY_NAME, DrugClass.class).setParameter("searchString", searchString);
        List<DrugClass> possibleMatches = query.getResultList();

        System.out.println ("\nSELECT FROM FOLLOWING LIST: ");
        for (DrugClass drugClass : possibleMatches)
        {
            System.out.println (String.format ("<%4d>\t%-45s", options, drugClass.getName()) );

            options++;
        }

        if (isMultiple)
        {
            System.out.print ("\nINPUT SELECTION(S). SELECT MULTIPLE BY SEPARATING WITH SPACE(S) > ");
            String [] userChoices = cin.nextLine ().split (" ");
            
            for (String option : userChoices)
            {
                if (option.equals ("") || option == null)
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