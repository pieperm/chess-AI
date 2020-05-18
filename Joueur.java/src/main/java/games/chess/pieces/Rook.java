package games.chess.pieces;

import games.chess.ChessBoard;
import games.chess.ChessPiece;
import games.chess.Color;

import java.util.ArrayList;

/**
 * Rook ChessPiece
 */
public class Rook extends ChessPiece {

    /**
     * Constructor for a Rook
     * @param rowPosition the initial row of the rook, 1-8
     * @param colPosition the initial column of the rook, a-h
     * @param color WHITE or BLACK
     */
    public Rook(int rowPosition, char colPosition, Color color) {
        this.pieceName = "rook";
        this.pieceSymbol = color == Color.WHITE ? 'R' : 'r';
        this.rowPosition = rowPosition;
        this.colPosition = colPosition;
        this.color = color;
        this.value = 5;
        this.moved = false;
    }

    /**
     * Determines the moves that can be made by this Rook from its current position
     * @param chessBoard the ChessBoard
     * @param currentPlayer the current player's color
     * @return list of destination tiles as UCI strings
     */
    @Override
    protected ArrayList<String> getMoves(ChessBoard chessBoard, Color currentPlayer) {
        return this.getRookMoves(chessBoard, currentPlayer, false);
    }

    /**
     * Determines all of the tiles that are under attack by this opponent Rook
     * @param chessBoard the ChessBoard
     * @param opponentPlayer the color of the opponent player's pieces
     * @return list of tiles that this Rook can attack
     */
    @Override
    protected ArrayList<String> getAttackedTiles(ChessBoard chessBoard, Color opponentPlayer) {
        return this.getRookMoves(chessBoard, opponentPlayer, true);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Rook clone = new Rook(rowPosition, colPosition, color);
        clone.moved = moved;
        return clone;
    }
}