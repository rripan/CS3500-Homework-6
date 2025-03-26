package cs3500.pawnsboard.Strategy;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.CardImp;
import cs3500.pawnsboard.model.mock.MockPawnsBoardModel;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.Player;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Additional tests for ChainedStrategy.
 */
public class ChainedStrategyTest {

  private ReadonlyPawnsBoardModel model;
  private List<Card> redHand;
  private List<Card> blueHand;
  private Strategy fillFirstStrategy;
  private Strategy maximizeRowScoreStrategy;
  private Strategy controlBoardStrategy;
  private Strategy chainedStrategy;

  @Before
  public void setUp() {
    redHand = new ArrayList<>();
    blueHand = new ArrayList<>();
    String[] influenceGrid = {"XXXXX", "XXIXX", "XICIX", "XXIXX", "XXXXX"};

    redHand.add(new CardImp("Attack", 2, 5, influenceGrid));
    redHand.add(new CardImp("Boost", 1, 3, influenceGrid));
    blueHand.add(new CardImp("Defense", 1, 3, influenceGrid));

    // ✅ Correct Mock Model Instantiation
    model = new MockPawnsBoardModel(
        new StringBuilder(),  // Log
        Player.RED,          // Current player
        5,                   // Rows
        5,                   // Columns
        true,                // Game is active
        Player.RED,          // First player
        new ArrayList<>(),   // Board contents
        new ArrayList<>(),   // Board owners
        new ArrayList<>(),   // Pawn counts
        redHand,             // Red player hand
        blueHand,            // Blue player hand
        new int[5][2]        // Row scores
    );

    // ✅ Correct ChainedStrategy Initialization
    fillFirstStrategy = new FillFirstStrategy();
    maximizeRowScoreStrategy = new MaximizeRowScoreStrategy();
    controlBoardStrategy = new ControlBoardStrategy();
    chainedStrategy = new ChainedStrategy(List.of(fillFirstStrategy, maximizeRowScoreStrategy, controlBoardStrategy));
  }

  /** ✅ **Test that ChainedStrategy falls back if the first strategy fails** */
  @Test
  public void testChainedStrategyFallback() {
    model = new MockPawnsBoardModel(new StringBuilder(), Player.RED, 5, 5, true, Player.RED,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
        new ArrayList<>(), new ArrayList<>(), new int[5][2]);

    chainedStrategy = new ChainedStrategy(List.of(fillFirstStrategy, maximizeRowScoreStrategy));
    Strategy.Move move = chainedStrategy.chooseMove(model, Player.RED);
    assertNull("ChainedStrategy should return null if all strategies fail", move);
  }

  /** ✅ **Test ChainedStrategy picks FillFirst if available** */
  @Test
  public void testChainedStrategyPrefersFirstStrategy() {
    Strategy.Move move = chainedStrategy.chooseMove(model, Player.RED);
    Strategy.Move fillFirstMove = fillFirstStrategy.chooseMove(model, Player.RED);
    assertEquals("ChainedStrategy should pick the first strategy's move", fillFirstMove, move);
  }

  /** ✅ **Test ChainedStrategy picks MaximizeRowScore if FillFirst fails** */
  @Test
  public void testChainedStrategyUsesMaximizeRowScore() {
    model = new MockPawnsBoardModel(new StringBuilder(), Player.RED, 5, 5, true, Player.RED,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
        new ArrayList<>(), blueHand, new int[5][2]);

    chainedStrategy = new ChainedStrategy(List.of(fillFirstStrategy, maximizeRowScoreStrategy));
    Strategy.Move move = chainedStrategy.chooseMove(model, Player.RED);
    Strategy.Move maxRowMove = maximizeRowScoreStrategy.chooseMove(model, Player.RED);

    assertEquals("ChainedStrategy should pick MaximizeRowScore if FillFirst fails", maxRowMove, move);
  }

  /** ✅ **Test ChainedStrategy uses ControlBoardStrategy when previous two fail** */
  @Test
  public void testChainedStrategyUsesControlBoardStrategy() {
    model = new MockPawnsBoardModel(new StringBuilder(), Player.RED, 5, 5, true, Player.RED,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
        new ArrayList<>(), new ArrayList<>(), new int[5][2]);

    chainedStrategy = new ChainedStrategy(List.of(fillFirstStrategy, maximizeRowScoreStrategy, controlBoardStrategy));
    Strategy.Move move = chainedStrategy.chooseMove(model, Player.RED);
    Strategy.Move controlMove = controlBoardStrategy.chooseMove(model, Player.RED);

    assertEquals("ChainedStrategy should pick ControlBoardStrategy if others fail", controlMove, move);
  }

  /** ✅ **Test ChainedStrategy works with only one strategy in the chain** */
  @Test
  public void testChainedStrategyWithOneStrategy() {
    chainedStrategy = new ChainedStrategy(List.of(fillFirstStrategy));
    Strategy.Move move = chainedStrategy.chooseMove(model, Player.RED);

    assertEquals("ChainedStrategy with one strategy should behave like that strategy",
        fillFirstStrategy.chooseMove(model, Player.RED), move);
  }

  /** ✅ **Test ChainedStrategy returns null when all strategies fail** */
  @Test
  public void testChainedStrategyFailsGracefully() {
    model = new MockPawnsBoardModel(new StringBuilder(), Player.RED, 5, 5, true, Player.RED,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
        new ArrayList<>(), new ArrayList<>(), new int[5][2]);

    chainedStrategy = new ChainedStrategy(List.of(fillFirstStrategy, maximizeRowScoreStrategy, controlBoardStrategy));
    Strategy.Move move = chainedStrategy.chooseMove(model, Player.RED);
    assertNull("ChainedStrategy should return null when no strategies work", move);
  }
}
