package cs3500.pawnsboard.view;

import cs3500.pawnsboard.Controller.PawnsBoardStubController;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.CellContent;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * A graphical user interface for the Pawns Board game using Java Swing.
 */
public class PawnsBoardGUIViewImpl extends JFrame implements PawnsBoardGUIView {
  private final ReadonlyPawnsBoardModel model;
  public PawnsBoardStubController controller;

  BoardPanel boardPanel;
  private HandPanel handPanel;
  InfoPanel infoPanel;

  int selectedCardIndex = -1;
  int selectedRow = -1;
  int selectedCol = -1;

  /**
   * Constructs a new GUI view for the given model.
   *
   * @param model the model to visualize
   */
  public PawnsBoardGUIViewImpl(ReadonlyPawnsBoardModel model) {
    this.model = model;

    setTitle("Pawns Board Game");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Create panels
    boardPanel = new BoardPanel();
    handPanel = new HandPanel();
    infoPanel = new InfoPanel();

    // Add panels to the frame
    add(boardPanel, BorderLayout.CENTER);
    add(handPanel, BorderLayout.SOUTH);
    add(infoPanel, BorderLayout.NORTH);

    // Set up key listener for confirm/pass
    setupKeyListener();

    // Set the size of the frame
    setSize(800, 600);
    setLocationRelativeTo(null);

    // Create and set a controller
    this.controller = new PawnsBoardStubController(model, this);
  }

