package games.chess;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ChessSolver that implements Time-Limited Iterative-Deepening Depth-Limited Minimax
 * with Alpha-Beta Pruning, Quiescent Search, and History Table to compute the best move
 */
public class QuiescentSolver extends ChessSolver implements Heuristic {

    private static final int MAX_DEPTH_LIMIT = 2;  // the maximum depth that is reached in iterative deepening
    private double timeRemaining;  // the total time left for the player until the end of the game
    private double startTime;  // time at which the player's turn begins
    private HistoryTable historyTable;

    /**
     * Constructor for QuiescentSolver
     * @param chessBoard current state of the ChessBoard
     * @param currentPlayer the current player's color
     * @param timeRemaining the total time left for the player until the end of the game
     * @param historyTable reference to the player's history table, developed throughout the game
     */
    public QuiescentSolver(ChessBoard chessBoard, Color currentPlayer, double timeRemaining, HistoryTable historyTable) {
        super(chessBoard, currentPlayer);
        this.timeRemaining = timeRemaining;
        this.historyTable = historyTable;
    }

    /**
     * Determines the best move as indicated by TI-ID-DL-MM-AB-QS-HT
     * @return string in UCI notation for the minimax move
     */
    @Override
    public String computeBestMove() {
        startTime = System.nanoTime();
        double allocatedTime = allocatedTurnTime();
        String action = null;

        ArrayList<String> possibleMoves = chessBoard.findAllMoves(currentPlayer);
        String moveFromHistory = findMoveFromHistory(possibleMoves);
        if(moveFromHistory != null) {
            return moveFromHistory;
        }

        for(int k = 1; k <= MAX_DEPTH_LIMIT; k++) {  // iterative deepening from depth 1 to MAX_DEPTH_LIMIT
            double elapsedTime = System.nanoTime() - startTime;
            if(elapsedTime > allocatedTime) {
                break;  // break to return the most recently found action
            }

            String searchResult = quiescentSearch(chessBoard, possibleMoves, k, allocatedTime);
            if(searchResult == null) {  // a timeout occurred, so return the most recently computed action
                break;
            } else {  // the minimax search returned a result within the allocated time
                action = searchResult;
            }
        }

        // add action to history table if it is optimal
        historyTable.insertOrIncrement(action, (int)Math.pow(2, 1));

        return action;
    }

    /**
     * Perform a quiescent search up to the given depth limit, without going overtime
     * @param chessBoard The initial state of the chess board
     * @param possibleMoves The list of possible moves for the current player
     * @param depthLimit The depth limit of actions into the game tree
     * @param allocatedTime The amount of time allotted to determine the move to make
     * @return a String in UCI notation representing the best move at this depth limit, or null if a timeout occurs
     */
    private String quiescentSearch(ChessBoard chessBoard, ArrayList<String> possibleMoves, int depthLimit, double allocatedTime) {
        Integer value;
        value = maxValue(chessBoard, 0, depthLimit, -INFINITY, INFINITY, allocatedTime, null);
        if(value == null) {
            return null;  // a timeout is signalled by returning null
        }

        String bestAction = null;
        for(String move : possibleMoves) {
            Integer minimax;
            minimax = minValue(result(chessBoard, move), 0, depthLimit, -INFINITY, INFINITY, allocatedTime, move);
            if(minimax == null) {
                return null;
            }
            if(minimax.equals(value)) {
                bestAction = move;
            }
        }

        // in case no best move is found, compute a random one beforehand to prevent going over allocated time
        if(bestAction == null) {
            RandomSolver randomSolver = new RandomSolver(chessBoard, currentPlayer);
            bestAction = randomSolver.computeBestMove();
        }

        return bestAction;
    }

    /**
     * Computes the maxValue at a certain depth
     * @param chessBoard the current state of the board
     * @param depth the current depth of actions into the state tree
     * @param depthLimit the limit on the depth, at which a heuristic value is returned
     * @param alpha threshold for trimming branches from MIN player
     * @param beta threshold for trimming branches from MAX player
     * @param allocatedTime The amount of time allotted to determine the move to make
     * @param action The action that produced this ChessBoard
     * @return the value for MAX player at the given board state, or null if a timeout occurs
     */
    private Integer maxValue(ChessBoard chessBoard, int depth, int depthLimit, int alpha, int beta, double allocatedTime, String action) {
        double elapsedTime = System.nanoTime() - startTime;
        if(elapsedTime > allocatedTime) {
            return null;  // null signifies that a timeout occurred
        }

        ChessBoard clonedBoard = new ChessBoard(chessBoard);
        if(cutoff(chessBoard, depth, depthLimit)) {
            historyTable.insertOrIncrement(action, (int)Math.pow(2, depth));
            return h(chessBoard);  // if at a cutoff, approximate using heuristic value
        }

        int value = -INFINITY;
        ArrayList<String> allMoves = clonedBoard.findAllMoves(currentPlayer);
        for(String move : allMoves) {
            try {
                Integer min = minValue(result(clonedBoard, move), depth + 1, depthLimit, alpha, beta, allocatedTime, move);
                if (min == null) {  // a timeout occurred
                    return null;
                }
                value = Math.max(value, min);
                if (value >= beta) {
                    return value;  // if value exceeds the beta threshold, cut this branch
                }
                alpha = Math.max(alpha, value);  // set a new threshold for alpha
            } catch (StackOverflowError e) {
                return h(chessBoard);  // return the heuristic value if quiescent search runs out of memory
            }
        }

        return value;
    }

