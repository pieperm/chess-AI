package games.chess;

import java.util.ArrayList;
import java.util.Random;

/**
 * ChessSolver that randomly selects a valid move
 */
public class RandomSolver extends ChessSolver {

    /**
     * Constructor for RandomSolver
     * @param chessBoard current state of the ChessBoard
     * @param currentPlayer the current player's color
     */
    public RandomSolver(ChessBoard chessBoard, Color currentPlayer) {
        super(chessBoard, currentPlayer);
    }

    @Override
    public String computeBestMove() {
        Random rand = new Random();
        ArrayList<String> allPossibleMoves = chessBoard.findAllMoves(currentPlayer);
        if(allPossibleMoves.isEmpty()) {
            System.out.println(chessBoard.getAttackedTiles());
        }
        String randomMove = allPossibleMoves.get(rand.nextInt(allPossibleMoves.size()));  // choose a random move
        return randomMove;
    }
}
