package cs3500.pawnsboard.view;

/**
 * Interface for a graphical user interface view of the Pawns Board game.
 * This view displays the game board, player hands, scores, and allows user interaction.
 */
public interface PawnsBoardGUIView {

  /**
   * Makes the view visible.
   *
   * @param visible whether the view should be visible
   */
  void setVisible(boolean visible);

  /**
   * Refreshes the view to reflect the current state of the model.
   */
  void refresh();

  /**
   * Highlights a card in the current player's hand.
   *
   * @param cardIndex the index of the card to highlight
   */
  void highlightCard(int cardIndex);

  /**
   * Highlights a cell on the board.
   *
   * @param row the row of the cell to highlight
   * @param col the column of the cell to highlight
   */
  void highlightCell(int row, int col);

  /**
   * Clears any highlighted card or cell.
   */
  void clearHighlights();

  /**
   * Displays a message to the user.
   *
   * @param message the message to display
   */
  void displayMessage(String message);
}