package games.chess.pieces;

import games.chess.ChessBoard;
import games.chess.ChessPiece;
import games.chess.Color;

import java.util.ArrayList;

/**
 * King ChessPiece
 */
public class King extends ChessPiece {

    /**
     * Constructor for a King
     * @param rowPosition the initial row of the king, 1-8
     * @param colPosition the initial column of the king, a-h
     * @param color WHITE or BLACK
     */
    public King(int rowPosition, char colPosition, Color color) {
        this.pieceName = "king";
        this.pieceSymbol = color == Color.WHITE ? 'K' : 'k';
        this.rowPosition = rowPosition;
        this.colPosition = colPosition;
        this.color = color;
        this.value = 0;
        this.moved = false;
    }


    /**
     * Determines the moves that can be made by this King from its current position
     * @param chessBoard the ChessBoard
     * @param currentPlayer the current player's color
     * @return list of destination tiles as UCI strings
     */
    @Override
    protected ArrayList<String> getMoves(ChessBoard chessBoard, Color currentPlayer) {
        return findKingMovements(chessBoard, currentPlayer, false);
    }

    /**
     * Determines all of the tiles that are under attack by this opponent King
     * @param chessBoard the ChessBoard
     * @param opponentPlayer the color of the opponent player's pieces
     * @return list of tiles that this King can attack
     */
    @Override
    protected ArrayList<String> getAttackedTiles(ChessBoard chessBoard, Color opponentPlayer) {
        return findKingMovements(chessBoard, opponentPlayer, true);
    }

    private ArrayList<String> findKingMovements(ChessBoard chessBoard, Color currentPlayer, boolean inclusive) {
        ArrayList<String> possibleMoves = new ArrayList<>();

        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(!(i == 0 && j == 0)) {  // king cannot stay put
                    int row = rowPosition + i;
                    char col = (char) ((int) colPosition + j);
                    if (chessBoard.tileExists(row, col)) {
                        String tile = "" + col + row;
                        if(chessBoard.at(row, col) == null) {
                            possibleMoves.add(tile);
                        } else {
                            if(inclusive) {
                                possibleMoves.add(tile);
                            } else {
                                if(chessBoard.at(row, col).getColor() != currentPlayer) {
                                    possibleMoves.add(tile);
                                }
                            }
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        King clone = new King(rowPosition, colPosition, color);
        clone.moved = moved;
        return clone;
    }
}