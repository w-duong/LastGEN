package csulb.cecs323.model.Menu;

import javax.persistence.EntityManager;
import java.util.Scanner;

public class MainMenu
{
    static private final Scanner cin = new Scanner (System.in);
    private EntityManager entityManager;

    public MainMenu (EntityManager entityManager) { this.entityManager = entityManager; }

    public int display ()
    {
        int userChoice, status;

        do
        {
            System.out.println("\n\n\n\n\n\n\n\n\n\n");
            System.out.print
                    (   "********************************************************************************************************\n" +
                        "**                                                                                                    **\n" +
                        "**                                             -- LastGEN --                                          **\n" +
                        "**                                          **   MAIN MENU   **                                       **\n" +
                        "**                                                                                                    **\n" +
                        "**                                   < 1 > Drug Database Maintenance                                  **\n" +
                        "**                                   < 2 > Patient Database Maintenance                               **\n" +
                        "**                                   < 3 > Inventory Maintenance                                      **\n" +
                        "**                                   < 4 > Dispensing System                                          **\n" +
                        "**                                   < 5 >         QUIT                                               **\n" +
                        "**                                                                                                    **\n" +
                        "**                                                                                                    **\n" +
                        "**                                                                                                    **\n" +
                        "********************************************************************************************************\n" +
                        "                                         ENTER CHOICE (1-5) > "
                    );

            userChoice = Integer.parseInt(cin.nextLine());
            String pause;

            switch (userChoice)
            {
                case 1:
                    DrugDBMenu drugDBMenu = new DrugDBMenu(entityManager);
                    drugDBMenu.display ();
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
            }
        }
        while (userChoice != 5);

        return (userChoice == 5) ? 0 : 1;
    }
}
