package cs3500.pawnsboard.Strategy;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.CellContent;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.Player;

import java.util.List;

/**
 * A "Minimax" strategy for the Pawns Board game.
 * This strategy selects a move that minimizes the best move an opponent can make.
 */
public class MinMaxStrategy implements Strategy {

  @Override
  public Move chooseMove(ReadonlyPawnsBoardModel model, Player player) {
    if (model.getCurrentPlayer() != player) {
      throw new IllegalArgumentException("It is not " + player + "'s turn");
    }

    List<Card> hand = model.getPlayerHand(player);
    Player opponent = player.opponent();
    Move bestMove = null;
    int worstOpponentMove = Integer.MAX_VALUE;

    for (int row = 0; row < model.getRows(); row++) {
      for (int col = 0; col < model.getColumns(); col++) {
        for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
          Card card = hand.get(cardIndex);

          // Simulate checking if move is valid
          if (isValidMove(model, player, card, row, col)) {
            // Simulate the move and get new board state
            int[][] simulatedScores = simulateMove(model, player, card, row, col);

            // Evaluate opponent's best move
            int opponentBestMove = evaluateOpponentBestMove(simulatedScores, opponent);

            // Choose the move that minimizes the opponent’s best move
            if (opponentBestMove < worstOpponentMove) {
              worstOpponentMove = opponentBestMove;
              bestMove = new Move(cardIndex, row, col);
            }
          }
        }
      }
    }

    return bestMove != null ? bestMove : null;
  }

  /**
   * Simulates placing a card on the board and returns an updated row score array.
   * This avoids modifying ReadonlyPawnsBoardModel.
   */
  private int[][] simulateMove(ReadonlyPawnsBoardModel model, Player player, Card card, int row, int col) {
    int[][] simulatedScores = new int[model.getRows()][2];

    // Copy existing row scores
    for (int r = 0; r < model.getRows(); r++) {
      simulatedScores[r][0] = model.getRowScore(Player.RED, r);
      simulatedScores[r][1] = model.getRowScore(Player.BLUE, r);
    }

    // Apply card effect (simulate its impact on row scores)
    simulatedScores[row][player == Player.RED ? 0 : 1] += card.getValueScore();

    return simulatedScores;
  }

  /**
   * Simulates how well the opponent can perform in their best move.
   */
  private int evaluateOpponentBestMove(int[][] simulatedScores, Player opponent) {
    int bestScore = Integer.MIN_VALUE;

    for (int row = 0; row < simulatedScores.length; row++) {
      int opponentScore = simulatedScores[row][opponent == Player.RED ? 0 : 1];
      if (opponentScore > bestScore) {
        bestScore = opponentScore;
      }
    }
    return bestScore;
  }

  /**
   * Checks if a move is valid based on available pawns.
   */
  private boolean isValidMove(ReadonlyPawnsBoardModel model, Player player, Card card, int row, int col) {
    return model.getCellContent(row, col) == CellContent.PAWN &&
        model.getCellOwner(row, col) == player &&
        model.getPawnCount(row, col) >= card.getCost();
  }
}