  private void setupKeyListener() {
    // Add a key listener to the frame
    this.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (controller != null) {
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // Confirm move
            controller.handleConfirmMove();
          } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Pass turn
            controller.handlePassTurn();
          }
        }
      }
    });

    // Make sure the frame can receive key events
    this.setFocusable(true);
    this.requestFocus();
  }

  @Override
  public void refresh() {
    boardPanel.repaint();
    handPanel.repaint();
    infoPanel.repaint();
  }

  @Override
  public void highlightCard(int cardIndex) {
    this.selectedCardIndex = cardIndex;
    handPanel.repaint();
  }

  @Override
  public void highlightCell(int row, int col) {
    this.selectedRow = row;
    this.selectedCol = col;
    boardPanel.repaint();
  }

  @Override
  public void clearHighlights() {
    this.selectedCardIndex = -1;
    this.selectedRow = -1;
    this.selectedCol = -1;
    boardPanel.repaint();
    handPanel.repaint();
  }

  @Override
  public void displayMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
  }

  /**
   * Panel for displaying the game board.
   */
  class BoardPanel extends JPanel {
    public BoardPanel() {
      setBackground(Color.WHITE);

      // Add mouse listener for cell selection
      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          int clickedRow = getRowFromY(e.getY());
          int clickedCol = getColFromX(e.getX());

          if (clickedRow >= 0 && clickedRow < model.getRows() &&
                  clickedCol >= 0 && clickedCol < model.getColumns()) {
            controller.handleCellClick(clickedRow, clickedCol);
          }
        }
      });
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;

      int rows = model.getRows();
      int cols = model.getColumns();

      int cellWidth = getWidth() / cols;
      int cellHeight = getHeight() / rows;

      // Draw the board
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
          // Determine cell color based on content and selection
          Color cellColor = Color.WHITE;
          if (r == selectedRow && c == selectedCol) {
            cellColor = Color.CYAN;
          }

          // Draw cell background
          g2d.setColor(cellColor);
          g2d.fillRect(c * cellWidth, r * cellHeight, cellWidth, cellHeight);

          // Draw cell border
          g2d.setColor(Color.BLACK);
          g2d.drawRect(c * cellWidth, r * cellHeight, cellWidth, cellHeight);

          // Draw cell content
          drawCellContent(g2d, r, c, cellWidth, cellHeight);
        }
      }

      // Draw row scores
      for (int r = 0; r < rows; r++) {
        int redScore = model.getRowScore(Player.RED, r);
        int blueScore = model.getRowScore(Player.BLUE, r);

        g2d.setColor(Color.RED);
        g2d.drawString(String.valueOf(redScore), 5, r * cellHeight + cellHeight / 2);

        g2d.setColor(Color.BLUE);
        g2d.drawString(String.valueOf(blueScore), getWidth() - 15, r * cellHeight + cellHeight / 2);
      }
    }

    private void drawCellContent(Graphics2D g2d, int row, int col, int cellWidth, int cellHeight) {
      CellContent content = model.getCellContent(row, col);
      Player owner = model.getCellOwner(row, col);

      int x = col * cellWidth;
      int y = row * cellHeight;

      switch (content) {
        case EMPTY:
          // Draw an empty cell
          g2d.setColor(Color.LIGHT_GRAY);
          g2d.drawString("_", x + cellWidth / 2 - 5, y + cellHeight / 2 + 5);
          break;
        case PAWN:
          // Draw pawns
          g2d.setColor(owner == Player.RED ? Color.RED : Color.BLUE);
          int pawnCount = model.getPawnCount(row, col);
          g2d.drawString(String.valueOf(pawnCount), x + cellWidth / 2 - 5, y + cellHeight / 2 + 5);
          break;
        case CARD:
          // Draw card
          drawCard(g2d, row, col, x, y, cellWidth, cellHeight);
          break;
      }
    }

    private void drawCard(Graphics2D g2d, int row, int col, int x, int y, int cellWidth, int cellHeight) {
      Card card = model.getCard(row, col);
      Player owner = model.getCellOwner(row, col);

      if (card != null) {
        // Draw card outline
        g2d.setColor(owner == Player.RED ? new Color(255, 200, 200) : new Color(200, 200, 255));
        g2d.fillRect(x + 2, y + 2, cellWidth - 4, cellHeight - 4);

        g2d.setColor(owner == Player.RED ? Color.RED : Color.BLUE);
        g2d.drawRect(x + 2, y + 2, cellWidth - 4, cellHeight - 4);

        // Draw card name and value
        g2d.drawString(card.getName(), x + 5, y + 15);
        g2d.drawString("" + card.getValueScore(), x + cellWidth / 2 - 5, y + cellHeight / 2 + 5);
      }
    }

    private int getRowFromY(int y) {
      return y / (getHeight() / model.getRows());
    }

    private int getColFromX(int x) {
      return x / (getWidth() / model.getColumns());
    }

    @Override
    public Dimension getPreferredSize() {
      return new Dimension(500, 400);
    }
  }

  /**
   * Panel for displaying the current player's hand.
   */
  class HandPanel extends JPanel {
    public HandPanel() {
      setBackground(Color.LIGHT_GRAY);

      // Add mouse listener for card selection
      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          int clickedCardIndex = getCardIndexFromX(e.getX());
          List<Card> hand = model.getPlayerHand(model.getCurrentPlayer());

          if (clickedCardIndex >= 0 && clickedCardIndex < hand.size()) {
            controller.handleCardClick(clickedCardIndex);
          }
        }
      });
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;

      Player currentPlayer = model.getCurrentPlayer();
      List<Card> hand = model.getPlayerHand(currentPlayer);

      int cardWidth = 100;
      int cardHeight = 150;
      int spacing = 10;

      // Draw the hand title
      g2d.setColor(currentPlayer == Player.RED ? Color.RED : Color.BLUE);
      g2d.setFont(new Font("Arial", Font.BOLD, 14));
      g2d.drawString(currentPlayer + "'s Hand", 10, 20);

      // Draw each card in the hand
      for (int i = 0; i < hand.size(); i++) {
        Card card = hand.get(i);
        int x = i * (cardWidth + spacing) + 10;
        int y = 30;

        // Determine card color based on selection
        Color cardColor = (i == selectedCardIndex) ? Color.CYAN : Color.WHITE;

        // Draw card background
        g2d.setColor(cardColor);
        g2d.fillRect(x, y, cardWidth, cardHeight);

        // Draw card border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, cardWidth, cardHeight);

        // Draw card details
        g2d.drawString(card.getName(), x + 5, y + 15);
        g2d.drawString("Cost: " + card.getCost(), x + 5, y + 30);
        g2d.drawString("Value: " + card.getValueScore(), x + 5, y + 45);

        // Draw influence grid
        drawInfluenceGrid(g2d, card, x, y + 60, currentPlayer);
      }
    }

    private void drawInfluenceGrid(Graphics2D g2d, Card card, int x, int y, Player player) {
      String[] grid = card.getInfluenceGrid();
      int cellSize = 10;

      // For blue player, we need to mirror the influence grid horizontally
      if (player == Player.BLUE) {
        for (int r = 0; r < 5; r++) {
          for (int c = 0; c < 5; c++) {
            // For blue, read the character from the mirrored position
            char ch = grid[r].charAt(4 - c);
            drawInfluenceCell(g2d, ch, x + c * cellSize, y + r * cellSize, cellSize);
          }
        }
      } else {
        // For red player, display the grid as is
        for (int r = 0; r < 5; r++) {
          for (int c = 0; c < 5; c++) {
            char ch = grid[r].charAt(c);
            drawInfluenceCell(g2d, ch, x + c * cellSize, y + r * cellSize, cellSize);
          }
        }
      }
    }

    private void drawInfluenceCell(Graphics2D g2d, char ch, int x, int y, int cellSize) {
      if (ch == 'I') {
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x, y, cellSize, cellSize);
      } else if (ch == 'C') {
        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, cellSize, cellSize);
      } else {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, cellSize, cellSize);
      }

      g2d.setColor(Color.BLACK);
      g2d.drawRect(x, y, cellSize, cellSize);

      // Draw the character in the cell
      g2d.drawString(String.valueOf(ch), x + 2, y + cellSize - 2);
    }

    private int getCardIndexFromX(int x) {
      int cardWidth = 100;
      int spacing = 10;

      return (x - 10) / (cardWidth + spacing);
    }

    @Override
    public Dimension getPreferredSize() {
      return new Dimension(800, 200);
    }
  }

  /**
   * Panel for displaying game information.
   */
  private class InfoPanel extends JPanel {
    public InfoPanel() {
      setBackground(Color.LIGHT_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;

      Player currentPlayer = model.getCurrentPlayer();

      // Draw the current player
      g2d.setColor(currentPlayer == Player.RED ? Color.RED : Color.BLUE);
      g2d.setFont(new Font("Arial", Font.BOLD, 14));
      g2d.drawString("Current Player: " + currentPlayer, 10, 20);

      // Draw the scores
      int redScore = model.getTotalScore(Player.RED);
      int blueScore = model.getTotalScore(Player.BLUE);

      g2d.setColor(Color.RED);
      g2d.drawString("Red Score: " + redScore, 200, 20);

      g2d.setColor(Color.BLUE);
      g2d.drawString("Blue Score: " + blueScore, 350, 20);

      // Draw game over message if applicable
      if (model.isGameOver()) {
        Player winner = model.getWinner();
        g2d.setColor(Color.BLACK);
        if (winner != null) {
          g2d.drawString("Game Over! Winner: " + winner, 500, 20);
        } else {
          g2d.drawString("Game Over! Tie Game", 500, 20);
        }
      }

      // Draw key controls reminder
      g2d.setColor(Color.BLACK);
      g2d.setFont(new Font("Arial", Font.PLAIN, 12));
      g2d.drawString("Press ENTER to confirm move, SPACE to pass turn", 10, 35);
    }

    @Override
    public Dimension getPreferredSize() {
      return new Dimension(800, 40);
    }
  }
}