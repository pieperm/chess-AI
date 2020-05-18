package games.chess;

import games.chess.pieces.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data structure to hold the state of the board (from the perspective of the current player)
 */
public class ChessBoard {

    private ChessPiece[][] board;
    private ArrayList<ChessPiece> whitePieces;
    private ArrayList<ChessPiece> blackPieces;
    private HashMap<String, Boolean> attackedTiles;
    private String whiteKingPosition;  // king positions used to quickly determine when king is in check
    private String blackKingPosition;

    /**
     * Constructor for a ChessBoard
     * Populates the board with ChessPieces according to fen
     * Fills arrays with white pieces and black pieces
     * Initializes the map of attacked tiles to all be false
     * Initializes the king positions
     * @param fen A string in Forsyth-Edwards Notation describing the board state
     */
    public ChessBoard(String fen) {
        board = new ChessPiece[8][8];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        attackedTiles = new HashMap<>();
        String boardState = fen.substring(0, fen.indexOf(' '));
        String[] rows = boardState.split("/");  // split fen into an array of rows

        int rowPosition = 8;

        // initialize the board with pieces
        for(String row : rows) {
            char colPosition = 'a';
            for(int i = 0; i < row.length(); i++) {
                char pieceSymbol = row.charAt(i);
                if(Character.isDigit(pieceSymbol)) {  // numbers in fen skip over tiles by its amount
                    int emptySpaces = Character.getNumericValue(pieceSymbol);
                    colPosition += emptySpaces - 1;
                } else {
                    ChessPiece chessPiece = getPieceFromFEN(pieceSymbol, rowPosition, colPosition);
                    this.set(rowPosition, colPosition, chessPiece);  // put the new piece at this position
                    if(chessPiece.getColor() == Color.WHITE) {
                        whitePieces.add(chessPiece);  // keep internal list of all white pieces
                        if(chessPiece instanceof King) {
                            whiteKingPosition = "" + colPosition + rowPosition;  // track the king position
                        }
                    } else if(chessPiece.getColor() == Color.BLACK) {
                        blackPieces.add(chessPiece);  // keep internal list of all black pieces
                        if(chessPiece instanceof King) {
                            blackKingPosition = "" + colPosition + rowPosition;  // track the king position
                        }
                    }
                    String tile = "" + colPosition + rowPosition;
                    attackedTiles.put(tile, false);
                    colPosition++;
                }
            }
            rowPosition--;
        }
    }

