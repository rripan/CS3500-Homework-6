package cs3500.pawnsboard.Strategy;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.CellContent;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.Player;

import java.util.List;

/**
 * A "Maximize Row-Score" strategy for the Pawns Board game.
 * This strategy tries to win rows by making the player's row score higher
 * than the opponent's, starting from the top row.
 */
public class MaximizeRowScoreStrategy implements Strategy {

  @Override
  public Move chooseMove(ReadonlyPawnsBoardModel model, Player player) {
    if (model.getCurrentPlayer() != player) {
      throw new IllegalArgumentException("It is not " + player + "'s turn");
    }

    List<Card> hand = model.getPlayerHand(player);
    Player opponent = player.opponent();

    // Try each row from top to bottom
    for (int row = 0; row < model.getRows(); row++) {
      int playerRowScore = model.getRowScore(player, row);
      int opponentRowScore = model.getRowScore(opponent, row);

      // If the player's score is lower or equal to the opponent's,
      // try to find a move that increases it
      if (playerRowScore <= opponentRowScore) {
        Move bestMove = null;
        int bestScoreImprovement = 0;

        // Try each card in the hand
        for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
          Card card = hand.get(cardIndex);
          int cardCost = card.getCost();
          int cardValue = card.getValueScore();

          // Try each column in the row
          for (int col = 0; col < model.getColumns(); col++) {
            // Check if the cell contains pawns owned by the player
            if (model.getCellContent(row, col) == CellContent.PAWN &&
                    model.getCellOwner(row, col) == player &&
                    model.getPawnCount(row, col) >= cardCost) {

              // Calculate the new row score if this card is played
              int newRowScore = playerRowScore + cardValue;

              // If this improves the score enough to beat the opponent
              if (newRowScore > opponentRowScore &&
                      (bestMove == null || newRowScore - playerRowScore > bestScoreImprovement)) {
                bestMove = new Move(cardIndex, row, col);
                bestScoreImprovement = newRowScore - playerRowScore;
              }
            }
          }
        }

        // If we found a good move for this row, return it
        if (bestMove != null) {
          return bestMove;
        }
      }
    }

    // No good move found in any row, try any legal move
    for (int row = 0; row < model.getRows(); row++) {
      for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
        Card card = hand.get(cardIndex);
        int cardCost = card.getCost();

        for (int col = 0; col < model.getColumns(); col++) {
          if (model.getCellContent(row, col) == CellContent.PAWN &&
                  model.getCellOwner(row, col) == player &&
                  model.getPawnCount(row, col) >= cardCost) {

            return new Move(cardIndex, row, col);
          }
        }
      }
    }

    // No valid move found
    return null;
  }
}