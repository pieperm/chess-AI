package games.chess;

import java.util.ArrayList;

/**
 * Abstract data structure for representing a chess piece
 */
public abstract class ChessPiece {

    protected String pieceName = "UNKNOWN";
    protected char pieceSymbol;  // symbol for a piece in UCI notation
    protected Color color;  // BLACK or WHITE possible piece colors
    protected int rowPosition;  // number 1-8 for row of chess board
    protected char colPosition;  // character a-h for column of chess board
    protected int value;  // relative value of a piece (positive for white, negative for black)
    protected boolean moved;  // flag for whether the piece has been moved yet

    /**
     * Abstract method for each child piece to implement, determining the moves that can be made by that piece
     * based on its type, color, and position
     * @param chessBoard the ChessBoard
     * @param currentPlayer the color of the ChessPiece owned by the current player
     * @return list of destination tiles as UCI strings
     */
    protected abstract ArrayList<String> getMoves(ChessBoard chessBoard, Color currentPlayer);

    /**
     * Abstract method for each child piece to implement, determining all of the tiles that are under attack
     * or "controlled" by the opponent
     * @param chessBoard the ChessBoard
     * @param opponentPlayer the color of the opponent player's pieces
     * @return list of tiles that are under attack by the opponent
     */
    protected abstract ArrayList<String> getAttackedTiles(ChessBoard chessBoard, Color opponentPlayer);

    /**
     * Update the rowPosition and colPosition of the ChessPiece
     * @param row the new row of the ChessPiece, 1 through 8
     * @param col the new column of the ChessPiece, a through h
     */
    public void updatePosition(int row, char col) {
        rowPosition = row;
        colPosition = col;
    }

    /**
     * Helper method for getting tile string from row and column
     * @return string of the board tile for colPosition and rowPosition
     */
    public String getPieceTile() {
        String tile = "";
        tile += colPosition;
        tile += rowPosition;
        return tile;
    }

    /**
     * Getter for the piece's color
     * @see Color
     * @return Color.BLACK or Color.WHITE
     */
    public Color getColor() {
        return color;
    }

    /**
     * Getter for pieceName (bishop, pawn, etc)
     * @return the piece name
     */
    public String getPieceName() {
        return pieceName;
    }

    /**
     * Getter for the piece's relative value (greater magnitude is more valuable)
     * @return the piece's relative value
     */
    public int getValue() {
        return value;
    }

    public void pieceMoved() {
        moved = true;
    }

    /**
     * Find the valid moves that a rook can make
     * @param chessBoard the ChessBoard
     * @param currentPlayer the color of the current player
     * @param inclusive true if tiles occupied by the same color can be considered, false if not
     * @return list of possible destination tiles
     */
    protected ArrayList<String> getRookMoves(ChessBoard chessBoard, Color currentPlayer, boolean inclusive) {
        ArrayList<String> possibleMoves = new ArrayList<>();

        // check the up direction
        for(int row = rowPosition + 1; row <= 8; row++) {
            boolean upDirectionComplete = rookDirection(chessBoard, currentPlayer, possibleMoves, row, colPosition, inclusive);
            if(upDirectionComplete) {
                break;  // stop traversing the up direction
            }
        }

        // check the down direction
        for(int row = rowPosition - 1; row >= 1; row--) {
            boolean downDirectionComplete = rookDirection(chessBoard, currentPlayer, possibleMoves, row, colPosition, inclusive);
            if(downDirectionComplete) {
                break;  // stop traversing the down direction
            }
        }

        // check the right direction
        for(char col = (char)((int)colPosition + 1); col <= 'h'; col++) {
            boolean rightDirectionComplete = rookDirection(chessBoard, currentPlayer, possibleMoves, rowPosition, col, inclusive);
            if(rightDirectionComplete) {
                break;
            }
        }

        // check the left direction
        for(char col = (char)((int)colPosition - 1); col >= 'a'; col--) {
            boolean leftDirectionComplete = rookDirection(chessBoard, currentPlayer, possibleMoves, rowPosition, col, inclusive);
            if(leftDirectionComplete) {
                break;
            }
        }

        return possibleMoves;
    }

