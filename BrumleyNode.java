import java.util.ArrayList;

/**
 *
 * @author Justin
 */
public class BrumleyNode
{
    // Stores the current board being examined by this node.
    private char _currentBoard[][];
    
    // Tracks how many pegs are on the board, and how many jumps are available.
    private int _pegsLeft;
    private int _jumpsAvailable;
    
    // For every jump available, these lists track the position from where the jump
    // starts, to where the peg will land.
    private int[] _jumpFrom = new int[10];
    private int[] _jumpTo = new int[10];
    
    private ArrayList<BrumleyNode> _children = new ArrayList<BrumleyNode>();
    
    private int _level;
    
    /**
     * Default constructor for the node class. Requires a 5x5 board to be passed in.
     * This constructor will be used to initialize the root node.
     * @param board - The board to clone over.
     */
    public BrumleyNode(char[][] board)
    {
        // Clones the passed in board to the _currentBoard variable.
        cloneBoard(board);
        
        // Check the board to count the amount of pegs.
        checkBoard();
        
        // Check for jumps and init the jump variables
        checkJumps();
        
        // Init the children
        _children = new ArrayList<BrumleyNode>();
        
        // Call the generate children method to possibly generate child nodes.
        //createChildren();
        
        // Sets the current level to 0 because no moves have been made yet.
        _level = 0;
        
    }
    
    /**
     * Constructor for the Left Child nodes.
     * It requires a new board to be created after jumping the specified pegs.
     * Peg that needs to be jumped is included in the parameters.
     * @param board 
     */
    public BrumleyNode(char[][] board, int jumpFrom, int jumpTo, int pegsLeft, int curLevel)
    {
        // Clone the board over to the current board variable
        cloneBoard(board);
        
        // Set the level of this node.
        _level = curLevel;
        
        // Init the children variable
        _children = new ArrayList<BrumleyNode>();
        
        // Uses the jumpFrom integer to calculate the (i, j) position of the jumping peg.
        int i_from = (int) Math.floor((double) jumpFrom / 5.0f);
        int j_from = jumpFrom % 5;
        
        // Uses the jumpTo integer to calculate the (i, j) position of where the
        // jumping peg is to land.
        int i_to = (int) Math.floor((double) jumpTo / 5.0f);
        int j_to = jumpTo % 5;
        
        // Finds the midpoint of jumpFrom and jumpTo and uses that to calculate the
        // (i, j) position of the peg being jumped.
        double mid = (jumpFrom + jumpTo) / 2;
        int i_peg = (int) Math.floor(mid / 5.0f);
        int j_peg = (int) mid % 5;
        
        // The spaces of the peg being jumped and the peg doing the jumping will
        // be cleared out after the jump:
        _currentBoard[i_from][j_from] = '.';
        _currentBoard[i_peg][j_peg] = '.';
        
        // The position of the jumpTo integer should be filled because that is where
        // the jumping peg will land:
        _currentBoard[i_to][j_to] = 'o';
        
         // Assign the pegs left variable to that of the parents minus 1.
        _pegsLeft = pegsLeft - 1;
        
        // If only two pegs remain, check the distance between them:
        if(_pegsLeft == 2)
        {
            // Find the pegs:
            int peg_i = 0, peg_j = 0, peg2_i, peg2_j;
            boolean foundOne = false;
            
            peg2_i = i_to;
            peg2_j = j_to;
            
            for(int i = 0; i < 5 && !foundOne; i++)
            {
                for(int j = 0; j < 5 && !foundOne; ++j)
                {
                    if(_currentBoard[i][j] == 'o' && !(i == peg2_i && j == peg2_j))
                    {
                        foundOne = true;
                        peg_i = i;
                        peg_j = j;
                    }
                }
            }
            
            // Find the distance between the two points...
            double distance = Math.sqrt(Math.pow(peg_i - peg2_i, 2) + Math.pow(peg_j - peg2_j, 2));
            
            // If they are more than one tile away from each other, no use making children.
            if(distance > 1)
            {
                return;
            }
            else if(distance == 1)
            {
                // Check for jumps and init the jump variables because the board has probably
                // changed a lot since the parent node.
                checkJumps();

                // Call the generate children method to possibly generate child nodes.
                //createChildren();
            }
        }
        else if(_pegsLeft > 2)
        {
            // Check for jumps and init the jump variables because the board has probably
            // changed a lot since the parent node.
            checkJumps();
            
            // Call the generate children method to possibly generate child nodes.
            //createChildren();
        }
        else
        {
            // Only one peg remains so don't bother with children.
        }
    } 
    
