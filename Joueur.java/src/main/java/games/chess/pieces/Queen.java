package games.chess.pieces;

import games.chess.ChessBoard;
import games.chess.ChessPiece;
import games.chess.Color;

import java.util.ArrayList;

/**
 * Queen ChessPiece
 */
public class Queen extends ChessPiece {

    /**
     * Constructor for a Queen
     * @param rowPosition the initial row of the queen, 1-8
     * @param colPosition the initial column of the queen, a-h
     * @param color WHITE or BLACK
     */
    public Queen(int rowPosition, char colPosition, Color color) {
        this.pieceName = "queen";
        this.pieceSymbol = color == Color.WHITE ? 'Q' : 'q';
        this.rowPosition = rowPosition;
        this.colPosition = colPosition;
        this.color = color;
        this.value = 9;
        this.moved = false;
    }


    /**
     * Determines the moves that can be made by this Queen from its current position
     * @param chessBoard the ChessBoard
     * @param currentPlayer the current player's color
     * @return list of destination tiles as UCI strings
     */
    @Override
    protected ArrayList<String> getMoves(ChessBoard chessBoard, Color currentPlayer) {
        ArrayList<String> possibleMoves = new ArrayList<>();

        possibleMoves.addAll(this.getRookMoves(chessBoard, currentPlayer, false));
        possibleMoves.addAll(this.getBishopMoves(chessBoard, currentPlayer, false));

        return possibleMoves;
    }

    /**
     * Determines all of the tiles that are under attack by this opponent Queen
     * @param chessBoard the ChessBoard
     * @param opponentPlayer the color of the opponent player's pieces
     * @return list of tiles that this Queen can attack
     */
    @Override
    protected ArrayList<String> getAttackedTiles(ChessBoard chessBoard, Color opponentPlayer) {
        ArrayList<String> possibleMoves = new ArrayList<>();

        possibleMoves.addAll(this.getRookMoves(chessBoard, opponentPlayer, true));
        possibleMoves.addAll(this.getBishopMoves(chessBoard, opponentPlayer, true));

        return possibleMoves;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Queen clone = new Queen(rowPosition, colPosition, color);
        clone.moved = moved;
        return clone;
    }
}