package cs3500.pawnsboard.Strategy;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.CellContent;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.Player;

import java.util.List;

/**
 * A "Fill First" strategy for the Pawns Board game.
 * This strategy chooses the first valid move it finds.
 */
public class FillFirstStrategy implements Strategy {

  @Override
  public Move chooseMove(ReadonlyPawnsBoardModel model, Player player) {
    if (model.getCurrentPlayer() != player) {
      throw new IllegalArgumentException("It is not " + player + "'s turn");
    }

    List<Card> hand = model.getPlayerHand(player);

    // Try each card in the hand
    for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
      Card card = hand.get(cardIndex);
      int cardCost = card.getCost();

      // Try each cell on the board
      for (int row = 0; row < model.getRows(); row++) {
        for (int col = 0; col < model.getColumns(); col++) {
          // Check if the cell contains pawns owned by the player
          if (model.getCellContent(row, col) == CellContent.PAWN &&
                  model.getCellOwner(row, col) == player &&
                  model.getPawnCount(row, col) >= cardCost) {

            // Found a valid move
            return new Move(cardIndex, row, col);
          }
        }
      }
    }

    // No valid move found
    return null;
  }
}