package games.chess;

import java.util.HashMap;

/**
 * Transposition table that stores previous states
 */
public class TranspositionTable {

    /**
     * Subclass that handles data related to a specific board state
     */
    static class TableEntry {

        private int depth;
        private Integer value;
        private String bestMove;

        /**
         * Constructor for a TableEntry in a TranspositionTable
         * @see TranspositionTable
         * @param depth the depth in the game tree the board state was observed at
         * @param value the h-value returned from pruning the game tree
         * @param bestMove the current best action for this state
         */
        public TableEntry(int depth, Integer value, String bestMove) {
            this.depth = depth;
            this.value = value;
            this.bestMove = bestMove;
        }
    }

    HashMap<String, TableEntry> table;  // maps board state --> TableEntry

    /**
     * Constructor for a TranspositionTable, initializes HashMap
     */
    public TranspositionTable() {
        table = new HashMap<>();
    }

    /**
     * Determine whether a board state exists in the transposition table
     * @param boardState the board state from {@code ChessBoard.toString()}
     * @return true if the table contains this state, false if not
     */
    public boolean hasEntry(String boardState) {
        return table.containsKey(boardState);
    }

    /**
     * Adds or updates the TableEntry for a board state
     * @param boardState the board state to update the TableEntry
     * @param depth the depth in the game tree the board state was observed at
     * @param value the h-value returned from pruning the game tree
     * @param bestMove the current best action for this state
     */
    public void putEntry(String boardState, int depth, Integer value, String bestMove) {
        table.put(boardState, new TableEntry(depth, value, bestMove));
    }

    /**
     * Get the stored depth for the given board state
     * @param boardState the board state from {@code ChessBoard.toString()}
     * @return the depth this state was encountered at in the game tree
     */
    public int getDepth(String boardState) {
        return table.get(boardState).depth;
    }

    /**
     * Get the stored value for the given board state
     * @param boardState the board state from {@code ChessBoard.toString()}
     * @return the h-value associated with this state
     */
    public Integer getValue(String boardState) {
        return table.get(boardState).value;
    }

    /**
     * Get the stored best move to make from the given board state
     * @param boardState the board state from {@code ChessBoard.toString()}
     * @return the best move to make at this state, in UCI notation
     */
    public String getBestMove(String boardState) {
        return table.get(boardState).bestMove;
    }

}
