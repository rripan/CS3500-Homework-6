package cs3500.pawnsboard.Strategy;

import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.Player;

/**
 * Interface for a strategy that determines moves for a computer player in the Pawns Board game.
 */
public interface Strategy {

  /**
   * Determines the next move for the specified player based on the current game state.
   * If no valid move is available, returns null to indicate that the player should pass.
   *
   * @param model the current state of the game
   * @param player the player for whom to determine the move
   * @return a Move object representing the chosen move, or null if no valid move is available
   */
  Move chooseMove(ReadonlyPawnsBoardModel model, Player player);

  /**
   * Represents a move in the Pawns Board game, consisting of a card index and a position.
   */
  class Move {
    private final int cardIndex;
    private final int row;
    private final int col;

    /**
     * Constructs a new move.
     *
     * @param cardIndex the index of the card in the player's hand
     * @param row the row to place the card
     * @param col the column to place the card
     */
    public Move(int cardIndex, int row, int col) {
      this.cardIndex = cardIndex;
      this.row = row;
      this.col = col;
    }

    /**
     * Gets the index of the card in the player's hand.
     *
     * @return the card index
     */
    public int getCardIndex() {
      return cardIndex;
    }

    /**
     * Gets the row to place the card.
     *
     * @return the row
     */
    public int getRow() {
      return row;
    }

    /**
     * Gets the column to place the card.
     *
     * @return the column
     */
    public int getCol() {
      return col;
    }

    @Override
    public String toString() {
      return "Move{cardIndex=" + cardIndex + ", row=" + row + ", col=" + col + "}";
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Move move = (Move) obj;
      return cardIndex == move.cardIndex && row == move.row && col == move.col;
    }
  }
}