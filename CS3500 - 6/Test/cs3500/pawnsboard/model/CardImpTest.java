package cs3500.pawnsboard.model;

import cs3500.pawnsboard.model.mock.MockPawnsBoardModel;

import org.junit.Test;

import static cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.Player.RED;
import static org.junit.Assert.*;
import java.util.ArrayList;

/**
 * Tests for the Card implementation.
 */
public class CardImpTest {

  /**
   * Test creating a valid card.
   */
  @Test
  public void testValidCardCreation() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    Card card = new CardImp("TestCard", 2, 3, influenceGrid);

    assertEquals("TestCard", card.getName());
    assertEquals(2, card.getCost());
    assertEquals(3, card.getValueScore());

    // Test that the influence grid is properly read
    assertTrue(card.hasInfluenceAt(0, 0)); // The center position (card itself)
    assertTrue(card.hasInfluenceAt(-1, 0)); // One space up
    assertTrue(card.hasInfluenceAt(1, 0)); // One space down
    assertTrue(card.hasInfluenceAt(0, 1)); // One space right

    assertFalse(card.hasInfluenceAt(-2, -2)); // Top-left corner
    assertFalse(card.hasInfluenceAt(2, 2)); // Bottom-right corner
  }

  /**
   * Test creating a card with invalid parameters.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_NullName() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    new CardImp(null, 2, 3, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_EmptyName() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    new CardImp("", 2, 3, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_ZeroCost() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    new CardImp("TestCard", 0, 3, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_NegativeCost() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    new CardImp("TestCard", -1, 3, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_CostTooHigh() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    new CardImp("TestCard", 4, 3, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_ZeroValueScore() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    new CardImp("TestCard", 2, 0, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_NegativeValueScore() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    new CardImp("TestCard", 2, -1, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_NullInfluenceGrid() {
    new CardImp("TestCard", 2, 3, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_WrongRowCount() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX"
    };

    new CardImp("TestCard", 2, 3, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_WrongColumnCount() {
    String[] influenceGrid = {
            "XXXX",
            "XXIX",
            "XICX",
            "XXIX",
            "XXXX"
    };

    new CardImp("TestCard", 2, 3, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_InvalidCharacter() {
    String[] influenceGrid = {
            "XXXXX",
            "XXQXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    new CardImp("TestCard", 2, 3, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_NoCenter() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XIIIX",
            "XXIXX",
            "XXXXX"
    };

    new CardImp("TestCard", 2, 3, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_CenterInWrongPosition() {
    String[] influenceGrid = {
            "XXXXX",
            "XCXXX",
            "XIIIX",
            "XXIXX",
            "XXXXX"
    };

    new CardImp("TestCard", 2, 3, influenceGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardCreation_MultipleCenters() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXCXX",
            "XXXXX"
    };

    new CardImp("TestCard", 2, 3, influenceGrid);
  }

  /**
   * Test the copy method.
   */
  @Test
  public void testCardCopy() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    Card original = new CardImp("TestCard", 2, 3, influenceGrid);
    Card copy = original.copy();

    // Test that the copy has the same values
    assertEquals(original.getName(), copy.getName());
    assertEquals(original.getCost(), copy.getCost());
    assertEquals(original.getValueScore(), copy.getValueScore());

    // Test that the influence grid is the same
    assertArrayEquals(original.getInfluenceGrid(), copy.getInfluenceGrid());

    // Test that it's a deep copy
    assertNotSame(original, copy);
    assertNotSame(original.getInfluenceGrid(), copy.getInfluenceGrid());
  }

  /**
   * Test equals and hashCode.
   */
  @Test
  public void testEqualsAndHashCode() {
    String[] influenceGrid1 = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    String[] influenceGrid2 = {
            "XXXXX",
            "XIIII",
            "XICII",
            "XIIII",
            "XXXXX"
    };

    Card card1 = new CardImp("TestCard", 2, 3, influenceGrid1);
    Card card2 = new CardImp("TestCard", 2, 3, influenceGrid1); // Same as card1
    Card card3 = new CardImp("DifferentCard", 2, 3, influenceGrid1); // Different name
    Card card4 = new CardImp("TestCard", 1, 3, influenceGrid1); // Different cost
    Card card5 = new CardImp("TestCard", 2, 4, influenceGrid1); // Different value
    Card card6 = new CardImp("TestCard", 2, 3, influenceGrid2); // Different influence

    // Reflexivity
    assertEquals(card1, card1);

    // Symmetry
    assertEquals(card1, card2);
    assertEquals(card2, card1);

    // Different cards should not be equal
    assertNotEquals(card1, card3);
    assertNotEquals(card1, card4);
    assertNotEquals(card1, card5);
    assertNotEquals(card1, card6);

    // null and different types
    assertNotEquals(card1, null);
    assertNotEquals(card1, "NotACard");

    // hashCode consistency
    assertEquals(card1.hashCode(), card2.hashCode());

    // Different cards should have different hashCodes (not guaranteed, but likely)
    assertNotEquals(card1.hashCode(), card3.hashCode());
  }

  /**
   * Test the hasInfluenceAt method with invalid coordinates.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testHasInfluenceAt_InvalidCoordinates_TooNegative() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    Card card = new CardImp("TestCard", 2, 3, influenceGrid);
    card.hasInfluenceAt(-3, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHasInfluenceAt_InvalidCoordinates_TooPositive() {
    String[] influenceGrid = {
            "XXXXX",
            "XXIXX",
            "XICIX",
            "XXIXX",
            "XXXXX"
    };

    Card card = new CardImp("TestCard", 2, 3, influenceGrid);
    card.hasInfluenceAt(0, 3);
  }
  /**
   * Test if the game correctly identifies when it has ended.
   */
  @Test
  public void testGameEndsCorrectly() {
    // Create a mock game state that is over
    MockPawnsBoardModel gameModel = new MockPawnsBoardModel(
        new StringBuilder(), RED,  // Current player
        5, 5,        // Board dimensions
        true,        // Game over state
        RED,  // Winner
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
        new ArrayList<>(), new ArrayList<>(), new int[5][2]
    );

    // Check if the game model recognizes that the game has ended
    assertTrue("Game should be over", gameModel.isGameOver());

    // Check if the winner is correctly assigned
    assertEquals("Winner should be RED", RED, gameModel.getWinner());
  }

}