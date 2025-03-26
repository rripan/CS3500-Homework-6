package cs3500.pawnsboard.model;

import java.util.Arrays;
import java.util.Objects;


/**
 * Implementation of the Card interface for the Pawns Board game.
 * This class is immutable.
 */
public class CardImp implements Card {
  private final String name;
  private final int cost;
  private final int valueScore;
  private final String[] influenceGrid;

  /**
   * Constructs a new card with the given properties.
   *
   * @param name the name of the card
   * @param cost the cost of the card (1-3 pawns)
   * @param valueScore the value score of the card (positive integer)
   * @param influenceGrid a 5x5 grid representing the card's influence
   * @throws IllegalArgumentException if any parameters are invalid
   */
  public CardImp(String name, int cost, int valueScore, String[] influenceGrid) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Card name cannot be null or empty");
    }
    if (cost < 1 || cost > 3) {
      throw new IllegalArgumentException("Card cost must be between 1 and 3");
    }
    if (valueScore <= 0) {
      throw new IllegalArgumentException("Card value score must be positive");
    }
    validateInfluenceGrid(influenceGrid);

    this.name = name;
    this.cost = cost;
    this.valueScore = valueScore;
    // Create a defensive copy of the influence grid
    this.influenceGrid = Arrays.copyOf(influenceGrid, influenceGrid.length);
  }


  /**
   * Validates the influence grid to ensure it is properly formatted.
   *
   * @param grid the influence grid to validate
   * @throws IllegalArgumentException if the grid is invalid
   */
  private void validateInfluenceGrid(String[] grid) {
    if (grid == null) {
      throw new IllegalArgumentException("Influence grid cannot be null");
    }
    if (grid.length != 5) {
      throw new IllegalArgumentException("Influence grid must have 5 rows");
    }

    boolean foundCenter = false;

    for (int i = 0; i < 5; i++) {
      if (grid[i] == null) {
        throw new IllegalArgumentException("Influence grid row cannot be null");
      }
      if (grid[i].length() != 5) {
        throw new IllegalArgumentException("Each row in influence grid must have 5 characters");
      }

      for (int j = 0; j < 5; j++) {
        char c = grid[i].charAt(j);
        if (c != 'X' && c != 'I' && c != 'C') {
          throw new IllegalArgumentException(
                  "Influence grid can only contain 'X', 'I', or 'C' characters");
        }

        if (c == 'C') {
          if (foundCenter) {
            throw new IllegalArgumentException("Influence grid can only have one center ('C')");
          }
          if (i != 2 || j != 2) {
            throw new IllegalArgumentException("Center ('C') must be at position (2,2)");
          }
          foundCenter = true;
        }
      }
    }

    if (!foundCenter) {
      throw new IllegalArgumentException("Influence grid must have a center ('C') at position (2,2)");
    }
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
    // The influence grid is a 5x5 grid, with (0,0) at the center
    if (relRow < -2 || relRow > 2 || relCol < -2 || relCol > 2) {
      throw new IllegalArgumentException("Relative coordinates must be between -2 and 2");
    }

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
    return null;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardImp card = (CardImp) o;
    return cost == card.cost
            && valueScore == card.valueScore
            && Objects.equals(name, card.name)
            && Arrays.equals(influenceGrid, card.influenceGrid);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(name, cost, valueScore);
    result = 31 * result + Arrays.hashCode(influenceGrid);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name).append(" (Cost: ").append(cost).append(", Value: ").append(valueScore).append(")\n");
    for (String row : influenceGrid) {
      sb.append(row).append("\n");
    }
    return sb.toString();
  }
}

