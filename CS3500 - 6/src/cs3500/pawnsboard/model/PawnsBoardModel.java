package cs3500.pawnsboard.model;

import java.util.List;

/**
 * Represents the model for the Pawns Board game. This interface provides methods to
 * interact with the game state, make moves, and query information about the game.
 */
public interface PawnsBoardModel extends ReadonlyPawnsBoardModel {

  /**
   * Initialize the game with the specified parameters.
   *
   * @param rows the number of rows in the board (must be > 0)
   * @param cols the number of columns in the board (must be > 1 and odd)
   * @param redDeck the deck of cards for the red player
   * @param blueDeck the deck of cards for the blue player
   * @param handSize the initial hand size for each player (must be <= deck size / 3)
   * @throws IllegalArgumentException if any parameters are invalid
   */
  void initGame(int rows, int cols, List<Card> redDeck, List<Card> blueDeck, int handSize);

  /**
   * Start the game, dealing initial hands to players.
   * Red player always goes first.
   */
  void startGame();

  /**
   * Pass the current player's turn.
   * @throws IllegalStateException if the game is over
   */
  void passTurn();

  /**
   * Place a card from the current player's hand onto the board.
   *
   * @param cardIndex the index of the card in the player's hand
   * @param row the row to place the card
   * @param col the column to place the card
   * @throws IllegalArgumentException if the placement is invalid
   * @throws IllegalStateException if the game is over
   */
  void placeCard(int cardIndex, int row, int col);

  ReadonlyPawnsBoardModel copy();
}