    /**
     * Helper method to check possible movements in a single direction until hitting a piece or the board's edge
     * @param chessBoard the ChessBoard's current state
     * @param currentPlayer the current player's color
     * @param possibleMoves reference to a list of moves to add to
     * @param row the row of the tile to check
     * @param col the column of the tile to check
     * @param inclusive true if the piece counts tiles that are occupied by its own color
     * @return true if the current direction is finished, false otherwise
     */
    private boolean rookDirection(ChessBoard chessBoard, Color currentPlayer, ArrayList<String> possibleMoves, int row, char col, boolean inclusive) {
        if(chessBoard.isEmpty(row, col)) {  // check for lines of empty tiles
            possibleMoves.add("" + col + row);
        } else {
            if(inclusive) {  // if inclusive, add tile regardless of what is there
                possibleMoves.add("" + col + row);
            } else {  // if exclusive, make sure the piece there is of the opposite color (can be captured)
                if(chessBoard.at(row, col).getColor() != currentPlayer) {
                    possibleMoves.add("" + col + row);
                }
            }
            return true;  // ran into a piece, stop checking for more tiles in this direction
        }
        return false;  // no pieces found yet, keep searching in this direction
    }

    /**
     * Find the valid moves that a bishop can make
     * @param chessBoard the ChessBoard
     * @param currentPlayer the color of the current player
     * @param inclusive true if tiles occupied by the same color can be considered, false if not
     * @return list of possible destination tiles
     */
    protected ArrayList<String> getBishopMoves(ChessBoard chessBoard, Color currentPlayer, boolean inclusive) {
        ArrayList<String> possibleMoves = new ArrayList<>();

        // check up (+1) and left (-1)
        bishopDirection(chessBoard, currentPlayer, possibleMoves, 1, -1, inclusive);

        // check up (+1) and right (+1)
        bishopDirection(chessBoard, currentPlayer, possibleMoves, 1, 1, inclusive);

        // check down (-1) and left (-1)
        bishopDirection(chessBoard, currentPlayer, possibleMoves, -1, -1, inclusive);

        // check down (-1) and right (+1)
        bishopDirection(chessBoard, currentPlayer, possibleMoves, -1, 1, inclusive);

        return possibleMoves;
    }

    /**
     * Helper method to check possible movements in a single diagonal direction until hitting a piece or the board's edge
     * @param chessBoard the ChessBoard's current state
     * @param currentPlayer the current player's color
     * @param possibleMoves reference to a list of moves to add to
     * @param rowChange +1 to increment to a higher row, -1 to decrement to a lower row
     * @param colChange +1 to increment to a higher column, -1 to decrement to a lower column
     * @param inclusive true if the piece counts tiles that are occupied by its own color
     */
    private void bishopDirection(ChessBoard chessBoard, Color currentPlayer, ArrayList<String> possibleMoves, int rowChange, int colChange, boolean inclusive) {
        boolean pieceFound = false;
        boolean onBoard = true;
        int row = rowPosition;
        char col = colPosition;

        while(onBoard && !pieceFound) {
            row += rowChange;
            col += colChange;
            onBoard = chessBoard.tileExists(row, col);
            String tile = "" + col + row;
            if(onBoard) {
                if(chessBoard.isEmpty(row, col)) {  // add empty tiles to possible moves
                    possibleMoves.add(tile);
                } else {  // space is occupied
                    pieceFound = true;
                    if(inclusive) {
                        possibleMoves.add(tile);  // include tile regardless of color
                    } else {
                        if(chessBoard.at(row, col).getColor() != currentPlayer) {
                            possibleMoves.add(tile);  // only consider tiles occupied by pieces of opposite color
                        }
                    }
                }
            }
        }
    }

    /**
     * Override method for cloning a ChessPiece, each ChessPiece implements this on its own
     * @return duplicate of the Chess Piece, with a deep copy of all member variables
     * @throws CloneNotSupportedException if the clone fails
     */
    @Override
    protected abstract Object clone() throws CloneNotSupportedException;

    /**
     * Override method for displaying a ChessPiece as a string
     * @return a string showing the piece color, type, and location (e.g. WK@e1)
     */
    @Override
    public String toString() {
        String color = this.color == Color.WHITE ? "W" : "B";
        return color + this.pieceSymbol + "@" + colPosition + rowPosition;
    }
}