    /**
     * Constructor for the Right Child nodes.
     * It requires the same board to be passed in, and will truncate the first
     * element off the passed in array lists for jumping, because the first element will
     * have been skipped by the parent node.
     */
    public BrumleyNode(char[][] board, int pegsLeft, int[] fromList, int[] toList, int jumpsAvailable, int curLevel)
    {
        // Clone the board, fromList, and toList:
        //cloneBoard(board);
        _currentBoard = board;
        //cloneJumpFromList(fromList);
        _jumpFrom = fromList;
        _jumpTo = toList;
        //cloneJumpToList(toList);
        
        // Set the level of this node.
        _level = curLevel;
        
        // Set the pegs left variable to that of the parent, because it did not change.
        _pegsLeft = pegsLeft;
        
        // Remove the last element in the arraylists:
        _jumpsAvailable = jumpsAvailable - 1;
        
        // Creates children of this node.
        //createChildren();
    }
    
    // ----- Private Methods -----
    
    /**
     * Creates child nodes for the current instance of the node class.
     * Children are only created if this node still has jumps available.
     * 
     * If jumps available is greater than 1, two children are created,
     * else if jumps is equal to 1, just the left node is created.
     * else if no jumps available, no children are created.
     */
    public void createChildren()
    {
        _children = new ArrayList<BrumleyNode>();
        
        if(_jumpsAvailable > 1)
        {
            /* Create a right and left child node */
            
            // The left child is responsible for jumping the first jump available in this node.
            BrumleyNode leftChild = new BrumleyNode(_currentBoard, _jumpFrom[_jumpsAvailable-1], _jumpTo[_jumpsAvailable-1], this.getPegCount(), _level + 1);
            
            // The right child skips that jump and moves on to the next ones to try.
            BrumleyNode rightChild = new BrumleyNode(_currentBoard, getPegCount(), _jumpFrom, _jumpTo, this.getJumpCount(), _level + 1);
            
            // Add the children to the child list
           // if(leftChild.getJumpCount() > 0)
                _children.add(leftChild);
            _children.add(rightChild);
        }
        else if(_jumpsAvailable == 1)
        {
            /* Create just a left child node */
            BrumleyNode onlyChild = new BrumleyNode(_currentBoard, _jumpFrom[_jumpsAvailable-1], _jumpTo[_jumpsAvailable-1], this.getPegCount(), _level + 1);
            
            // Add the only child to the children list
            //if(onlyChild.getJumpCount() > 0)
                _children.add(onlyChild);
        }
    }
    
    /**
     * Counts how many pegs are left in the board and sets the _pegsLeft variable.
     */
    private void checkBoard()
    {
        _pegsLeft = 0;
        
        for(int i = 0; i < 5; i++)
        {
            for(int j = 0; j < 5; ++j)
            {
                if(_currentBoard[i][j] == 'o')
                {
                    _pegsLeft++;
                }
            }
        }
    }
    
    /**
     * Checks the entire matrix for any possible jumps and adds them to the 
     * array lists.
     * Also increments the _jumpsAvailable variable by one for each jump found.
     */
    private void checkJumps()
    {
        // Resets the jump variables.
        _jumpsAvailable = 0;
        
        // Loops through the matrix, to check if each point is a jumpable peg.
        for(int i = 0; i < 5; i++)
        {
            for(int j = 0; j < 5; ++j)
            {
                if(_currentBoard[i][j] == 'o')
                    checkJumpAt(i, j);
            }
        }
    }
    
