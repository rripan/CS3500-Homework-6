package cs3500.pawnsboard.Controller;

import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel;
import cs3500.pawnsboard.view.PawnsBoardGUIView;

/**
 * A stub controller for the Pawns Board game that prints actions to System.out.
 * This will be replaced by a real controller in the next part of the assignment.
 */
public class PawnsBoardStubController {
  private final ReadonlyPawnsBoardModel model;
  private final PawnsBoardGUIView view;

  private int selectedCardIndex = -1;
  private int selectedRow = -1;
  private int selectedCol = -1;

  /**
   * Constructs a new stub controller with the given model and view.
   *
   * @param model the game model
   * @param view the GUI view
   */
  public PawnsBoardStubController(ReadonlyPawnsBoardModel model, PawnsBoardGUIView view) {
    this.model = model;
    this.view = view;
  }

  /**
   * Handles a card click in the player's hand.
   *
   * @param cardIndex the index of the clicked card
   */
  public void handleCardClick(int cardIndex) {
    System.out.println("Card clicked: " + cardIndex + " (owned by " + model.getCurrentPlayer() + ")");

    // If the same card is clicked again, deselect it
    if (cardIndex == selectedCardIndex) {
      selectedCardIndex = -1;
      view.clearHighlights();
    } else {
      // Otherwise, select the new card and highlight it
      selectedCardIndex = cardIndex;
      view.highlightCard(cardIndex);
    }
  }

  /**
   * Handles a cell click on the board.
   *
   * @param row the row of the clicked cell
   * @param col the column of the clicked cell
   */
  public void handleCellClick(int row, int col) {
    System.out.println("Cell clicked: (" + row + "," + col + ")");

    // If the same cell is clicked again, deselect it
    if (row == selectedRow && col == selectedCol) {
      selectedRow = -1;
      selectedCol = -1;
      view.clearHighlights();
    } else {
      // Otherwise, select the new cell and highlight it
      selectedRow = row;
      selectedCol = col;
      view.highlightCell(row, col);
    }
  }

  /**
   * Handles a key press to confirm a move.
   */
  public void handleConfirmMove() {
    System.out.println("Confirm move key pressed.");
  }

  /**
   * Handles a key press to pass a turn.
   */
  public void handlePassTurn() {
    System.out.println("Pass turn key pressed.");
  }
}