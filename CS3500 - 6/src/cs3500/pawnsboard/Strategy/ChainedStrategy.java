package cs3500.pawnsboard.Strategy;

import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.Player;

import java.util.List;

/**
 * A strategy that chains multiple strategies together.
 * It tries each strategy in order until one returns a valid move.
 */
public class ChainedStrategy implements Strategy {
  private final List<Strategy> strategies;

  /**
   * Constructs a ChainedStrategy using multiple strategies.
   * It tries each strategy in order until a valid move is found.
   *
   * @param strategies The list of strategies to apply in sequence.
   */
  public ChainedStrategy(List<Strategy> strategies) {
    if (strategies == null || strategies.isEmpty()) {
      throw new IllegalArgumentException("Strategies list cannot be null or empty");
    }
    this.strategies = strategies;
  }

  @Override
  public Move chooseMove(ReadonlyPawnsBoardModel model, Player player) {
    for (Strategy strategy : strategies) {
      Move move = strategy.chooseMove(model, player);
      if (move != null) {
        return move; // Return the first successful move
      }
    }
    return null; // No valid moves found
  }
}
