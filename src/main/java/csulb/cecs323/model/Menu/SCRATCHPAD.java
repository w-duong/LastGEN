import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public ArrayList<String> paragraph = new ArrayList<>();
    public static final Scanner cin = new Scanner (System.in);
    
	public static void main(String[] args) 
	{
	    String example = "In addition to the primary volume descriptor " +
	                    "a CD-ROM may contain a supplementary volume descriptor.\n" +
	                    "All-natural treats with simple ingredients.\n" +
	                    "\n " + 
	                    "...Because dogs deserve...\n " + "\n" + 
	                    "REAL FOOD.\n";
	    
	    // outer loop for continuous, iterative editing (necessary???)
		for (int i = 0; i < 4; ++i)
		{
		    String [] edited = splitter (example);
		    
		    example = String.join ("\n", edited);
		}
		
	}
	
	public static String [] splitter (String longstring)
	{
	    // split initial string into line form
	    String [] results = longstring.split ("\n");

        // display for user to select line to edit	    
	    for (int i = 0; i < results.length; ++i)
	        System.out.println (String.format ("Line%3d : %s", i + 1, results[i]) );
	        
	    // prompt user for input
        System.out.print ("\nINPUT CHOICE TO EDIT: ");
        int choice = Integer.parseInt (cin.nextLine()) - 1;
        
        //'buffer' = reusable, temporary variable; 'toAdd' = need ArrayList to allow for growth
        String buffer = null;
        ArrayList<String> toAdd = new ArrayList<>();
        System.out.println ("\nINPUT NEW PASSAGE (ENTER 'QQQ' TO FINISH: ");
        
        // continue "buffer"ing into list 'toAdd' until finished editing
        while (!(buffer = cin.nextLine()).equals ("QQQ"))
            toAdd.add (buffer);
        
        // reset 'buffer' for final time, then incrementally add from 'toAdd'
        buffer = "";
        for (String line : toAdd)
            buffer += line + "\n";
            
        results[choice] = buffer;
        
	    return results;
	}
}