    /**
     * Copy constructor for a ChessBoard, performs a deep copy of all member variables
     * @param chessBoard the ChessBoard to copy
     */
    public ChessBoard(ChessBoard chessBoard) {

        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        // copy to new memory locations
        this.board = new ChessPiece[8][8];
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {
                if(chessBoard.at(row, col) == null) {  // empty spaces are null
                    this.board[row][col] = null;
                } else {
                    try {  // attempt to clone the ChessPiece (to avoid using the same reference)
                        this.board[row][col] = (ChessPiece) chessBoard.at(row, col).clone();
                        if(this.at(row, col).getColor() == Color.WHITE) {  // make new lists of black and white pieces
                            whitePieces.add(at(row, col));
                        } else {
                            blackPieces.add(at(row, col));
                        }
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // clone map to avoid using same reference
        attackedTiles = (HashMap<String, Boolean>) chessBoard.attackedTiles.clone();

        // copy the king positions
        whiteKingPosition = chessBoard.whiteKingPosition;
        blackKingPosition = chessBoard.blackKingPosition;
    }

    /**
     * Parse a tile string to find the row
     * @param boardTile the tile (e.g. a5)
     * @return 1 <= integer <= 8 representing the row
     */
    public int getRow(String boardTile) {
        return Character.getNumericValue(boardTile.charAt(1));
    }

    /**
     * Parse a tile string to find the column
     * @param boardTile the tile (e.g. a5)
     * @return a, b, c, d, e, f, or h, representing the column
     */
    public char getCol(String boardTile) {
        return boardTile.charAt(0);
    }

    /**
     * Find the piece located at row and column indexes
     * @param row index of the row
     * @param col index of the column
     * @return the ChessPiece at that board location
     */
    public ChessPiece at(int row, int col) {
        return board[row][col];
    }

    /**
     * Find the piece located at a given board tile
     * @param boardTile the tile
     * @return the ChessPiece at that board location
     */
    public ChessPiece at(String boardTile) {
        char col = getCol(boardTile);
        int row = getRow(boardTile);
        return at(row, col);
    }

    /**
     * Find the piece located at a given row position and column position
     * @param row the row, between 1 and 8 (inclusive)
     * @param col the column, between a and h (inclusive)
     * @return the ChessPiece at that board location
     */
    public ChessPiece at(int row, char col) {
        int boardCol = col - 'a';
        int boardRow = 8 - row;
        return board[boardRow][boardCol];
    }

    /**
     * Put a piece at a specific board location
     * @param row the row, between 1 and 8 (inclusive)
     * @param col the column, between a and h (inclusive)
     * @param chessPiece the piece to place there
     */
    public void set(int row, char col, ChessPiece chessPiece) {
        int boardCol = col - 'a';
        int boardRow = 8 - row;
        board[boardRow][boardCol] = chessPiece;
    }

    /**
     * Move a piece from its current location to a new tile
     * @param piece the ChessPiece to be moved
     * @param boardTile the destination tile where the piece will move
     * @param promotion character representing a possible pawn promotion,
     *                  null if the piece is not a pawn or the pawn is not being promoted
     */
    public void movePiece(ChessPiece piece, String boardTile, Character promotion) {
        this.set(piece.rowPosition, piece.colPosition, null);  // remove piece from its current tile

        int row = getRow(boardTile);
        char col = getCol(boardTile);
        if(piece instanceof King) {  // if either king is moved, update its position
            if(piece.getColor() == Color.WHITE) {
                whiteKingPosition = boardTile;
            } else {
                blackKingPosition = boardTile;
            }
        }
        piece.updatePosition(row, col);  // update the piece's internal tracking of its position
        this.set(row, col, piece);  // set the piece at its new tile
        piece.pieceMoved();  // flag that the piece has been moved (used for pawn movement and castling)

        if(piece instanceof Pawn) {
            if(promotion != null) {  // promote the pawn based on the promotion argument
                promoteTo(promotion, getRow(boardTile), getCol(boardTile), piece.getColor());
            }
        }
    }

    /**
     * Move a piece according the UCI string
     * @param move the string in Universal Chess Interface
     */
    public void movePiece(String move) {
        String pieceTile = move.substring(0, 2);
        String destinationTile = move.substring(2);
        Character promotion = null;
        if(destinationTile.length() == 3) {  // promotions have extra letter appended on
            promotion = destinationTile.charAt(destinationTile.length() - 1);  // determine which piece the pawn is promoted to
        }
        ChessPiece piece = at(pieceTile);
        movePiece(piece, destinationTile, promotion);  // break up data to pass to overloaded movePiece
    }

    /**
     * Flag when a tile is attacked
     * @param tile the tile to mark as under attack or not
     * @param isAttacked whether the tile is under attack by the opponent
     */
    public void tileAttacked(String tile, boolean isAttacked) {
        attackedTiles.put(tile, isAttacked);
    }

    /**
     * Check whether a board position is empty
     * @param row the row, between 1 and 8 (inclusive)
     * @param col the column, between a and h (inclusive)
     * @return true if the piece there is null, false otherwise
     */
    public boolean isEmpty(int row, char col) {
        return at(row, col) == null;
    }

    /**
     * Check whether a board position exists
     * @param row the row, between 1 and 8 (inclusive)
     * @param col the column, between a and h (inclusive)
     * @return true if 1 <= row <= 8 and 'a' <= col <= 'h'
     */
    public boolean tileExists(int row, char col) {
        return 1 <= row && row <= 8 && 'a' <= col && col <= 'h';
    }

    /**
     * Getter for the list of white chess pieces
     * @return list of white pieces
     */
    public ArrayList<ChessPiece> getWhitePieces() {
        return whitePieces;
    }

    /**
     * Getter for the list of black chess pieces
     * @return list of black pieces
     */
    public ArrayList<ChessPiece> getBlackPieces() {
        return blackPieces;
    }

    /**
     * Getter for the white king's tile position
     * @return the tile where the white king is located in UCI
     */
    public String getWhiteKingPosition() {
        return whiteKingPosition;
    }

    /**
     * Getter for the black king's tile position
     * @return the tile where the black king is located in UCI
     */
    public String getBlackKingPosition() {
        return blackKingPosition;
    }

    /**
     * Update whitePieces when a white piece has been captured
     * @param tile the tile where the white piece is captured
     */
    public void captureWhitePiece(String tile) {
        whitePieces.remove(at(tile));
    }

    /**
     * Update blackPieces when a black piece has been captured
     * @param tile the tile where the black piece is captured
     */
    public void captureBlackPiece(String tile) {
        blackPieces.remove(at(tile));
    }

    /**
     * Getter for the map of attacked tiles
     * @return HashMap of tile (String) -> whether the tile is attacked (Boolean)
     */
    public HashMap<String, Boolean> getAttackedTiles() {
        return attackedTiles;
    }

    /**
     * Generate a new ChessPiece based on the input symbol from fen string
     * @param uciSymbol the symbol representing the piece and its color
     * @param row the initial row of the piece
     * @param col the initial column of the piece
     * @return the newly created ChessPiece
     */
    public ChessPiece getPieceFromFEN(char uciSymbol, int row, char col) {
        ChessPiece chessPiece;

        // capital letters indicate white pieces, lowercase letters indicate black pieces
        switch(uciSymbol) {
            case 'b':
                chessPiece = new Bishop(row, col, Color.BLACK);
                break;
            case 'B':
                chessPiece = new Bishop(row, col, Color.WHITE);
                break;
            case 'k':
                chessPiece = new King(row, col, Color.BLACK);
                break;
            case 'K':
                chessPiece = new King(row, col, Color.WHITE);
                break;
            case 'n':
                chessPiece = new Knight(row, col, Color.BLACK);
                break;
            case 'N':
                chessPiece = new Knight(row, col, Color.WHITE);
                break;
            case 'p':
                chessPiece = new Pawn(row, col, Color.BLACK);
                break;
            case 'P':
                chessPiece = new Pawn(row, col, Color.WHITE);
                break;
            case 'q':
                chessPiece = new Queen(row, col, Color.BLACK);
                break;
            case 'Q':
                chessPiece = new Queen(row, col, Color.WHITE);
                break;
            case 'r':
                chessPiece = new Rook(row, col, Color.BLACK);
                break;
            case 'R':
                chessPiece = new Rook(row, col, Color.WHITE);
                break;
            default:
                throw new Error("INVALID SYMBOL: " + uciSymbol);
        }

        return chessPiece;
    }

    /**
     * Print the ChessBoard with labels for rows and columns, and . where no piece is
     */
    public void print() {
        System.out.println("___a_b_c_d_e_f_g_h");
        for(int row = 8; row >= 1; row--) {
            System.out.print(row + "|");
            for(char col = 'a'; col <= 'h'; col++) {
                System.out.print(" ");
                if(isEmpty(row, col)) {
                    System.out.print(".");
                } else {
                    System.out.print(this.at(row, col).pieceSymbol);
                }
            }
            System.out.print("\n");
        }
    }

    /**
     * Update which tiles are under attack
     * @param currentPlayer the color of the current player
     */
    public void updateAttackedTiles(Color currentPlayer) {

        for(int row = 8; row >= 1; row--) {  // reset all to false
            for(char col = 'a'; col <= 'h'; col++) {
                tileAttacked("" + col + row, false);
            }
        }

        if(currentPlayer == Color.WHITE) {  // find attacked tiles for each color
            for(ChessPiece blackPiece : getBlackPieces()) {
                findAttackedTiles(blackPiece, Color.BLACK);
            }
        } else {
            for(ChessPiece whitePiece: getWhitePieces()) {
                findAttackedTiles(whitePiece, Color.WHITE);
            }
        }
    }

    /**
     * Mark the tiles that are under attack from the opponent's pieces
     * @param chessPiece the opponent's chess piece
     * @param opponentColor the opponent's chess piece color
     */
    private void findAttackedTiles(ChessPiece chessPiece, Color opponentColor) {
        ArrayList<String> possibleAttacks = chessPiece.getAttackedTiles(this, opponentColor);
        for(String move : possibleAttacks) {
            tileAttacked(move, true);  // add all possible piece attacks
        }
    }

    /**
     * Find all possible moves for the current player
     * @param currentPlayer the color of the current player
     * @return list of all moves in UCI notation
     */
    public ArrayList<String> findAllMoves(Color currentPlayer) {
        ArrayList<String> possibleMoves = new ArrayList<>();
        ArrayList<ChessPiece> pieces = currentPlayer == Color.WHITE ? whitePieces : blackPieces;  // determine which list of pieces to use

        for(ChessPiece piece : pieces) {
            ArrayList<String> moves = piece.getMoves(this, currentPlayer);  // get the moves for that piece

            for(String move : moves) {
                String fullMove = piece.getPieceTile() + move;

                if(piece instanceof Pawn && checkPawnPromotion(move, piece.getColor())) {  // determine if pawn should be promoted
                    for(char suffix : new char[] {'q', 'n', 'r', 'b'}) {  // try each of the choices for promoting
                        boolean putsKingInCheck = testMove(fullMove, currentPlayer);
                        if(!putsKingInCheck) {  // ensure that moving this piece does not put the king in check
                            possibleMoves.add(fullMove + suffix);  // append the promotion to the UCI string
                        }
                    }
                } else {
                    boolean putsKingInCheck = testMove(fullMove, currentPlayer);
                    if (!putsKingInCheck) {  // ensure that moving this piece does not put the king in check
                        possibleMoves.add(fullMove);
                    }
                }
            }
        }

        ArrayList<String> castleMoves = findCastleMoves(currentPlayer);
        possibleMoves.addAll(castleMoves);  // add possible castling moves, if available

        return possibleMoves;
    }

    /**
     * Find the possible moves for the current player to castle
     * @param currentPlayer the current player's color
     * @return a list of 0, 1 or 2 castling moves in UCI notation
     */
    public ArrayList<String> findCastleMoves(Color currentPlayer) {
        ArrayList<String> castleMoves = new ArrayList<>();
        String kingPosition = currentPlayer == Color.WHITE ? whiteKingPosition : blackKingPosition;

        boolean kingUnmoved = this.at(kingPosition) != null && !this.at(kingPosition).moved;  // whether king has moved yet
        boolean kingInCheck = this.attackedTiles.get(kingPosition);  // whether king is in check

        // castling requires that the king has not moved and the king is not in check
        if(kingUnmoved && !kingInCheck) {
            int kingRow = getRow(kingPosition);
            char kingCol = getCol(kingPosition);

            // check for castling to the right of the king
            for(char col = kingCol; col <= 'h'; col++) {
                if(!isEmpty(kingRow, col)) {  // check if space is occupied
                    ChessPiece piece = at(kingRow, col);
                    if(piece instanceof Rook && !piece.moved && piece.color == currentPlayer) {  // if piece is an unmoved rook
                        if(!attackedTiles.get("" + col + kingRow)) {  // no intermediate tiles can be under attack
                            castleMoves.add("" + kingPosition + "g" + kingRow);  // castling kingside is valid
                        } else {
                            break;  // castling is blocked by an attacked tile
                        }
                    } else {
                        break;  // castling is blocked by a piece on right side
                    }
                }
            }

            // check for castling to the left of the king
            for(char col = kingCol; col >= 'a'; col--) {
                if(!isEmpty(kingRow, col)) {  // check if space is occupied
                    ChessPiece piece = at(kingRow, col);
                    if(piece instanceof Rook && !piece.moved && piece.color == currentPlayer) {  // if piece is an unmoved rook
                        if(!attackedTiles.get("" + col + kingRow)) {  // no intermediate tiles can be under attack
                            castleMoves.add("" + kingPosition + "c" + kingRow);  // castling queenside is valid
                        } else {
                            break;  // castling is blocked by an attacked tile
                        }
                    } else {
                        break;  // castling is blocked by a piece on left side
                    }
                }
            }
        }

        return castleMoves;
    }

    /**
     * Update internal lists of pieces when a piece is captured
     * @param move the move that is being made in UCI notation
     */
    public void detectCaptures(String move) {
        String destinationTile = move.substring(2);
        ChessPiece piece = at(destinationTile);
        if(piece != null) {
            if(piece.getColor() == Color.WHITE) {
                captureWhitePiece(destinationTile);
            } else {
                captureBlackPiece(destinationTile);
            }
        }
    }

    /**
     * Simulate the effects of moving a piece to a space
     * @param move the move from one tile to another in UCI notation
     * @param currentPlayer the current player's color
     * @return true if the movement would put the king in check, false otherwise
     */
    public boolean testMove(String move, Color currentPlayer) {

        ChessBoard testBoard = new ChessBoard(this);  // copy the board to test out the specified move

        testBoard.detectCaptures(move);
        testBoard.movePiece(move);
        testBoard.updateAttackedTiles(currentPlayer);

        String kingPosition = currentPlayer == Color.WHITE ? testBoard.whiteKingPosition : testBoard.blackKingPosition;  // look up the current player's king
        boolean putsInCheck = testBoard.attackedTiles.get(kingPosition);  // see if the king's position is under attack

        return putsInCheck;
    }

    /**
     * Checks whether moving a pawn to a location will cause it to be promoted
     * @param pawnDestination the tile the pawn is being moved to (UCI notation)
     * @param pawnColor the color of the pawn
     * @return true if a white pawn is in the 8th rank or a black pawn is in the 1st rank, false otherwise
     */
    public boolean checkPawnPromotion(String pawnDestination, Color pawnColor) {
        if(pawnColor == Color.WHITE) {
            return getRow(pawnDestination) == 8;
        } else {
            return getRow(pawnDestination) == 1;
        }
    }

    /**
     * Promote a pawn to random choice of queen, knight, bishop, or rook
     * @param row the row of the pawn
     * @param col the column of the pawn
     * @param pieceColor the piece color to be promoted to
     */
    public void promoteTo(Character pieceType, int row, char col, Color pieceColor) {
        ChessPiece newPiece = null;
        switch(pieceType) {
            case 'q':
                newPiece = new Queen(row, col, pieceColor);
                break;
            case 'n':
                newPiece = new Knight(row, col, pieceColor);
                break;
            case 'r':
                newPiece = new Rook(row, col, pieceColor);
                break;
            case 'b':
                newPiece = new Bishop(row, col, pieceColor);
                break;
        }

        if(pieceColor == Color.WHITE) {
            for(int i = 0; i < whitePieces.size(); i++) {
                ChessPiece whitePiece = whitePieces.get(i);
                if(whitePiece instanceof Pawn && whitePiece.rowPosition == row && whitePiece.colPosition == col) {
                    // remove the pawn from the list of white pieces
                    whitePieces.remove(whitePiece);
                }
            }
            whitePieces.add(newPiece);  // replace the removed pawn with the promoted piece
        } else {
            for(int i = 0; i < blackPieces.size(); i++) {
                ChessPiece blackPiece = blackPieces.get(i);
                if(blackPiece instanceof Pawn && blackPiece.rowPosition == row && blackPiece.colPosition == col) {
                    // remove the pawn from the list of black pieces
                    blackPieces.remove(blackPiece);
                }
            }
            blackPieces.add(newPiece);  // replace the removed pawn with the promoted piece
        }

        set(row, col, newPiece);  // update the board with the promoted piece
    }

    /**
     * Overridden method to convert a ChessBoard into a String
     * @return string representing piece color, positions, and empty tiles
     */
    @Override
    public String toString() {
        StringBuilder board = new StringBuilder();
        for(int row = 8; row >= 1; row--) {
            for(char col = 'a'; col <= 'h'; col++) {
                if(isEmpty(row, col)) {
                    board.append(" ");  // spaces represent empty tiles
                } else {
                    char pieceSymbol = this.at(row, col).pieceSymbol;
                    board.append(pieceSymbol);
                }
            }
            board.append("/");  // separator between rows
        }
        return board.toString();
    }
}