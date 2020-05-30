package csulb.cecs323.model.Menu;

import java.util.Scanner;

public class ClassDBsubmodule
{
    private static final Scanner cin = new Scanner (System.in);

    public void addClass ()
    {
        int userChoice, classCount;
        String tempAbbr = "AAAA", tempName = "DEFAULT";

        do
        {
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
                            String.format  ("**  < 1 > Enter Drug Class Name                   > %-50s**\n", tempName.substring(0, 45)) +
                            String.format  ("**  < 2 > Enter Drug Class Abbreviation           > %-5s                                         **\n", tempAbbr) +
                            "**  < 3 > Return to previous menu                                                                     **\n" +
                            "**                                                                                                    **\n" +
                            "********************************************************************************************************\n" +
                            "                                          CHOICE (0 - 3): "
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
                case 3:
            }
        }
        while (userChoice != 3);
    }
}
