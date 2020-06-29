package csulb.cecs323.model.Menu;

import csulb.cecs323.model.DrugClass;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DrugDBMenu
{
    private static final Scanner cin = new Scanner (System.in);
    private EntityManager entityManager;

    public DrugDBMenu (EntityManager entityManager) { this.entityManager = entityManager; }

    public int display ()
    {
        int userChoice;
        ClassDBsubmodule submodule = new ClassDBsubmodule(entityManager);
        QueryDB query = new QueryDB(entityManager);
        ArrayList<DrugClass> classResults;

        do
        {
            System.out.println("\n\n\n\n\n\n\n\n\n\n");
            System.out.print
                    (   "********************************************************************************************************\n" +
                        "**                                                                                                    **\n" +
                        "**                                             -- LastGEN --                                          **\n" +
                        "**                                  **   DRUG DATABASE MAINTENANCE   **                               **\n" +
                        "**                                                                                                    **\n" +
                        "**                                  < 1 > Add drug CLASS                                              **\n" +
                        "**                                  < 2 > Edit drug CLASS                                             **\n" +
                        "**                                  < 3 > Delete drug CLASS                                           **\n" +
                        "**                                  < 4 > Add new DRUG                                                **\n" +
                        "**                                  < 5 > Edit a DRUG                                                 **\n" +
                        "**                                  < 6 > Delete a DRUG                                               **\n" +
                        "**                                  < 7 >         QUIT                                                **\n" +
                        "**                                                                                                    **\n" +
                        "**                                                                                                    **\n" +
                        "**                                                                                                    **\n" +
                        "********************************************************************************************************\n" +
                        "                                         ENTER CHOICE (1-7) > "
                    );

            userChoice = Integer.parseInt(cin.nextLine());
            String pause;

            switch (userChoice)
            {
                case 1:
                    System.out.print ("\nStart with NEW DRUG template or use pre-existing DRUG? Enter choice (0 = NEW, 1 = Existing) >");
                    
                    //<-- TO DO: user input validation -->//
                    
                    if (Integer.parseInt (cin.nextLine()) == 0)
                    {
                        DrugClass workingCopy = new DrugClass ("AAAA", "DEFAULT");
                        
                        submodule.addEditClass(workingCopy);
                    }
                    else
                    {
                        classResults = query.genericSelections_isMultiple(false, new DrugClass ());

                        //<-- TO DO: handle if no results -->//
                        
                        DrugClass workingCopy = new DrugClass (classResults.get(0));
                        
                        submodule.addEditClass(workingCopy);
                    }
                    break;
                case 2:
                    classResults = query.genericSelections_isMultiple(false, new DrugClass());
                    
                    submodule.addEditClass(classResults.get(0));
                    break;
                case 3:
                    classResults = query.genericSelections_isMultiple(false, new DrugClass());

                    entityManager.getTransaction().begin();

                    Query deleteQuery = entityManager.createQuery("DELETE FROM DrugClass dc WHERE dc = :toDelete");
                    int deletedCount = deleteQuery.setParameter("toDelete", classResults.get(0)).executeUpdate();

                    entityManager.getTransaction().commit();
            }
        }
        while (userChoice != 7);

        return (userChoice == 7) ? 0 : 1;
    }
    
}
