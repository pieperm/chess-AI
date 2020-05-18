/**
 * This is where you build your AI for the Chess game.
 */
package games.chess;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import joueur.BaseAI;

// <<-- Creer-Merge: imports -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
// you can add additional import(s) here
// <<-- /Creer-Merge: imports -->>

/**
 * This is where you build your AI for the Chess game.
 */
public class AI extends BaseAI {
    /**
     * This is the Game object itself, it contains all the information about the current game
     */
    public Game game;

    /**
     * This is your AI's player. This AI class is not a player, but it should command this Player.
     */
    public Player player;

    // <<-- Creer-Merge: fields -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
    // you can add additional fields here for your AI to use
    public ChessBoard chessBoard;
    public HistoryTable historyTable;
    public TranspositionTable transpositionTable;
    // <<-- /Creer-Merge: fields -->>


    /**
     * This returns your AI's name to the game server. Just replace the string.
     * @return string of you AI's name
     */
    public String getName() {
        // <<-- Creer-Merge: get-name -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
        return "mepww2"; // REPLACE THIS WITH YOUR TEAM NAME!
        // <<-- /Creer-Merge: get-name -->>
    }

    /**
     * This is automatically called when the game first starts, once the Game object and all GameObjects have been initialized, but before any players do anything.
     * This is a good place to initialize any variables you add to your AI, or start tracking game objects.
     */
    public void start() {
        // <<-- Creer-Merge: start -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
        super.start();
        chessBoard = new ChessBoard(game.fen);
        historyTable = new HistoryTable();
        transpositionTable = new TranspositionTable();
        // <<-- /Creer-Merge: start -->>
    }

    /**
     * This is automatically called every time the game (or anything in it) updates.
     * If a function you call triggers an update this will be called before that function returns.
     */
    public void gameUpdated() {
        // <<-- Creer-Merge: game-updated -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
        super.gameUpdated();
        // <<-- /Creer-Merge: game-updated -->>
    }

    /**
     * This is automatically called when the game ends.
     * You can do any cleanup of you AI here, or do custom logging. After this function returns the application will close.
     * @param  won  true if your player won, false otherwise
     * @param  reason  a string explaining why you won or lost
     */
    public void ended(boolean won, String reason) {
        // <<-- Creer-Merge: ended -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
        super.ended(won, reason);
        // <<-- /Creer-Merge: ended -->>
    }


    /**
     * This is called every time it is this AI.player's turn to make a move.
     *
     * @return A string in Universal Chess Inferface (UCI) or Standard Algebraic Notation (SAN) formatting for the move you want to make. If the move is invalid or not properly formatted you will lose the game.
     */
    public String makeMove() {
        // <<-- Creer-Merge: makeMove -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
        game.print();
        // Put your game logic here for makeMove
        Color currentPlayer = player.color.equals("white") ? Color.WHITE : Color.BLACK;

        if(!game.history.isEmpty()) {  // update board state from opponent's move
            String latestMove = game.history.get(game.history.size() - 1);  // get opponent's last move
            String originalTile = latestMove.substring(0, 2);
            ChessPiece opponentPiece = chessBoard.at(originalTile);
            if(opponentPiece != null) {
                chessBoard.detectCaptures(latestMove);
                chessBoard.movePiece(latestMove);
            }
        }

        // mark tiles that are under attack by the opponent
        chessBoard.updateAttackedTiles(currentPlayer);

        // use minimax with alpha-beta pruning, quiescent search, and history table to determine the best move
        ChessSolver chessSolver = new QuiescentSolver(chessBoard, currentPlayer, player.timeRemaining, historyTable, transpositionTable);
        String chosenMove = chessSolver.computeBestMove();
        System.out.println(currentPlayer + "'s move: " + chosenMove + "\n");  // print the move

        // update the internal board state
        chessBoard.detectCaptures(chosenMove);
        chessBoard.movePiece(chosenMove);

        return chosenMove;
        // <<-- /Creer-Merge: makeMove -->>
    }


    // <<-- Creer-Merge: methods -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
    // you can add additional methods here for your AI to call
    // <<-- /Creer-Merge: methods -->>
}
