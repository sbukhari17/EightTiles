/** Board.java
 *  Creates a board based on user input and runs an interactive loop to either allow the user to solve the board or to automatically solve it
 *  if the board is solvable, prints out a step-by-step solution
 *  if the board is unsolvable, prints out a best-found solution
 *
 * Created by help
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import static java.lang.System.exit;

public class Board {
    public int grid[][];
    HashMap<Integer, Coordinate> mapFromIntegerToCoord = new HashMap<>();

    public Board(int choice) {
        if (choice == 1) {
            generateBoard(new Random(System.currentTimeMillis()));
        } else if (choice == 2) {
            generateBoard();
        } else {
            Constants.outputStream.println("Invalid input given.");
            exit(1);
        }

    }

    /**
     * Performs a deep copy of a board by copying its grid and mappings.
     * @param b
     */
    public Board(Board b) {
        grid = new int[Constants.dimX][Constants.dimY];
        for (int i = 0; i < Constants.dimX; i++) {
            for (int j = 0; j < Constants.dimY; j++) {
                grid[i][j] = b.grid[i][j];
                mapFromIntegerToCoord.put(b.grid[i][j], new Coordinate(i, j));
            }
        }
    }

    /**
     * compares the grid of this to board board for equality
     * @param b
     * @return
     */
    public boolean equals(Board b) {
        for (int i = 0; i < Constants.dimX; i++) {
            if (!grid[i].equals(b.grid[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * generated a board using a random seeded with time.
     * 1. generate 9 unique, random integers (0-9)
     * 2. export them into an array
     * 3. convert into an integer array
     * 4. set up grid
     *
     * @param rand
     */
    private void generateBoard(Random rand) {
        grid = new int[Constants.dimX][Constants.dimY];
        int[] boardVals = new int[Constants.gridSize];
        for (int i = 0; i < (Constants.gridSize); i++) {
            boardVals[i] = i;
        }
        shuffleNumbers(boardVals, rand);
        int arrIndex = 0;
        for (int i = 0; i < Constants.dimX; i++) {
            for (int j = 0; j < Constants.dimY; j++) {
                int num = boardVals[arrIndex++];
                mapFromIntegerToCoord.put(num, new Coordinate(i, j));
                grid[i][j] = num;
            }
        }
    }

    /**
     * Generates board based on user input
     * 1. take input from user
     * 2. parse char by char
     * 3. input into grid
     */
    private void generateBoard() {
        Scanner scan = new Scanner(Constants.inputStream);
        Constants.outputStream.println("Some boards such as 728045163 are impossible.");
        Constants.outputStream.println("Others such as 245386107 are possible.");
        Constants.outputStream.print("Enter a string of 6 digits (including 0) for the board --> ");
        String input = scan.nextLine().trim();
        grid = new int[Constants.dimX][Constants.dimY];
        int strIndex = 0;

        for (int i = 0; i < Constants.dimX; i++) {
            for (int j = 0; j < Constants.dimY; j++) {
                int num = Character.getNumericValue(input.charAt(strIndex++));
                mapFromIntegerToCoord.put(num, new Coordinate(i, j));
                grid[i][j] = num;
            }
        }
    }


    /**
     * Takes an array and shuffles it a random number of times
     * @param arr
     * @param rand
     */
    private void shuffleNumbers(int[] arr, Random rand) {
        int numTimesToSwap = rand.nextInt(200);
        for (int i = 0; i < numTimesToSwap; i++) {
            int index1 = rand.nextInt((Constants.gridSize) - 1);
            int index2 = rand.nextInt((Constants.gridSize) - 1);
            int tmp = arr[index2];
            arr[index2] = arr[index1];
            arr[index1] = tmp;
        }
    }

    /**
     * prints out the current grid
     */
    public void printBoard() {
        for (int i = 0; i < Constants.dimX; i++) {
            Constants.outputStream.print("  ");
            for (int j = 0; j < Constants.dimY; j++) {
                Constants.outputStream.print(grid[i][j] == 0 ? "  " : grid[i][j] + " ");
            }
            Constants.outputStream.print("\n");
        }
    }

    /**
     * determines whether the current grid is already solved
     * @return
     */
    public boolean isSolved() {
        for (int i = 0; i < Constants.dimX; i++) {
            for (int j = 0; j < Constants.dimY; j++) {
                if (i == Constants.dimX - 1 && j == Constants.dimY - 1) { //if at bottom right corner, index should be 0
                    if (grid[i][j] != 0) {
                        return false;
                    }
                } else if (grid[i][j] != (i * Constants.dimX) + j + 1) //every other index should satisfy its spot
                    return false;
            }
        }
        return true;
    }

    /**
     * slides a piece into the empty slot
     * @param n
     */
    public void makeMove(int n) {
        if (mapFromIntegerToCoord.containsKey(n)) {
            Coordinate coord = mapFromIntegerToCoord.remove(n);
            Coordinate zeroCoord = mapFromIntegerToCoord.remove(0);
            grid[zeroCoord.X][zeroCoord.Y] = grid[coord.X][coord.Y];
            grid[coord.X][coord.Y] = 0;
            mapFromIntegerToCoord.put(0, new Coordinate(coord.X, coord.Y));
            mapFromIntegerToCoord.put(n, new Coordinate(zeroCoord.X, zeroCoord.Y));

        }
    }

    /**
     * determines whether a given move is valid
     * @param n
     * @return
     */
    public boolean isValidMove(int n) {
        Coordinate coord = mapFromIntegerToCoord.get(n);
        if (mapFromIntegerToCoord.containsKey(n)) {
            if (coord.X + 1 <= Constants.dimX - 1) {
                if (grid[coord.X + 1][coord.Y] == 0) {
                    return true;
                }
            }
            if (coord.Y + 1 <= Constants.dimY - 1) {
                if (grid[coord.X][coord.Y + 1] == 0) {
                    return true;
                }
            }
            if (coord.X - 1 >= 0) {
                if (grid[coord.X - 1][coord.Y] == 0) {
                    return true;
                }
            }
            if (coord.Y - 1 >= 0) {
                if (grid[coord.X][coord.Y - 1] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *  while the board is unsolved, allows the user to:
     *      a. make a move (if it is valid)
     *      board. prompt for an automatic solution (if one exists, otherwise print the best board found)
     *      c. quit
     *
     */
    public void interactiveLoop() {
        Scanner scan = new Scanner(Constants.inputStream);
        int boardCounter = 1;
        Constants.outputStream.println("Initial board is:");
        boolean autoSolve = false;
        while (!isSolved() && !autoSolve) {
            Constants.outputStream.println(boardCounter++ + ".");
            printBoard();
            Constants.outputStream.println("Heuristic Value: " + currentHeuristic());
            Constants.outputStream.print("\nPiece to move: ");
            String move = scan.next();
            char m = move.charAt(0);
            if (m == 's') {
                autoSolve = true;
            } else {
                int numericInput = Character.getNumericValue(m);
                if (numericInput == 0) {
                    Constants.outputStream.println("\nExiting program.");
                    exit(0);
                }
                if (isValidMove(numericInput)) {
                    makeMove(numericInput);
                } else {
                    Constants.outputStream.println("*** Invalid move.   Please retry.");
                }
            }
        }
        if (!isSolved()) {
            SearchTree tree = autoSolve();
            if (tree.bestBoardHeuristic > 0) {
                Constants.outputStream.println("\nAll " +tree.oldBoards.size() + " moves have been tried.");
                Constants.outputStream.println("That puzzle is impossible to solve. Best board found:");
                tree.bestBoardFound.printBoard();
                Constants.outputStream.println("Heuristic value: " + tree.bestBoardHeuristic);
                Constants.outputStream.println("\nExiting program.");
            } else {
                Constants.outputStream.println("1.");
                printBoard();
                tree.createPath();
                ArrayList<Board> list = tree.path;

                for (int i = 0; i < list.size(); i++) {
                    Constants.outputStream.println(i + 2 + ".");
                    list.get(i).printBoard();
                }
                Constants.outputStream.println("\nDone.");
            }
        }

    }

    /**
     * attempts to solve the board by:
     *  repeatedly making the best move possible by using a calculated heuristic at each step
     * @return SearchTree
     */
    public SearchTree autoSolve() {
        Constants.outputStream.println("Solving puzzle automatically..........................");
        Node v = new Node(null, this);
        SearchTree tree = new SearchTree(v);
        boolean unsolvable = false;
        while (!v.board.isSolved() && !unsolvable) {
            ArrayList<Board> children = v.board.getChildren();
            for (Board b : children) {
                tree.addNode(new Node(v.board, b));
            }
            Node nextMove = tree.pop();
            if (nextMove == null) {
                unsolvable = true;
            } else {
                v = nextMove;
            }
        }
        return tree;
    }

    /**
     * Calculates the intended coordinate of a given integer.
     *  ex:
     *      1's intended coordinate in a 3x3 grid is (0,0)
     *      2's intended coordinate in a 3x3 grid is (0,1)
     *      8's intended coordinate in a 3x3 grid is (2,1)
     * @param n
     * @return a coordinate with the intended position of n
     */
    private static Coordinate getIntendedCoordinate(int n) {
        int intX = (n - 1) / Constants.dimX;
        int intY = (n - 1) % Constants.dimY;
        return new Coordinate(intX, intY);
    }

    /**
     * calculates the number of moves required to get a given number into it's intended location
     * @param n
     * @return
     */
    private int movesFromIntendedSlot(int n) {
        Coordinate currCoordinate = mapFromIntegerToCoord.get(n);
        if (n == 0) {
            return Math.abs((Constants.dimX - 1) - currCoordinate.X) + Math.abs((Constants.dimY - 1) - currCoordinate.Y);
        } else {
            Coordinate intendedCoordinate = getIntendedCoordinate(n);
            return Math.abs(intendedCoordinate.X - currCoordinate.X) + Math.abs(intendedCoordinate.Y - currCoordinate.Y);
        }
    }

    /**
     * calculates the heuristic of the current board by calculating the moves required to get the number at each index into it's desired index
     * @return
     */
    public int currentHeuristic() {
        int total = 0;
        for (int i = 0; i < Constants.dimX; i++) {
            for (int j = 0; j < Constants.dimY; j++) {
                total += movesFromIntendedSlot(grid[i][j]);
            }
        }
        return total;
    }

    /**
     * returns an ArrayList of board objects containing potential children of the current board
     * @return
     */
    public ArrayList<Board> getChildren() {
        ArrayList<Board> children = new ArrayList<>();
        Coordinate posOfZero = mapFromIntegerToCoord.get(0);

        if (posOfZero.X + 1 <= Constants.dimX - 1) {
            Board aBoard = new Board(this);
            aBoard.makeMove(grid[posOfZero.X + 1][posOfZero.Y]);
            children.add(aBoard);
        }
        if (posOfZero.Y + 1 <= Constants.dimY - 1) {
            Board aBoard = new Board(this);
            aBoard.makeMove(grid[posOfZero.X][posOfZero.Y + 1]);
            children.add(aBoard);
        }
        if (posOfZero.X - 1 >= 0) {
            Board aBoard = new Board(this);
            aBoard.makeMove(grid[posOfZero.X - 1][posOfZero.Y]);
            children.add(aBoard);
        }
        if (posOfZero.Y - 1 >= 0) {
            Board aBoard = new Board(this);
            aBoard.makeMove(grid[posOfZero.X][posOfZero.Y - 1]);
            children.add(aBoard);
        }
        return children;
    }
}

