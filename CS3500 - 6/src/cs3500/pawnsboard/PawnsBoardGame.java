package cs3500.pawnsboard;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.DeckConfigReader;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.PawnsBoardModelImpl;
import cs3500.pawnsboard.view.PawnsBoardGUIView;
import cs3500.pawnsboard.view.PawnsBoardGUIViewImpl;

import java.util.List;

/**
 * Main class to run the Pawns Board game.
 */
public final class PawnsBoardGame {

  /**
   * Main method to run the Pawns Board game.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    // Create an example model
    PawnsBoardModel model = new PawnsBoardModelImpl();

    // Use default deck configurations or read from file
    String redDeckPath = "docs/red_deck.txt";
    String blueDeckPath = "docs/blue_deck.txt";

    try {
      // Read the deck configurations
      List<Card> redDeck = DeckConfigReader.readDeck(redDeckPath);
      List<Card> blueDeck = DeckConfigReader.readDeck(blueDeckPath);

      // Initialize and start the game
      model.initGame(5, 7, redDeck, blueDeck, 3);
      model.startGame();

      // Create the view
      PawnsBoardGUIView view = new PawnsBoardGUIViewImpl(model);

      // Make the view visible
      view.setVisible(true);

    } catch (Exception e) {
      System.err.println("Error starting game: " + e.getMessage());
      e.printStackTrace();
    }
  }
}