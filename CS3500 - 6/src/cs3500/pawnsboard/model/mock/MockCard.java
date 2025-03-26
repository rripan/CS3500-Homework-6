package cs3500.pawnsboard.model.mock;

import cs3500.pawnsboard.model.Card;

import java.util.Arrays;

/**
 * A mock implementation of Card for testing.
 */
public class MockCard implements Card {
  private final String name;
  private final int cost;
  private final int valueScore;
  private final String[] influenceGrid;

  /**
   * Constructs a new mock card with the given parameters.
   *
   * @param name the name of the card
   * @param cost the cost of the card (1-3 pawns)
   * @param valueScore the value score of the card
   * @param influenceGrid the influence grid of the card
   */
  public MockCard(String name, int cost, int valueScore, String[] influenceGrid) {
    this.name = name;
    this.cost = cost;
    this.valueScore = valueScore;
    this.influenceGrid = Arrays.copyOf(influenceGrid, influenceGrid.length);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getCost() {
    return cost;
  }

  @Override
  public int getValueScore() {
    return valueScore;
  }

  @Override
  public boolean hasInfluenceAt(int relRow, int relCol) {
    // Convert relative coordinates to absolute coordinates in the grid
    int absRow = relRow + 2;
    int absCol = relCol + 2;

    return influenceGrid[absRow].charAt(absCol) == 'I';
  }

  @Override
  public String[] getInfluenceGrid() {
    return Arrays.copyOf(influenceGrid, influenceGrid.length);
  }

  @Override
  public Card copy() {
    return new MockCard(name, cost, valueScore, influenceGrid);
  }

  @Override
  public String toString() {
    return name + " (Cost: " + cost + ", Value: " + valueScore + ")";
  }
}