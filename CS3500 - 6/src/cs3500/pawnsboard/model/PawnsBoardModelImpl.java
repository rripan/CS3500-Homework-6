package cs3500.pawnsboard.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implementation of the PawnsBoardModel interface for the Pawns Board game.
 * This class maintains the game state and enforces the rules of the game.
 */
public class PawnsBoardModelImpl implements PawnsBoardModel {

  // The board is represented as a 2D array of cells
  private Cell[][] board;

  // The decks and hands of each player
  private List<Card> redDeck;
  private List<Card> blueDeck;
  private List<Card> redHand;
  private List<Card> blueHand;

  // Game state variables
  private Player currentPlayer;
  private boolean redPassed;
  private boolean bluePassed;
  private boolean gameStarted;
  private int rows;
  private int cols;

  // Random number generator for card dealing
  private final Random random;

  /**
   * Constructs a new PawnsBoardModel with default settings.
   * The game is not initialized until initGame() is called.
   */
  public PawnsBoardModelImpl() {
    this.random = new Random();
    this.gameStarted = false;
  }

  /**
   * Constructs a new PawnsBoardModel with a specified random seed.
   * This constructor is primarily for testing purposes to ensure reproducible results.
   *
   * @param seed the seed for the random number generator
   */
  public PawnsBoardModelImpl(long seed) {
    this.random = new Random(seed);
    this.gameStarted = false;
  }

  /**
   * Cell class to represent a single cell on the board.
   * A cell can contain pawns, a card, or nothing.
   */
  private static class Cell {
    private CellContent content;
    private Player owner;
    private int pawnCount;
    private Card card;

    /**
     * Constructs a new empty cell.
     */
    public Cell() {
      this.content = CellContent.EMPTY;
      this.owner = null;
      this.pawnCount = 0;
      this.card = null;
    }

    /**
     * Creates a copy of this cell.
     * @return a new cell with the same properties
     */
    public Cell copy() {
      Cell copy = new Cell();
      copy.content = this.content;
      copy.owner = this.owner;
      copy.pawnCount = this.pawnCount;
      copy.card = this.card != null ? this.card.copy() : null;
      return copy;
    }
  }

  @Override
  public void initGame(int rows, int cols, List<Card> redDeck, List<Card> blueDeck, int handSize) {
    // Validate parameters
    if (rows <= 0) {
      throw new IllegalArgumentException("Number of rows must be positive");
    }
    if (cols <= 1 || cols % 2 == 0) {
      throw new IllegalArgumentException("Number of columns must be greater than 1 and odd");
    }
    if (redDeck == null || blueDeck == null) {
      throw new IllegalArgumentException("Decks cannot be null");
    }
    if (redDeck.isEmpty() || blueDeck.isEmpty()) {
      throw new IllegalArgumentException("Decks cannot be empty");
    }
    if (handSize <= 0 || handSize > redDeck.size() / 3 || handSize > blueDeck.size() / 3) {
      throw new IllegalArgumentException(
              "Hand size must be positive and at most one-third of the deck size");
    }

    // Check if there are enough cards to potentially fill the board
    int totalCells = rows * cols;
    if (redDeck.size() + blueDeck.size() < totalCells) {
      throw new IllegalArgumentException(
              "Each deck must have at least enough cards to fill the board");
    }

    // Ensure no more than two copies of any card in each deck
    validateDeckDuplicates(redDeck);
    validateDeckDuplicates(blueDeck);


    // Initialize the board
    this.rows = rows;
    this.cols = cols;
    this.board = new Cell[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        this.board[r][c] = new Cell();
      }
    }

    // Initialize the first and last column with pawns
    for (int r = 0; r < rows; r++) {
      // First column - Red pawns
      this.board[r][0].content = CellContent.PAWN;
      this.board[r][0].owner = Player.RED;
      this.board[r][0].pawnCount = 1;

      // Last column - Blue pawns
      this.board[r][cols - 1].content = CellContent.PAWN;
      this.board[r][cols - 1].owner = Player.BLUE;
      this.board[r][cols - 1].pawnCount = 1;
    }

