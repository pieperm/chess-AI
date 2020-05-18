package games.chess.pieces;

import games.chess.ChessBoard;
import games.chess.ChessPiece;
import games.chess.Color;

import java.util.ArrayList;

/**
 * Knight ChessPiece
 */
public class Knight extends ChessPiece {

    /**
     * Constructor for a Knight
     * @param rowPosition the initial row of the knight, 1-8
     * @param colPosition the initial column of the knight, a-h
     * @param color WHITE or BLACK
     */
    public Knight(int rowPosition, char colPosition, Color color) {
        this.pieceName = "knight";
        this.pieceSymbol = color == Color.WHITE ? 'N' : 'n';
        this.rowPosition = rowPosition;
        this.colPosition = colPosition;
        this.color = color;
        this.value = 3;
    }

    /**
     * Determines the moves that can be made by this Knight from its current position
     * @param chessBoard the ChessBoard
     * @param currentPlayer the current player's color
     * @return list of destination tiles as UCI strings
     */
    @Override
    protected ArrayList<String> getMoves(ChessBoard chessBoard, Color currentPlayer) {
        return findKnightMovements(chessBoard, currentPlayer, false);
    }

    /**
     * Determines all of the tiles that are under attack by this opponent Knight
     * @param chessBoard the ChessBoard
     * @param opponentPlayer the color of the opponent player's pieces
     * @return list of tiles that this Knight can attack
     */
    @Override
    protected ArrayList<String> getAttackedTiles(ChessBoard chessBoard, Color opponentPlayer) {
        return findKnightMovements(chessBoard, opponentPlayer, true);
    }

    /**
     *
     * @param chessBoard the ChessBoard
     * @param currentPlayer the current player's color
     * @param inclusive true if the piece counts tiles that are occupied by its own color
     * @return list of possible knight movements
     */
    private ArrayList<String> findKnightMovements(ChessBoard chessBoard, Color currentPlayer, boolean inclusive) {
        ArrayList<String> possibleMoves = new ArrayList<>();

        // check each possible L-shaped movement
        knightMovement(chessBoard, currentPlayer, possibleMoves, 1, -2, inclusive);
        knightMovement(chessBoard, currentPlayer, possibleMoves, 2, -1, inclusive);
        knightMovement(chessBoard, currentPlayer, possibleMoves, 2, 1, inclusive);
        knightMovement(chessBoard, currentPlayer, possibleMoves, 1, 2, inclusive);
        knightMovement(chessBoard, currentPlayer, possibleMoves, -1, 2, inclusive);
        knightMovement(chessBoard, currentPlayer, possibleMoves, -2, 1, inclusive);
        knightMovement(chessBoard, currentPlayer, possibleMoves, -2, -1, inclusive);
        knightMovement(chessBoard, currentPlayer, possibleMoves, -1, -2, inclusive);

        return possibleMoves;
    }

    /**
     * Helper method to check an L-shaped movement of a knight
     * @param chessBoard the ChessBoard
     * @param currentPlayer the current player's color
     * @param possibleMoves reference to a list of moves to add to
     * @param rowChange -2, -1, +1, or +2 depending on the L-shape (rowChange should not equal colChange)
     * @param colChange -2, -1, +1, or +2 depending on the L-shape (rowChange should not equal colChange)
     * @param inclusive true if the piece counts tiles that are occupied by its own color
     */
    private void knightMovement(ChessBoard chessBoard, Color currentPlayer, ArrayList<String> possibleMoves, int rowChange, int colChange, boolean inclusive) {
        int row = rowPosition + rowChange;
        char col = (char)((int) colPosition + colChange);
        if(chessBoard.tileExists(row, col)) {
            if(inclusive) {  // add tile regardless of color
                possibleMoves.add("" + col + row);
            } else {  // ensure that knight is not trying to jump on top of its own color
                if(chessBoard.isEmpty(row, col) || chessBoard.at(row, col).getColor() != currentPlayer) {
                    possibleMoves.add("" + col + row);
                }
            }
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Knight clone = new Knight(rowPosition, colPosition, color);
        clone.moved = moved;
        return clone;
    }
}