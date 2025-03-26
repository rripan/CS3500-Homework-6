package cs3500.pawnsboard.Strategy;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.CellContent;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.Player;

import java.util.List;

/**
 * A "Control the Board" strategy for the Pawns Board game.
 * This strategy chooses a move that maximizes the player's ownership of cells.
 */
public class ControlBoardStrategy implements Strategy {

  @Override
  public Move chooseMove(ReadonlyPawnsBoardModel model, Player player) {
    if (model.getCurrentPlayer() != player) {
      throw new IllegalArgumentException("It is not " + player + "'s turn");
    }

    List<Card> hand = model.getPlayerHand(player);
    Move bestMove = null;
    int maxCellsOwned = 0;

    // Iterate through all board positions
    for (int row = 0; row < model.getRows(); row++) {
      for (int col = 0; col < model.getColumns(); col++) {
        for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
          Card card = hand.get(cardIndex);

          // Check if this move is valid
          if (isValidMove(model, player, card, row, col)) {
            int cellsOwned = simulateMove(model, player, card, row, col);

            // Choose the move that maximizes cells owned
            if (cellsOwned > maxCellsOwned ||
                (cellsOwned == maxCellsOwned && isUpperLeft(row, col, bestMove))) {
              bestMove = new Move(cardIndex, row, col);
              maxCellsOwned = cellsOwned;
            }
          }
        }
      }
    }

    return bestMove != null ? bestMove : null;
  }

  /**
   * Simulates placing a card and counts how many cells the player would own after the move.
   */
  private int simulateMove(ReadonlyPawnsBoardModel model, Player player, Card card, int row, int col) {
    int ownedCells = 0;
    int rows = model.getRows();
    int cols = model.getColumns();
    String[] influenceGrid = card.getInfluenceGrid();
    int center = influenceGrid.length / 2; // Center index of the grid

    for (int i = 0; i < influenceGrid.length; i++) {
      for (int j = 0; j < influenceGrid[i].length(); j++) {
        int targetRow = row + (i - center);
        int targetCol = col + (j - center);

        // Ensure within board bounds
        if (targetRow >= 0 && targetRow < rows && targetCol >= 0 && targetCol < cols) {
          if (influenceGrid[i].charAt(j) == 'I' || influenceGrid[i].charAt(j) == 'C') {
            // If the target cell is already owned by the player, count it
            if (model.getCellOwner(targetRow, targetCol) == player) {
              ownedCells++;
            }
          }
        }
      }
    }

    return ownedCells;
  }

  /**
   * Checks if a move is valid.
   */
  private boolean isValidMove(ReadonlyPawnsBoardModel model, Player player, Card card, int row, int col) {
    return model.getCellContent(row, col) == CellContent.PAWN &&
        model.getCellOwner(row, col) == player &&
        model.getPawnCount(row, col) >= card.getCost();
  }

  /**
   * Determines if (row, col) is upper-left compared to a previous best move.
   */
  private boolean isUpperLeft(int row, int col, Move bestMove) {
    return bestMove == null || row < bestMove.getRow() || (row == bestMove.getRow() && col < bestMove.getCol());
  }
}
