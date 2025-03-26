package cs3500.pawnsboard.model.mock;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock implementation of ReadonlyPawnsBoardModel for testing strategies.
 * This mock records method calls and can be configured to return specific values.
 */
public class MockPawnsBoardModel implements ReadonlyPawnsBoardModel {
  private final StringBuilder log;
  private final Player currentPlayer;
  private final int rows;
  private final int cols;
  private final boolean gameOver;
  private final Player winner;
  private final List<List<CellContent>> boardContent;
  private final List<List<Player>> boardOwners;
  private final List<List<Integer>> pawnCounts;
  private final List<Card> redHand;
  private final List<Card> blueHand;
  private final int[][] rowScores;

  /**
   * Constructs a new mock model with the given parameters.
   *
   * @param log the StringBuilder to record method calls
   * @param currentPlayer the current player
   * @param rows the number of rows on the board
   * @param cols the number of columns on the board
   * @param gameOver whether the game is over
   * @param winner the winner of the game, or null if tied or not over
   * @param boardContent the content of each cell on the board
   * @param boardOwners the owner of each cell on the board
   * @param pawnCounts the number of pawns on each cell on the board
   * @param redHand the cards in the red player's hand
   * @param blueHand the cards in the blue player's hand
   * @param rowScores the scores for each player in each row
   */
  public MockPawnsBoardModel(StringBuilder log, Player currentPlayer, int rows, int cols,
                             boolean gameOver, Player winner,
                             List<List<CellContent>> boardContent,
                             List<List<Player>> boardOwners,
                             List<List<Integer>> pawnCounts,
                             List<Card> redHand,
                             List<Card> blueHand,
                             int[][] rowScores) {
    this.log = log;
    this.currentPlayer = currentPlayer;
    this.rows = rows;
    this.cols = cols;
    this.gameOver = gameOver;
    this.winner = winner;
    this.boardContent = boardContent;
    this.boardOwners = boardOwners;
    this.pawnCounts = pawnCounts;
    this.redHand = redHand;
    this.blueHand = blueHand;
    this.rowScores = rowScores;
  }

  @Override
  public Player getCurrentPlayer() {
    log.append("getCurrentPlayer()\n");
    return currentPlayer;
  }

  @Override
  public List<Card> getPlayerHand(Player player) {
    log.append("getPlayerHand(").append(player).append(")\n");
    return player == Player.RED ? new ArrayList<>(redHand) : new ArrayList<>(blueHand);
  }

  @Override
  public int getRows() {
    log.append("getRows()\n");
    return rows;
  }

  @Override
  public int getColumns() {
    log.append("getColumns()\n");
    return cols;
  }

  @Override
  public CellContent getCellContent(int row, int col) {
    log.append("getCellContent(").append(row).append(",").append(col).append(")\n");
    return boardContent.get(row).get(col);
  }

  @Override
  public Player getCellOwner(int row, int col) {
    log.append("getCellOwner(").append(row).append(",").append(col).append(")\n");
    return boardOwners.get(row).get(col);
  }

  @Override
  public int getPawnCount(int row, int col) {
    log.append("getPawnCount(").append(row).append(",").append(col).append(")\n");
    return pawnCounts.get(row).get(col);
  }

  @Override
  public Card getCard(int row, int col) {
    log.append("getCard(").append(row).append(",").append(col).append(")\n");
    return null; // Mock cards not implemented for simplicity
  }

  @Override
  public int getRowScore(Player player, int row) {
    log.append("getRowScore(").append(player).append(",").append(row).append(")\n");
    return rowScores[row][player == Player.RED ? 0 : 1];
  }

  @Override
  public int getTotalScore(Player player) {
    log.append("getTotalScore(").append(player).append(")\n");
    int totalScore = 0;
    for (int r = 0; r < rows; r++) {
      int playerRowScore = getRowScore(player, r);
      int opponentRowScore = getRowScore(player.opponent(), r);
      if (playerRowScore > opponentRowScore) {
        totalScore += playerRowScore;
      }
    }
    return totalScore;
  }

  @Override
  public boolean isGameOver() {
    log.append("isGameOver()\n");
    return gameOver;
  }

  @Override
  public Player getWinner() {
    log.append("getWinner()\n");
    if (!gameOver) {
      throw new IllegalStateException("Game is not over yet");
    }
    return winner;
  }

  @Override
  public boolean hasPlayerPassed(Player player) {
    log.append("hasPlayerPassed(").append(player).append(")\n");
    return false;
  }



  @Override
  public boolean isLegalMove(int cardIndex, int row, int col) {
    log.append("isLegalMove(").append(cardIndex).append(",").append(row).append(",").append(col).append(")\n");

    // Check if valid coordinates
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      return false;
    }

    // Check if the cell contains pawns owned by the current player
    if (boardContent.get(row).get(col) != CellContent.PAWN ||
            boardOwners.get(row).get(col) != currentPlayer) {
      return false;
    }

    // Check if the card index is valid
    List<Card> hand = currentPlayer == Player.RED ? redHand : blueHand;
    if (cardIndex < 0 || cardIndex >= hand.size()) {
      return false;
    }

    // Check if there are enough pawns
    if (pawnCounts.get(row).get(col) < hand.get(cardIndex).getCost()) {
      return false;
    }

    return true;
  }

  /**
   * Gets the log of method calls.
   *
   * @return the log of method calls
   */
  public String getLog() {
    return log.toString();
  }
}