    /**
     * Checks for any jumps by the peg located at position i, j in the matrix.
     * This function is very long, but accomplishes checking north, south, east, and west
     * of the peg in question.
     * @param i - the row of the peg
     * @param j - the column of the peg to check
     */
    private void checkJumpAt(int i, int j)
    {
        /* CHECK TO THE LEFT: */
        
        // if two spaces to the left is off the board, or the piece located there
        // is a pound ('#'), no use checking anymore.
        if(j - 2 >= 0 && _currentBoard[i][j-2] != '#' && _currentBoard[i][j-2] != 'o')
        {
            // First check if the position immediately to the left is a peg.
            // if it is, check the next spot over to see if its empty.
            if(_currentBoard[i][j-1] == 'o')
            {
                if(_currentBoard[i][j-2] == '.')
                {
                    // Position must be converted to a single integer...
                    // Formula is: X + (Y * 5)
                    int jumpFromPos = (i * 5) + j;
                    int jumpToPos = (i * 5) + (j - 2);
                    
                    // Place positions into the arrays
                    _jumpFrom[_jumpsAvailable] = jumpFromPos;
                    _jumpTo[_jumpsAvailable] = jumpToPos;
                    
                    // If it is empty, that means a jump is available here, so
                    // it needs to be added to the list
                    _jumpsAvailable++;
                }
            }
        }
        
        /* CHECK TO THE Right: */
        
        // if two spaces to the right is off the board, or the piece located there
        // is a pound ('#'), no use checking anymore.
        if(j + 2 < 5 && _currentBoard[i][j+2] != '#' && _currentBoard[i][j+2] != 'o')
        {
            // First check if the position immediately to the right is a peg.
            // if it is, check the next spot over to see if its empty.
            if(_currentBoard[i][j+1] == 'o')
            {
                if(_currentBoard[i][j+2] == '.')
                {
                    // Position must be converted to a single integer...
                    // Formula is: X + (Y * 5)
                    int jumpFromPos = (i * 5) + j;
                    int jumpToPos = (i * 5) + (j + 2);
                    
                    // Place positions into the arrays
                    _jumpFrom[_jumpsAvailable] = jumpFromPos;
                    _jumpTo[_jumpsAvailable] = jumpToPos;
                    
                    // If it is empty, that means a jump is available here, so
                    // it needs to be added to the list
                    _jumpsAvailable++;
                    
                }
            }
        }
        
        /* CHECK DOWN: */
        
        // if two spaces down is off the board, or the piece located there
        // is a pound ('#'), no use checking anymore.
        if(i + 2 < 5 && _currentBoard[i+2][j] != '#' && _currentBoard[i+2][j] != 'o')
        {
            // First check if the position immediately down is a peg.
            // if it is, check the next spot down to see if its empty.
            if(_currentBoard[i+1][j] == 'o')
            {
                if(_currentBoard[i+2][j] == '.')
                {
                    // Position must be converted to a single integer...
                    // Formula is: X + (Y * 5)
                    int jumpFromPos = (i * 5) + j;
                    int jumpToPos = ((i + 2) * 5) + j;
                    
                    // Place positions into the arrays
                    _jumpFrom[_jumpsAvailable] = jumpFromPos;
                    _jumpTo[_jumpsAvailable] = jumpToPos;
                    
                    // If it is empty, that means a jump is available here, so
                    // it needs to be added to the list
                    _jumpsAvailable++;
                    
                }
            }
        }
        
        /* CHECK UP: */
        
        // if two spaces up is off the board, or the piece located there
        // is a pound ('#'), no use checking anymore.
        if(i - 2 >= 0 && _currentBoard[i-2][j] != '#' && _currentBoard[i-2][j] != 'o')
        {
            // First check if the position immediately up is a peg.
            // if it is, check the next spot up to see if its empty.
            if(_currentBoard[i-1][j] == 'o')
            {
                if(_currentBoard[i-2][j] == '.')
                {
                    // Position must be converted to a single integer...
                    // Formula is: X + (Y * 5)
                    int jumpFromPos = (i * 5) + j;
                    int jumpToPos = ((i - 2) * 5) + j;
                    
                    // Place positions into the arrays
                    _jumpFrom[_jumpsAvailable] = jumpFromPos;
                    _jumpTo[_jumpsAvailable] = jumpToPos;
                    
                    // If it is empty, that means a jump is available here, so
                    // it needs to be added to the list
                    _jumpsAvailable++;
                }
            }
        }
    }
    
    /**
     * Clones the parameter "board" into the _currentBoard variable.
     * @param board - The board to be cloned.
     */
    private void cloneBoard(char[][] board)
    {
        _currentBoard = new char[5][5];
        
        for(int i = 0; i < 5; i++)
            _currentBoard[i] = board[i].clone();
    }
    
    /**
     * Prints the current board off to the console.
     */
    public void printBoard()
    {
        for(int i = 0; i < 5; i++)
        {
            for(int j = 0; j < 5; ++j)
            {
                System.out.print(_currentBoard[i][j]);
            }
            System.out.println();
        }
    }
    
    /**
     * Clones the Jump To array list "list" into the _jumpTo arraylist variable
     * @param list - The list to be cloned
     */
    private void cloneJumpToList(int[] list)
    {
        System.arraycopy(list, 0, _jumpTo, 0, list.length);
    }
    
    /**
     * Clones the Jump From array list "list" into the _jumpFrom arraylist variable
     * @param list - The list to be cloned
     */
    private void cloneJumpFromList(int[] list)
    {
        System.arraycopy(list, 0, _jumpFrom, 0, list.length);
    }
    
    // ----- Getters and Setters -----
    
    public int getPegCount() { return _pegsLeft; }
    public void setPegCount(int amount) { _pegsLeft = amount; }
    
    public int getJumpCount() { return _jumpsAvailable; }
    
    public int getLevel() { return _level; }
    public void setLevel(int num) { _level = num; }
    
    public ArrayList<BrumleyNode> getChildren() { return _children; }
    
    public char[][] getBoard() { return _currentBoard; }
    
    // ----- Comparators -----
    
    public boolean equals(Object other)
    {
        if(!(other instanceof BrumleyNode))
            return false;
        
        // Typecase other as a node
        BrumleyNode otherNode = (BrumleyNode) other;
        
        for(int i = 0; i < 5; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                if(_currentBoard[i][j] != otherNode.getBoard()[i][j])
                    return false;
            }
        }
        
        return true;
    }
}
