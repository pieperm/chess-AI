package games.chess;

/**
 * Abstract class to encapsulate different kinds of solvers
 */
public abstract class ChessSolver {

    protected static final int INFINITY = 99999999;
    protected ChessBoard chessBoard;
    protected Color currentPlayer;

    /**
     * Constructor for a ChessSolver
     * @param chessBoard current state of the ChessBoard
     * @param currentPlayer the current player's color
     */
    public ChessSolver(ChessBoard chessBoard, Color currentPlayer) {
        this.chessBoard = chessBoard;
        this.currentPlayer = currentPlayer;
    }

    /**
     * Abstract method to compute the best move based on the chosen method
     * @return string in UCI notation for the computed move
     */
    public abstract String computeBestMove();

    /**
     * Determines if the board is in a terminal state
     * @param chessBoard current state of the ChessBoard
     * @return true if either king is checkmated
     */
    protected boolean terminal(ChessBoard chessBoard) {
        // determine if white is checkmated
        boolean whiteKingAttacked = chessBoard.getAttackedTiles().get(chessBoard.getWhiteKingPosition());
        boolean whiteCheckmated = whiteKingAttacked && chessBoard.findAllMoves(Color.WHITE).isEmpty();

        // determine if black is checkmated
        boolean blackKingAttacked = chessBoard.getAttackedTiles().get(chessBoard.getBlackKingPosition());
        boolean blackCheckmated = blackKingAttacked && chessBoard.findAllMoves(Color.BLACK).isEmpty();

        return whiteCheckmated || blackCheckmated;
    }

    /**
     * Compute the new board that results from taking an action (moving a piece)
     * @param initialBoard the current state of the board
     * @param action the move to make in UCI
     * @return ChessBoard that results from the move
     */
    protected ChessBoard result(ChessBoard initialBoard, String action) {
        ChessBoard clonedBoard = new ChessBoard(initialBoard);
        clonedBoard.detectCaptures(action);
        clonedBoard.movePiece(action);
        return clonedBoard;
    }

}
