package games.chess.pieces;

import games.chess.ChessBoard;
import games.chess.ChessPiece;
import games.chess.Color;

import java.util.ArrayList;

/**
 * Bishop ChessPiece
 */
public class Bishop extends ChessPiece {

    /**
     * Constructor for a Bishop
     * @param rowPosition the initial row of the bishop, 1-8
     * @param colPosition the initial column of the bishop, a-h
     * @param color WHITE or BLACK
     */
    public Bishop(int rowPosition, char colPosition, Color color) {
        this.pieceName = "bishop";
        this.pieceSymbol = color == Color.WHITE ? 'B' : 'b';
        this.rowPosition = rowPosition;
        this.colPosition = colPosition;
        this.color = color;
        this.value = 3;
        this.moved = false;
    }

    /**
     * Determines the moves that can be made by this Bishop from its current position
     * @param chessBoard the ChessBoard
     * @param currentPlayer the current player's color
     * @return list of destination tiles as UCI strings
     */
    @Override
    protected ArrayList<String> getMoves(ChessBoard chessBoard, Color currentPlayer) {
        return this.getBishopMoves(chessBoard, currentPlayer, false);
    }

    /**
     * Determines all of the tiles that are under attack by this opponent Bishop
     * @param chessBoard the ChessBoard
     * @param opponentPlayer the color of the opponent player's pieces
     * @return list of tiles that this Bishop can attack
     */
    @Override
    protected ArrayList<String> getAttackedTiles(ChessBoard chessBoard, Color opponentPlayer) {
        return this.getBishopMoves(chessBoard, opponentPlayer, true);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Bishop clone = new Bishop(rowPosition, colPosition, color);
        clone.moved = moved;
        return clone;
    }
}