    /**
     * Computes the minValue at a certain depth
     * @param chessBoard the current state of the board
     * @param depth the current depth of actions into the state tree
     * @param depthLimit the limit on the depth, at which a heuristic value is returned
     * @param alpha threshold for trimming branches from MIN player
     * @param beta threshold for trimming branches from MAX player
     * @param allocatedTime the time allotted for completing this turn
     * @param action the action that produced this ChessBoard
     * @return the value for MIN player at the given board state, or null if a timeout occurs
     */
    private Integer minValue(ChessBoard chessBoard, int depth, int depthLimit, int alpha, int beta, double allocatedTime, String action) {
        double elapsedTime = System.nanoTime() - startTime;
        if(elapsedTime > allocatedTime) {
            return null;  // null signifies that a timeout occurred
        }

        ChessBoard clonedBoard = new ChessBoard(chessBoard);
        if(cutoff(chessBoard, depth, depthLimit)) {
            historyTable.insertOrIncrement(action, (int)Math.pow(2, depth));
            return h(chessBoard);
        }

        int value = INFINITY;
        ArrayList<String> allMoves = clonedBoard.findAllMoves(currentPlayer);
        for (String move : allMoves) {
            try {
                Integer max = maxValue(result(clonedBoard, move), depth + 1, depthLimit, alpha, beta, allocatedTime, move);
                if (max == null) {  // a timeout occurred
                    return null;
                }
                value = Math.min(value, max);
                if (value <= alpha) {
                    return value;  // if the value is below the alpha threshold, cut this branch
                }
                beta = Math.min(beta, value);  // set a new threshold for beta
            } catch (StackOverflowError e) {
                return h(chessBoard);  // return the heuristic value if quiescent search runs out of memory
            }
        }

        return value;
    }

    /**
     *
     * @param chessBoard the current state of the ChessBoard
     * @param depth the current depth of the game tree
     * @param depthLimit the current maximum depth for iterative deepening
     * @return true if in a terminal state or in a quiescent state at or beyond the depth limit, false otherwise
     */
    public boolean cutoff(ChessBoard chessBoard, int depth, int depthLimit) {
        if(terminal(chessBoard)) {
            return true;
        } else if(depth >= depthLimit && isQuiescent(chessBoard)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines whether a board state is quiescent; that is, the potential captures for
     * the opponent on the next turn are below a certain threshold
     * @param chessBoard the ChessBoard state
     * @return true if the ChessBoard is quiescent for the current player, false otherwise
     */
    public boolean isQuiescent(ChessBoard chessBoard) {
        ArrayList<ChessPiece> playerPieces = currentPlayer == Color.WHITE ? chessBoard.getWhitePieces() : chessBoard.getBlackPieces();
        HashMap<String, Boolean> potentialCaptures = chessBoard.getAttackedTiles();
        int pieceValueTotal = 0;
        int quiescenceScore = 0;
        for(ChessPiece piece : playerPieces) {  // for each of the player's pieces, determine
            pieceValueTotal += piece.getValue();  // accumulate total value of all player's pieces
            if(potentialCaptures.get(piece.getPieceTile())) {  // if the piece could be captured by the opponent
                quiescenceScore += piece.getValue();  // update the score piece value
            }
        }
        double pieceValueThreshold = 0.90 * pieceValueTotal;  // 90% of the total value of all the player's pieces
        return quiescenceScore <= pieceValueThreshold;  // quiescent only if the score is within this threshold
    }

    /**
     * Heuristic function to determine the relative value of a ChessBoard
     * @param chessBoard the ChessBoard to find the heuristic value of
     * @return heuristic value; sum of the relative values of all pieces on the board
     */
    @Override
    public int h(ChessBoard chessBoard) {
        int hValue = 0;
        ArrayList<ChessPiece> whitePieces = chessBoard.getWhitePieces();
        ArrayList<ChessPiece> blackPieces = chessBoard.getBlackPieces();

        for(ChessPiece piece : whitePieces) {
            if(currentPlayer == Color.WHITE) {
                hValue += piece.getValue();  // white pieces increase h-value for white player
            } else {
                hValue -= piece.getValue();  // black pieces decrease h-value for white player
            }
        }

        for(ChessPiece piece : blackPieces) {
            if(currentPlayer == Color.BLACK) {
                hValue += piece.getValue();  // black pieces increase h-value for black player
            } else {
                hValue -= piece.getValue();  // white pieces decrease h-value for black player
            }
        }

        return hValue;
    }

    /**
     * Compute the allocated time for the turn based on how much time is left for the player
     * @return player's time limit for the current turn
     */
    public double allocatedTurnTime() {
        return timeRemaining * 0.02;  // 2% of the remaining time
    }

    /**
     * Looks at history table of good moves to see if they can be replayed
     * @param possibleMoves list of possible moves on the current ChessBoard
     * @return the String in UCI notation for the move with the highest score, or null if none exists
     */
    public String findMoveFromHistory(ArrayList<String> possibleMoves) {
        Integer maxScore = 0;
        String maxMove = null;
        for(String move : possibleMoves) {
            if(historyTable.containsAction(move) && historyTable.getScore(move) > maxScore) {
                maxScore = historyTable.getScore(move);
                maxMove = move;
            }
        }
        return maxMove;
    }

}
