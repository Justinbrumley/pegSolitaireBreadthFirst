import java.util.LinkedList;

/**
 *
 * @author Justin
 */
public class BrumleyBoard 
{
    private char[][] _board;
    private int _smallestAmount;
    
    public BrumleyBoard(char[][] board)
    {
        // If the board passed in is not the appropriate size, return here....
        if(board.length != 5 || board[0].length != 5)
        {
            System.out.println("Not enough chars in matrix.");
            return;
        }
        
        // Clone the board to the one in this class.
        cloneBoard(board);
        
        // Set up the queue for the loop.
        LinkedList<BrumleyNode> nodes = new LinkedList<BrumleyNode>();
        
        // Init the root of the tree.
        BrumleyNode root = new BrumleyNode(_board);
        
        // Set the smallest amount variable to the number of pegs in the root node.
        _smallestAmount = root.getPegCount();
        
        // Add the root to the queue...
        nodes.offerLast(root);
        int nodeCounter = 0;
        int initialPegCount = root.getPegCount();
        
        // Loop until the queue is empty or a solution down to one peg has been found...
        while(!nodes.isEmpty() && _smallestAmount > 1)
        {
            // Pull the first node from the queue.
            BrumleyNode node = nodes.pollFirst();
            nodeCounter++;

            /*       
            System.out.println("Node " + nodeCounter + ": ");
            node.printBoard();
            System.out.println();
            */
            
            if(node.getPegCount() > 1)
               node.createChildren();
            
            // If the node has children, add them to the queue.
            if(node.getChildren().size() != 0)
                for(int i = 0; i < node.getChildren().size(); ++i)
                {
                    // Creates a variable of the child at the particular index.
                    BrumleyNode child = node.getChildren().get(i);
                    
                    
                    if(nodes.contains(child))
                        continue;
                    else
                    {
                        // Check the child to see if a new low has been reached:
                        if(child.getPegCount() < _smallestAmount)
                        {
                            // If new low found, set the low as well as what level it is on.
                            _smallestAmount = child.getPegCount();
                        }

                        // Add the child to the queue.
                        nodes.offerLast(child);
                    }
                }
        }
        
        // Calculate how many moves was made the cheap way:
        int moves = initialPegCount - _smallestAmount;
        
        // Print out the best case:
        System.out.println("The best case ends with " + _smallestAmount + " pegs after " + moves + " moves and " + nodeCounter + " nodes generated.");
    }
    
    // ----- Private Methods -----
    
    /**
     * Clones the parameter board into the private _board variable of this object.
     * @param board - The board to clone.
     */
    private void cloneBoard(char[][] board)
    {
        _board = new char[5][5];
        
        for(int i = 0; i < 5; i++)
            _board[i] = board[i].clone();
    }
    
    private boolean isPromising(BrumleyNode node)
    {
        return true;
    }
    
    private void printBoard()
    {
        for(int i = 0; i < 5; i++)
        {
            for(int j = 0; j < 5; ++j)
            {
                System.out.print(_board[i][j]);
            }
            System.out.println();
        }
    }
}
