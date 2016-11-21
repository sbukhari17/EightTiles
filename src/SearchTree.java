/**
 * Used to hold a priority queue containing nodes. Also stores the best found node and its heuristic in the case of a solution not being found.
 * Created by help
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

public class SearchTree {
    public PriorityQueue<Node> boardQueue;
    public HashMap<String, Node> oldBoards;
    Board bestBoardFound;
    int bestBoardHeuristic;
    Node rootNode;
    ArrayList<Board> path;
    int currentBoardIndex;


    public SearchTree(Node n) {
        rootNode = new Node(null, n.board);
        bestBoardFound = new Board(n.board);
        bestBoardHeuristic = n.heuristic;
        boardQueue = new PriorityQueue<>();
        oldBoards = new HashMap<>();

        oldBoards.put(convertBoardToStringSequence(n.board), n);
    }

    /**
     * Adds a node to the priority queue if it doesn't already exist in the old boards
     * @param n
     */
    public void addNode(Node n) {
        String strSeq = convertBoardToStringSequence(n.board);
        if (!oldBoards.containsKey(strSeq)) {
            if (n.heuristic < bestBoardHeuristic) {
                bestBoardFound = n.board;
                bestBoardHeuristic = n.heuristic;
            }
            boardQueue.offer(n);
            oldBoards.put(strSeq, n);
        }
    }

    /**
     * pops a node from the top of the queue and returns it
     * @return
     */
    public Node pop() {
        Node ret = boardQueue.poll();
        return ret;
    }

    /**
     * Converts a nxn grid from the board of a node to a string sequence to allow us to map it to said node
     * @param b
     * @return
     */
    public String convertBoardToStringSequence(Board b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Constants.dimX; i++) {
            for (int j = 0; j < Constants.dimY; j++) {
                sb.append(b.grid[i][j]);
            }
        }
        return sb.toString();
    }

    /**
     * constructs a path to the solution by calling pathHelper on the best found board and stores it in an ArrayList.
     * @return the reverse of the resulting ArrayList because of how the recursive function is constructed
     */
    public void createPath() {
        path = new ArrayList<>();
        pathHelper(oldBoards.get(convertBoardToStringSequence(bestBoardFound)), path);
        Collections.reverse(path);
    }
    public Board getNextBoard(){
        return path.get(currentBoardIndex++);
    }

    /**
     * recursively adds nodes to an ArrayList by following a given node up its tree until it reaches a root node whose predecessor is null
     * @param n
     * @param path
     */
    public void pathHelper(Node n, ArrayList<Board> path) {
        if (n.previousBoard == null) {
            return;
        }
        path.add(n.board);
        pathHelper(oldBoards.get(convertBoardToStringSequence(n.previousBoard)), path);
    }
}
