package csulb.cecs323.model.Menu;

import java.util.Scanner;

public class DrugDBMenu
{
    private static final Scanner cin = new Scanner (System.in);

    public int display ()
    {
        int userChoice;

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
                    ClassDBsubmodule submodule = new ClassDBsubmodule();
                    submodule.addClass();
                    break;

            }
        }
        while (userChoice != 7);

        return (userChoice == 7) ? 0 : 1;
    }



}
