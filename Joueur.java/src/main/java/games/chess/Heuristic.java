package games.chess;

/**
 * Abstract parent class for defining common heuristics
 */
public interface Heuristic {

    /**
     * Abstract method for subclasses to implement custom heuristic
     * @param chessBoard the ChessBoard to find the heuristic value of
     * @return integer representing the board's heuristic value
     */
    int h(ChessBoard chessBoard);

}
