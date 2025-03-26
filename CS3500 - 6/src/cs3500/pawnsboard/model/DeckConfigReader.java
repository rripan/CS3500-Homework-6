package cs3500.pawnsboard.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A utility class for reading deck configurations from files.
 * This class is responsible for parsing deck configuration files and creating Card objects.
 */
public class DeckConfigReader {

  /**
   * Reads a deck configuration from a file.
   *
   * @param filePath the path to the deck configuration file
   * @return a list of cards defined in the file
   * @throws IllegalArgumentException if the file cannot be read or contains invalid data
   */
  public static List<Card> readDeck(String filePath) {
    File file = new File(filePath);
    List<Card> deck = new ArrayList<>();
    if (!file.exists()) {
      System.err.println("Error: Deck file not found: " + filePath);
      return new ArrayList<>(); // Return an empty list instead of throwing an error
    }
    try (Scanner scanner = new Scanner(file)) {
      // Continue reading cards until the end of file
      while (scanner.hasNextLine()) {
        deck.add(readCard(scanner));
      }

      return deck;
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("File not found: " + filePath, e);
    }
  }

  /**
   * Reads a single card definition from the scanner.
   *
   * @param scanner the scanner to read from
   * @return a new Card object
   * @throws IllegalArgumentException if the card definition is invalid
   */
  private static Card readCard(Scanner scanner) {
    try {
      // Read the card metadata line
      String metadataLine = scanner.nextLine();
      String[] parts = metadataLine.split("\\s+");

      if (parts.length < 3) {
        throw new IllegalArgumentException("Invalid card metadata: " + metadataLine);
      }

      String name = parts[0];
      int cost;
      int valueScore;

      try {
        cost = Integer.parseInt(parts[1]);
        valueScore = Integer.parseInt(parts[2]);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid cost or value score: " + metadataLine);
      }

      // Read the influence grid (5 rows)
      String[] influenceGrid = new String[5];
      for (int i = 0; i < 5; i++) {
        if (scanner.hasNextLine()) {
          influenceGrid[i] = scanner.nextLine();
        } else {
          throw new IllegalArgumentException("Incomplete influence grid for card: " + name);
        }
      }

      // Create and return the card
      return new CardImp(name, cost, valueScore, influenceGrid);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error reading card: " + e.getMessage(), e);
    }
  }
}