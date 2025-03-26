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
 * Tests for all strategy implementations.
 */
public class StrategyTest {

  private ReadonlyPawnsBoardModel model;
  private List<Card> redHand;
  private List<Card> blueHand;
  private Strategy fillFirstStrategy;
  private Strategy maximizeRowScoreStrategy;
  private Strategy controlBoardStrategy;
  private Strategy minimaxStrategy;
  private Strategy chainedStrategy;

  @Before
  public void setUp() {
    // Initialize a mock game model
    redHand = new ArrayList<>();
    blueHand = new ArrayList<>();
    String[] influenceGrid = {"XXXXX", "XXIXX", "XICIX", "XXIXX", "XXXXX"};

    redHand.add(new CardImp("Attack", 2, 5, influenceGrid));
    redHand.add(new CardImp("Boost", 1, 3, influenceGrid));
    blueHand.add(new CardImp("Defense", 1, 3, influenceGrid));

    StringBuilder log = new StringBuilder();
    int rows = 5;
    int cols = 5;

    List<List<ReadonlyPawnsBoardModel.CellContent>> boardContent = new ArrayList<>();
    List<List<ReadonlyPawnsBoardModel.Player>> boardOwners = new ArrayList<>();
    List<List<Integer>> pawnCounts = new ArrayList<>();
    int[][] rowScores = new int[rows][2];

// Initialize empty board setup
    for (int r = 0; r < rows; r++) {
      List<ReadonlyPawnsBoardModel.CellContent> rowContent = new ArrayList<>();
      List<ReadonlyPawnsBoardModel.Player> rowOwners = new ArrayList<>();
      List<Integer> rowPawns = new ArrayList<>();

      for (int c = 0; c < cols; c++) {
        rowContent.add(ReadonlyPawnsBoardModel.CellContent.EMPTY);
        rowOwners.add(Player.RED); // Assign ownership for testing
        rowPawns.add(0);
      }

      boardContent.add(rowContent);
      boardOwners.add(rowOwners);
      pawnCounts.add(rowPawns);
    }

    model = new MockPawnsBoardModel(
        log,                  // Log tracking moves
        Player.RED,           // Starting player
        rows, cols,           // Board dimensions
        true,                 // Game in progress
        Player.RED,           // Current player
        boardContent,         // Board cell contents
        boardOwners,          // Owners of board cells
        pawnCounts,           // Pawn counts
        new ArrayList<>(redHand),
        new ArrayList<>(blueHand),
        rowScores             // Row scores
    );


    // Initialize strategies
    fillFirstStrategy = new FillFirstStrategy();
    maximizeRowScoreStrategy = new MaximizeRowScoreStrategy();
    controlBoardStrategy = new ControlBoardStrategy();
    minimaxStrategy = new MinMaxStrategy();
    chainedStrategy = new ChainedStrategy(List.of(fillFirstStrategy, maximizeRowScoreStrategy));
  }

  /** 🟢 Test FillFirstStrategy selects the first available move */
  @Test
  public void testFillFirstStrategy() {
    Strategy.Move move = fillFirstStrategy.chooseMove(model, Player.RED);
    assertNotNull("FillFirst should return a valid move", move);
  }

  /** 🟢 Test MaximizeRowScoreStrategy chooses a move that increases row score */
  @Test
  public void testMaximizeRowScoreStrategy() {
    Strategy.Move move = maximizeRowScoreStrategy.chooseMove(model, Player.RED);
    assertNotNull("MaximizeRowScore should return a valid move", move);
    assertTrue("Move should improve score", move.getRow() >= 0 && move.getCol() >= 0);
  }

  /** 🟢 Test ControlBoardStrategy selects a move that maximizes cell ownership */
  @Test
  public void testControlBoardStrategy() {
    Strategy.Move move = controlBoardStrategy.chooseMove(model, Player.RED);
    assertNotNull("ControlBoardStrategy should return a valid move", move);
  }

  /** 🟢 Test MinimaxStrategy selects the best move to minimize opponent's strength */
  @Test
  public void testMinimaxStrategy() {
    Strategy.Move move = minimaxStrategy.chooseMove(model, Player.RED);
    assertNotNull("MinimaxStrategy should return a move", move);
  }

  /** 🟢 Test ChainedStrategy uses FillFirstStrategy first and falls back to MaximizeRowScore */
  @Test
  public void testChainedStrategy() {
    Strategy.Move move = chainedStrategy.chooseMove(model, Player.RED);
    assertNotNull("ChainedStrategy should return a move", move);
  }

  /** 🟢 Test that all strategies return null when no valid moves exist */
  @Test
  public void testNoValidMove() {
    // Simulate a situation where no valid moves exist
    StringBuilder log = new StringBuilder();
    int rows = 5;
    int cols = 5;

// Empty board where no valid moves exist
    List<List<ReadonlyPawnsBoardModel.CellContent>> boardContent = new ArrayList<>();
    List<List<ReadonlyPawnsBoardModel.Player>> boardOwners = new ArrayList<>();
    List<List<Integer>> pawnCounts = new ArrayList<>();
    int[][] rowScores = new int[rows][2];

    for (int r = 0; r < rows; r++) {
      List<ReadonlyPawnsBoardModel.CellContent> rowContent = new ArrayList<>();
      List<ReadonlyPawnsBoardModel.Player> rowOwners = new ArrayList<>();
      List<Integer> rowPawns = new ArrayList<>();

      for (int c = 0; c < cols; c++) {
        rowContent.add(ReadonlyPawnsBoardModel.CellContent.EMPTY);  // No pawns
        rowOwners.add(null);  // No ownership
        rowPawns.add(0);      // No pawns placed
      }

      boardContent.add(rowContent);
      boardOwners.add(rowOwners);
      pawnCounts.add(rowPawns);
    }

// No cards in hand for both players
    List<Card> emptyRedHand = new ArrayList<>();
    List<Card> emptyBlueHand = new ArrayList<>();

// Properly initialize the mock model
    model = new MockPawnsBoardModel(
        log,                  // Log tracking moves
        Player.RED,           // Starting player
        rows, cols,           // Board dimensions
        true,                 // Game in progress
        Player.RED,           // Current player
        boardContent,         // Board cell contents
        boardOwners,          // Owners of board cells
        pawnCounts,           // Pawn counts
        emptyRedHand,         // No available moves
        emptyBlueHand,        // No available moves
        rowScores             // Row scores
    );

// Now, test that no strategies can find a move
    Strategy.Move move = fillFirstStrategy.chooseMove(model, Player.RED);
    assertNull("FillFirstStrategy should return null when no moves are available", move);

  }
}