    // Initialize player decks and hands
    this.redDeck = new ArrayList<>(redDeck);
    this.blueDeck = new ArrayList<>(blueDeck);
    this.redHand = new ArrayList<>();
    this.blueHand = new ArrayList<>();

    // Reset game state
    this.currentPlayer = Player.RED; // Red always goes first
    this.redPassed = false;
    this.bluePassed = false;
    this.gameStarted = false;


  }

  /**
   * Validates that a deck has no more than two copies of any card.
   *
   * @param deck the deck to validate
   * @throws IllegalArgumentException if the deck has more than two copies of any card
   */
  private void validateDeckDuplicates(List<Card> deck) {
    for (int i = 0; i < deck.size(); i++) {
      Card card = deck.get(i);
      int count = 1;

      for (int j = i + 1; j < deck.size(); j++) {
        if (card.equals(deck.get(j))) {
          count++;
          if (count > 2) {
            throw new IllegalArgumentException(
                    "A deck cannot contain more than two copies of any card");
          }
        }
      }
    }
  }

  @Override
  public void startGame() {
    if (board == null) {
      throw new IllegalStateException("Game has not been initialized");
    }

    if (gameStarted) {
      throw new IllegalStateException("Game has already started");
    }

    // Deal initial hands
    for (int i = 0; i < getHandSize(); i++) {
      // Deal to red player
      if (!redDeck.isEmpty()) {
        int index = random.nextInt(redDeck.size());
        redHand.add(redDeck.remove(index));
      }

      // Deal to blue player
      if (!blueDeck.isEmpty()) {
        int index = random.nextInt(blueDeck.size());
        blueHand.add(blueDeck.remove(index));
      }
    }

    gameStarted = true;
    currentPlayer = Player.RED; // Red always goes first
    redPassed = false;
    bluePassed = false;
  }

  /**
   * Get the initial hand size.
   * @return the initial hand size
   */
  private int getHandSize() {
    return Math.max(Math.min(redHand.size(), blueHand.size()), 5); // Default to 5 if not specified
  }

  @Override
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  @Override
  public void passTurn() {
    checkGameStarted();
    checkGameNotOver();

    // Mark the current player as having passed
    if (currentPlayer == Player.RED) {
      redPassed = true;
    } else {
      bluePassed = true;
    }

    // Switch to the other player
    switchPlayer();
  }

  @Override
  public void placeCard(int cardIndex, int row, int col) {
    checkGameStarted();
    checkGameNotOver();

    // Validate coordinates
    validateCoordinates(row, col);

    List<Card> hand = (currentPlayer == Player.RED) ? redHand : blueHand;

    // Validate card index
    if (cardIndex < 0 || cardIndex >= hand.size()) {
      throw new IllegalArgumentException("Invalid card index: " + cardIndex);
    }

    Card card = hand.get(cardIndex);

    // Check if the cell contains pawns owned by the current player
    Cell cell = board[row][col];
    if (cell.content != CellContent.PAWN || cell.owner != currentPlayer) {
      throw new IllegalArgumentException("Cell does not contain pawns owned by current player");
    }

    // Check if there are enough pawns to cover the cost
    if (cell.pawnCount < card.getCost()) {
      throw new IllegalArgumentException("Not enough pawns to cover the card cost");
    }

    // Place the card on the cell
    cell.content = CellContent.CARD;
    cell.card = card;
    cell.pawnCount = 0; // Remove the pawns

    // Remove the card from the player's hand
    hand.remove(cardIndex);

    // Apply the card's influence
    applyCardInfluence(card, row, col);

    // Reset the pass flag for the current player
    if (currentPlayer == Player.RED) {
      redPassed = false;
    } else {
      bluePassed = false;
    }

    // Draw a card if possible
    drawCard();

    // Switch to the other player
    switchPlayer();
  }

  /**
   * Apply the influence of a card to the surrounding cells.
   *
   * @param card the card being placed
   * @param cardRow the row where the card is placed
   * @param cardCol the column where the card is placed
   */
  private void applyCardInfluence(Card card, int cardRow, int cardCol) {
    // Iterate over the 5x5 grid centered on the card
    for (int relRow = -2; relRow <= 2; relRow++) {
      for (int relCol = -2; relCol <= 2; relCol++) {
        // Skip if this position is not influenced by the card
        if (!card.hasInfluenceAt(relRow, relCol)) {
          continue;
        }

        // Calculate the absolute position on the board
        int absRow = cardRow + relRow;
        int absCol = cardCol + relCol;

        // Mirror the column for blue player
        if (currentPlayer == Player.BLUE) {
          absCol = cardCol - relCol;
        }

        // Skip if the position is outside the board
        if (absRow < 0 || absRow >= rows || absCol < 0 || absCol >= cols) {
          continue;
        }

        // Apply influence to the cell
        Cell cell = board[absRow][absCol];

        // Case 1: Cell has a card - No effect
        if (cell.content == CellContent.CARD) {
          continue;
        }

        // Case 2: Cell is empty - Add a pawn owned by current player
        if (cell.content == CellContent.EMPTY) {
          cell.content = CellContent.PAWN;
          cell.owner = currentPlayer;
          cell.pawnCount = 1;
          continue;
        }

        // Case 3: Cell has pawns
        if (cell.content == CellContent.PAWN) {
          // If owned by current player, increase pawn count (max 3)
          if (cell.owner == currentPlayer) {
            cell.pawnCount = Math.min(cell.pawnCount + 1, 3);
          }
          // If owned by opponent, change ownership
          else {
            cell.owner = currentPlayer;
          }
        }
      }
    }
  }

  /**
   * Draw a card from the current player's deck if possible.
   */
  private void drawCard() {
    List<Card> deck = (currentPlayer == Player.RED) ? redDeck : blueDeck;
    List<Card> hand = (currentPlayer == Player.RED) ? redHand : blueHand;

    if (!deck.isEmpty()) {
      int index = random.nextInt(deck.size());
      hand.add(deck.remove(index));
    }
  }

  /**
   * Switch to the other player.
   */
  private void switchPlayer() {
    currentPlayer = currentPlayer.opponent();
  }

  @Override
  public List<Card> getPlayerHand(Player player) {
    checkGameStarted();

    return Collections.unmodifiableList(
            player == Player.RED ? redHand : blueHand);
  }

  @Override
  public int getRows() {
    return rows;
  }

  @Override
  public int getColumns() {
    return cols;
  }

  @Override
  public CellContent getCellContent(int row, int col) {
    validateCoordinates(row, col);
    return board[row][col].content;
  }

  @Override
  public Player getCellOwner(int row, int col) {
    validateCoordinates(row, col);
    return board[row][col].owner;
  }

  @Override
  public int getPawnCount(int row, int col) {
    validateCoordinates(row, col);
    Cell cell = board[row][col];
    return cell.content == CellContent.PAWN ? cell.pawnCount : 0;
  }

  @Override
  public Card getCard(int row, int col) {
    validateCoordinates(row, col);
    Cell cell = board[row][col];
    return cell.content == CellContent.CARD ? cell.card : null;
  }

  @Override
  public int getRowScore(Player player, int row) {
    validateRow(row);

    int score = 0;
    for (int c = 0; c < cols; c++) {
      Cell cell = board[row][c];
      if (cell.content == CellContent.CARD && cell.owner == player) {
        score += cell.card.getValueScore();
      }
    }

    return score;
  }

  @Override
  public int getTotalScore(Player player) {
    int totalScore = 0;

    for (int r = 0; r < rows; r++) {
      int playerRowScore = getRowScore(player, r);
      int opponentRowScore = getRowScore(player.opponent(), r);

      // Add row score to total only if player has higher score in that row
      if (playerRowScore > opponentRowScore) {
        totalScore += playerRowScore;
      }
    }

    return totalScore;
  }

  @Override
  public boolean isGameOver() {
    return redPassed && bluePassed;
  }

  @Override
  public Player getWinner() {
    if (!isGameOver()) {
      throw new IllegalStateException("Game is not over yet");
    }

    int redScore = getTotalScore(Player.RED);
    int blueScore = getTotalScore(Player.BLUE);

    if (redScore > blueScore) {
      return Player.RED;
    } else if (blueScore > redScore) {
      return Player.BLUE;
    } else {
      return null; // Tie
    }
  }

  @Override
  public boolean hasPlayerPassed(Player player) {
    return player == Player.RED ? redPassed : bluePassed;
  }

  @Override
  public PawnsBoardModel copy() {
    PawnsBoardModelImpl copy = new PawnsBoardModelImpl(random.nextLong());

    // Copy board dimensions
    copy.rows = this.rows;
    copy.cols = this.cols;

    // Copy game state
    copy.currentPlayer = this.currentPlayer;
    copy.redPassed = this.redPassed;
    copy.bluePassed = this.bluePassed;
    copy.gameStarted = this.gameStarted;

    // Copy the board
    copy.board = new Cell[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        copy.board[r][c] = this.board[r][c].copy();
      }
    }

    // Copy decks and hands
    copy.redDeck = new ArrayList<>();
    for (Card card : this.redDeck) {
      copy.redDeck.add(card.copy());
    }

    copy.blueDeck = new ArrayList<>();
    for (Card card : this.blueDeck) {
      copy.blueDeck.add(card.copy());
    }

    copy.redHand = new ArrayList<>();
    for (Card card : this.redHand) {
      copy.redHand.add(card.copy());
    }

    copy.blueHand = new ArrayList<>();
    for (Card card : this.blueHand) {
      copy.blueHand.add(card.copy());
    }

    return copy;
  }

  /**
   * Validates the row coordinate.
   *
   * @param row the row coordinate to validate
   * @throws IllegalArgumentException if the row coordinate is invalid
   */
  private void validateRow(int row) {
    if (row < 0 || row >= rows) {
      throw new IllegalArgumentException("Invalid row: " + row);
    }
  }

  /**
   * Validates the column coordinate.
   *
   * @param col the column coordinate to validate
   * @throws IllegalArgumentException if the column coordinate is invalid
   */
  private void validateColumn(int col) {
    if (col < 0 || col >= cols) {
      throw new IllegalArgumentException("Invalid column: " + col);
    }
  }

  /**
   * Validates row and column coordinates.
   *
   * @param row the row coordinate to validate
   * @param col the column coordinate to validate
   * @throws IllegalArgumentException if either coordinate is invalid
   */
  private void validateCoordinates(int row, int col) {
    validateRow(row);
    validateColumn(col);
  }

  /**
   * Checks if the game has started.
   *
   * @throws IllegalStateException if the game has not started
   */
  private void checkGameStarted() {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not started");
    }
  }

  /**
   * Checks if the game is not over.
   *
   * @throws IllegalStateException if the game is over
   */
  private void checkGameNotOver() {
    if (isGameOver()) {
      throw new IllegalStateException("Game is over");
    }
  }

  @Override
  public boolean isLegalMove(int cardIndex, int row, int col) {
    // Check if game has started
    if (!gameStarted) {
      return false;
    }

    // Check if game is over
    if (isGameOver()) {
      return false;
    }

    // Validate coordinates
    try {
      validateCoordinates(row, col);
    } catch (IllegalArgumentException e) {
      return false;
    }

    // Get current player's hand
    List<Card> hand = (currentPlayer == Player.RED) ? redHand : blueHand;

    // Validate card index
    if (cardIndex < 0 || cardIndex >= hand.size()) {
      return false;
    }

    Card card = hand.get(cardIndex);

    // Check if the cell contains pawns owned by the current player
    Cell cell = board[row][col];
    if (cell.content != CellContent.PAWN || cell.owner != currentPlayer) {
      return false;
    }

    // Check if there are enough pawns to cover the cost
    if (cell.pawnCount < card.getCost()) {
      return false;
    }

    return true;
  }

}