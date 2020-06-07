package csulb.cecs323.model.Menu;

import javax.persistence.EntityManager;
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
                        ArrayList<DrugClass> results = query.forDrugClass_getSelections (false);
                        
                        DrugClass workingCopy = new DrugClass (results.get(1));
                        
                        submodule.addEditClass(workingCopy);
                    }
                    break;
                case 2:
                    ArrayList<DrugClass> results = query.forDrugClass_getSelections (false);
                    
                    submodule.addEditClass(results.get(1));
                    break;

            }
        }
        while (userChoice != 7);

        return (userChoice == 7) ? 0 : 1;
    }
    
}
