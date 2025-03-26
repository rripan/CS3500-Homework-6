package cs3500.pawnsboard.model;

import java.util.List;

/**
 * Represents a read-only view of the Pawns Board game model. This interface provides methods
 * to query information about the game state without modifying it.
 */
public interface ReadonlyPawnsBoardModel {
  /**
   * Enum representing the two players in the game.
   */
  enum Player {
    RED, BLUE;

    /**
     * Get the opponent of this player.
     * @return the opponent player
     */
    public Player opponent() {
      return this == RED ? BLUE : RED;
    }
  }

  /**
   * Enum representing the possible cell contents on the board.
   */
  enum CellContent {
    EMPTY, PAWN, CARD
  }

  /**
   * Get the current player whose turn it is.
   * @return the current player (RED or BLUE)
   */
  Player getCurrentPlayer();

  /**
   * Get the cards in the specified player's hand.
   *
   * @param player the player whose hand to get
   * @return a list of cards in the player's hand
   */
  List<Card> getPlayerHand(Player player);

  /**
   * Get the number of rows on the board.
   * @return the number of rows
   */
  int getRows();

  /**
   * Get the number of columns on the board.
   * @return the number of columns
   */
  int getColumns();

  /**
   * Get the content type of cell on the board.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return the content type of the cell (EMPTY, PAWN, or CARD)
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  CellContent getCellContent(int row, int col);

  /**
   * Get the owner of a cell on the board.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return the owner of the cell, or null if the cell is empty
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  Player getCellOwner(int row, int col);

  /**
   * Get the number of pawns on a cell.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return the number of pawns (0-3), or 0 if the cell does not contain pawns
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  int getPawnCount(int row, int col);

  /**
   * Get the card on a cell.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return the card on the cell, or null if the cell does not contain a card
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  Card getCard(int row, int col);

  /**
   * Get the row score for the specified player and row.
   *
   * @param player the player
   * @param row the row
   * @return the row score for the player
   * @throws IllegalArgumentException if the row is invalid
   */
  int getRowScore(Player player, int row);

  /**
   * Get the total score for the specified player.
   *
   * @param player the player
   * @return the total score for the player
   */
  int getTotalScore(Player player);

  /**
   * Check if the game is over.
   * The game is over when both players pass consecutively.
   *
   * @return true if the game is over, false otherwise
   */
  boolean isGameOver();

  /**
   * Get the winner of the game.
   *
   * @return the winning player, or null if the game is tied or not over
   * @throws IllegalStateException if the game is not over
   */
  Player getWinner();

  /**
   * Check if the specified player has passed their last turn.
   *
   * @param player the player
   * @return true if the player passed their last turn, false otherwise
   */
  boolean hasPlayerPassed(Player player);

  /**
   * Checks if it is legal for the current player to play a card at the given position.
   *
   * @param cardIndex the index of the card in the player's hand
   * @param row the row to place the card
   * @param col the column to place the card
   * @return true if the move is legal, false otherwise
   */
  boolean isLegalMove(int cardIndex, int row, int col);
}