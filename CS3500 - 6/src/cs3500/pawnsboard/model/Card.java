package cs3500.pawnsboard.model;


/**
 * Represents a card in the Pawns Board game. Each card has a name, cost, value score,
 * and an influence grid.
 */
public interface Card {
  /**
   * Get the name of the card.
   * @return the name of the card
   */
  String getName();

  /**
   * Get the cost of the card (1-3 pawns).
   * @return the cost of the card
   */
  int getCost();

  /**
   * Get the value score of the card.
   * @return the value score of the card
   */
  int getValueScore();

  /**
   * Check if the card influences a relative position on the influence grid.
   * The origin (0,0) represents the card's position.
   *
   * @param relRow the relative row (-2 to 2)
   * @param relCol the relative column (-2 to 2)
   * @return true if the card influences the position, false otherwise
   * @throws IllegalArgumentException if the relative coordinates are out of range
   */
  boolean hasInfluenceAt(int relRow, int relCol);

  /**
   * Get a string representation of the influence grid.
   * @return a 5x5 character grid representing the influence
   */
  String[] getInfluenceGrid();

  /**
   * Creates a copy of this card.
   * @return a new card with the same properties
   */
  Card copy();
}

