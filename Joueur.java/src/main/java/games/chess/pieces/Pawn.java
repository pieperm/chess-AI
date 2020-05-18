package games.chess.pieces;

import games.chess.ChessBoard;
import games.chess.ChessPiece;
import games.chess.Color;

import java.util.ArrayList;

/**
 * Pawn ChessPiece
 */
public class Pawn extends ChessPiece {

    /**
     * Constructor for a Pawn
     * @param rowPosition the initial row of the pawn, 1-8
     * @param colPosition the initial column of the pawn, a-h
     * @param color WHITE or BLACK
     */
    public Pawn(int rowPosition, char colPosition, Color color) {
        this.pieceName = "pawn";
        this.pieceSymbol = color == Color.WHITE ? 'P' : 'p';
        this.rowPosition = rowPosition;
        this.colPosition = colPosition;
        this.color = color;
        this.moved = false;
        this.value = 1;
        this.moved = false;
    }

    /**
     * Determines the moves that can be made by this Pawn from its current position
     * @param chessBoard the ChessBoard
     * @param currentPlayer the current player's color
     * @return list of destination tiles as UCI strings
     */
    @Override
    protected ArrayList<String> getMoves(ChessBoard chessBoard, Color currentPlayer) {

        ArrayList<String> possibleMoves = new ArrayList<>();

        if(currentPlayer == Color.WHITE) {
            possibleMoves = findMovements(chessBoard, currentPlayer, 1);
        } else if(currentPlayer == Color.BLACK) {
            possibleMoves = findMovements(chessBoard, currentPlayer, -1);
        }

        return possibleMoves;
    }

    /**
     * Determines all of the tiles that are under attack by this opponent Pawn
     * @param chessBoard the ChessBoard
     * @param opponentPlayer the color of the opponent player's pieces
     * @return list of tiles that this Pawn can attack
     */
    @Override
    protected ArrayList<String> getAttackedTiles(ChessBoard chessBoard, Color opponentPlayer) {
        ArrayList<String> movements = new ArrayList<>();

        int direction = opponentPlayer == Color.WHITE ? 1 : -1;
        int row = rowPosition + direction;  // up 1 for white, down 1 for black
        char left = (char)((int)colPosition - 1);
        char right = (char)((int)colPosition + 1);

        // pawns can only attack adjacent diagonal tiles
        if(chessBoard.tileExists(row, left)) {
            movements.add("" + left + row);
        }
        if(chessBoard.tileExists(row, right)) {
            movements.add("" + right + row);
        }

        return movements;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Pawn clone = new Pawn(rowPosition, colPosition, color);
        clone.moved = moved;
        return clone;
    }

    /**
     * Helper method to find the possible movements for a pawn
     * @param chessBoard the ChessBoard
     * @param currentPlayer the current player's color
     * @param direction +1 for white movements (up), -1 for black movements (down)
     * @return list of possible moves (including captures)
     */
    private ArrayList<String> findMovements(ChessBoard chessBoard, Color currentPlayer, int direction) {
        ArrayList<String> movements = new ArrayList<>();

        int row = rowPosition + direction;  // up 1 for white, down 1 for black
        char left = (char)((int)colPosition - 1);
        char right = (char)((int)colPosition + 1);

        // check adjacent diagonal tiles for captures
        if(chessBoard.tileExists(row, left) && chessBoard.at(row, left) != null && chessBoard.at(row, left).getColor() != currentPlayer) {
            movements.add("" + left + row);
        }
        if(chessBoard.tileExists(row, right) && chessBoard.at(row, right) != null && chessBoard.at(row, right).getColor() != currentPlayer) {
            movements.add("" + right + row);
        }

        if(chessBoard.tileExists(row, colPosition)) {
            ChessPiece pieceAhead = chessBoard.at(row, colPosition);
            if(pieceAhead == null) {
                movements.add("" + colPosition + row);
                if(!moved) {  // can potentially move 2 spaces forward, if first space is clear
                    row = rowPosition + (2 * direction);  // up 2 for white, down 2 for black
                    if(chessBoard.tileExists(row, colPosition)) {
                        ChessPiece piece2Ahead = chessBoard.at(row, colPosition);
                        if(piece2Ahead == null) {
                            movements.add("" + colPosition + row);
                        }
                    }
                }
            }
        }

        return movements;
    }
}