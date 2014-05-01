import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Justin
 */
public class BrumleyP4 
{
    public static void main(String[] args) throws FileNotFoundException
    {
        // Initialize variables
        Scanner scan;
        String filename;
        int numBoards;
        
        if(args.length != 1)
        {
            // Innappriate amount of arguments so print error and return.
            System.out.println("Error: Invalid amount of arguments.");
            return;
        }
        else
        {
            // Grab the filename from the arguments list.
            filename = args[0];
        }
        
        // Import the file using the scanner object.
        scan = new Scanner(new File(filename));
        
        // Retrieves first number in the text file, which should be the number
        // of boards the program needs to solve.
        numBoards = scan.nextInt();
        
        // Loop through all of the boards in the file using the numBoards variable.
        for(int i = 0; i < numBoards; i++)
        {
            char temp[][] = new char[5][5];
            
            for(int j = 0; j < 5; j++)
            {
                String line = scan.next();
                for(int k = 0; k < 5; ++k)
                {
                    temp[j][k] = line.charAt(k);
                }
            }
            
            // Create new board, which will generate a solution...
            BrumleyBoard board = new BrumleyBoard(temp);
        }
    }
}
