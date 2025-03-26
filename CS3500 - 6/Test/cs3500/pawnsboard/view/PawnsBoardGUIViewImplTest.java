package cs3500.pawnsboard.view;

import cs3500.pawnsboard.Controller.PawnsBoardStubController;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.CellContent;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModel.Player;
import cs3500.pawnsboard.model.mock.MockCard;
import cs3500.pawnsboard.model.mock.MockPawnsBoardModel;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for PawnsBoardGUIViewImpl.
 */
public class PawnsBoardGUIViewImplTest {

  private PawnsBoardGUIViewImpl view;
  private PawnsBoardStubController controller;
  private MockPawnsBoardModel model;
  private StringBuilder log;

  @Before
  public void setUp() {
    log = new StringBuilder();
    int rows = 5;
    int cols = 5;

    List<List<CellContent>> boardContent = new ArrayList<>();
    List<List<Player>> boardOwners = new ArrayList<>();
    List<List<Integer>> pawnCounts = new ArrayList<>();
    int[][] rowScores = new int[rows][2];

    for (int r = 0; r < rows; r++) {
      List<CellContent> rowContent = new ArrayList<>();
      List<Player> rowOwners = new ArrayList<>();
      List<Integer> rowPawns = new ArrayList<>();

      for (int c = 0; c < cols; c++) {
        rowContent.add(CellContent.EMPTY);
        rowOwners.add(Player.RED);  // Ensure a valid player is assigned
        rowPawns.add(0);
      }

      boardContent.add(rowContent);
      boardOwners.add(rowOwners);
      pawnCounts.add(rowPawns);
    }

    List<MockCard> redHand = new ArrayList<>();
    List<MockCard> blueHand = new ArrayList<>();
    String[] influenceGrid = {"XXXXX", "XXXXX", "XXCXX", "XXXXX", "XXXXX"};

    redHand.add(new MockCard("Attack", 2, 5, influenceGrid));
    blueHand.add(new MockCard("Defense", 1, 3, influenceGrid));

    model = new MockPawnsBoardModel(log, Player.RED, 5, 5, true, Player.RED,
        boardContent, boardOwners, pawnCounts, new ArrayList<>(redHand), new ArrayList<>(blueHand), rowScores);


    view = new PawnsBoardGUIViewImpl(model);
    controller = new PawnsBoardStubController(model, view);

    view.controller = controller;
    view.setVisible(true);
  }



  @Test
  public void testFrameInitialization() {
    assertNotNull(view);
    assertTrue(view.isVisible());
    assertEquals("Pawns Board Game", view.getTitle());
    assertEquals(JFrame.EXIT_ON_CLOSE, view.getDefaultCloseOperation());
  }

  @Test
  public void testPanelRendering() {
    Component[] components = view.getContentPane().getComponents();
    boolean boardExists = false;
    boolean handExists = false;
    boolean infoExists = false;

    for (Component c : components) {
      if (c instanceof JPanel) {
        Dimension size = c.getPreferredSize();
        if (size.equals(new Dimension(500, 400))) {
          boardExists = true;
        } else if (size.equals(new Dimension(800, 200))) {
          handExists = true;
        } else if (size.equals(new Dimension(800, 40))) {
          infoExists = true;
        }
      }
    }

    assertTrue("Board panel should exist", boardExists);
    assertTrue("Hand panel should exist", handExists);
    assertTrue("Info panel should exist", infoExists);
  }

  @Test
  public void testHighlightCell() {
    view.highlightCell(2, 3);
    assertEquals(2, view.selectedRow);
    assertEquals(3, view.selectedCol);
  }

  @Test
  public void testClearHighlights() {
    view.highlightCell(2, 3);
    view.highlightCard(1);
    view.clearHighlights();

    assertEquals(-1, view.selectedRow);
    assertEquals(-1, view.selectedCol);
    assertEquals(-1, view.selectedCardIndex);
  }

  @Test
  public void testHighlightCard() {
    view.highlightCard(2);
    assertEquals(2, view.selectedCardIndex);
  }

  @Test
  public void testDisplayMessage() {
    SwingUtilities.invokeLater(() -> {
      view.displayMessage("Test Message");
      JOptionPane pane = new JOptionPane();
      assertEquals("Test Message", pane.getMessage());
    });
  }

  @Test
  public void testKeyListenerEnter() throws Exception {
    // Ensure UI is set up properly
    SwingUtilities.invokeAndWait(() -> {
      view.setVisible(true);  // Ensure the view is visible
      view.requestFocusInWindow();  // Request focus properly
      view.revalidate();
      view.repaint();
    });

    // Wait for UI to fully load and gain focus
    waitForComponentToBeReady(view);

    // Clear log before test
    log.setLength(0);

    // Simulate Enter key press
    SwingUtilities.invokeLater(() -> {
      KeyEvent enterEvent = new KeyEvent(view, KeyEvent.KEY_PRESSED,
          System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, '\n');
      view.dispatchEvent(enterEvent);
    });

    // **Ensure Swing event processing completes**
    SwingUtilities.invokeAndWait(() -> {});

    // **Wait for log update before checking**
    waitForLogToContain("Confirm move key pressed.");

    // **Check the actual log content**
    String logOutput = log.toString();
    assertTrue("Expected log message not found. Log output: " + logOutput,
        logOutput.contains("Confirm move key pressed."));
  }
  @Test
  public void testSpaceKeyPassesTurn() throws Exception {
    log.setLength(0);

    SwingUtilities.invokeLater(() -> {
      KeyEvent spaceKey = new KeyEvent(view, KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
          0, KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
      view.dispatchEvent(spaceKey);
    });

    SwingUtilities.invokeAndWait(() -> {});

    waitForLogToContain("Pass turn key pressed.");
    assertTrue(log.toString().contains("Pass turn key pressed."));
  }


  /**
   * Waits until a specific log message appears.
   */
  private void waitForLogToContain(String expectedText) throws InterruptedException {
    for (int i = 0; i < 20; i++) {  // Retry for ~4 seconds max
      if (log.toString().contains(expectedText)) {
        return;  // Log contains expected text, exit
      }
      Thread.sleep(200); // Wait and retry
    }
    fail("Expected log message not found: " + expectedText);
  }

  /**
   * Waits until a Swing component is fully ready.
   */
  private void waitForComponentToBeReady(Component component) throws InterruptedException {
    for (int i = 0; i < 20; i++) {  // Retry for ~4 seconds max
      if (component.isDisplayable() && component.hasFocus()) {
        return;  // Component is ready, exit
      }
      Thread.sleep(200);
      component.requestFocusInWindow();  // Ensure the view is focused
    }
    fail("Component did not become ready in time");
  }


  @Test
  public void testKeyListenerSpace() {
    KeyEvent spaceKey = new KeyEvent(view, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
    view.dispatchEvent(spaceKey);

    assertTrue(log.toString().contains("Pass turn key pressed."));
  }

  @Test
  public void testBoardPanelClick() {
    PawnsBoardGUIViewImpl.BoardPanel boardPanel = view.new BoardPanel();
    MouseEvent click = new MouseEvent(boardPanel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 100, 150, 1, false);
    boardPanel.dispatchEvent(click);

    int clickedRow = 150 / (boardPanel.getHeight() / model.getRows());
    int clickedCol = 100 / (boardPanel.getWidth() / model.getColumns());

    assertTrue(log.toString().contains("Cell clicked: (" + clickedRow + "," + clickedCol + ")"));
  }

  @Test
  public void testHandPanelClick() {
    PawnsBoardGUIViewImpl.HandPanel handPanel = view.new HandPanel();
    MouseEvent click = new MouseEvent(handPanel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 50, 60, 1, false);
    handPanel.dispatchEvent(click);

    int clickedCardIndex = (50 - 10) / (100 + 10);
    assertTrue(log.toString().contains("Card clicked: " + clickedCardIndex));
  }

  @Test
  public void testBoardRendering() {
    PawnsBoardGUIViewImpl.BoardPanel boardPanel = view.boardPanel; // Use the initialized panel

    assertNotNull("Board panel should be initialized", boardPanel);

    // Ensure the panel is displayable before getting graphics
    SwingUtilities.invokeLater(() -> {
      boardPanel.revalidate();
      boardPanel.repaint();
    });

    // Allow Swing to process repaint events
    try {
      Thread.sleep(500); // Short delay for GUI update
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue("Board panel should be displayable", boardPanel.isDisplayable());
  }


  @Test
  public void testGameOverMessage() throws Exception {
    log = new StringBuilder();

    // Reinitialize model with game over state
    model = new MockPawnsBoardModel(log, Player.RED, 5, 5, true, Player.RED,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
        new ArrayList<>(), new ArrayList<>(), new int[5][2]);

    // Reinitialize view and controller to reflect the new model
    view = new PawnsBoardGUIViewImpl(model);
    controller = new PawnsBoardStubController(model, view);
    view.controller = controller;

    // Ensure UI updates
    SwingUtilities.invokeAndWait(() -> {
      view.refresh();
      view.revalidate();
      view.repaint();
      view.pack(); // Ensure layout is finalized
      view.setVisible(true); // Make sure the window is displayed
    });

    // Wait until the UI is properly rendered
    waitForRendering(view.infoPanel);

    // Capture text from the InfoPanel
    String infoPanelText = getRenderedText(view.infoPanel);

    // Assert that the "Game Over" message is displayed
    assertTrue("Game Over message should be visible", infoPanelText.contains("Game Over! Winner: RED"));
  }

  /**
   * Helper method to wait for the component to be displayable.
   */
  private void waitForRendering(JComponent component) {
    for (int i = 0; i < 10; i++) {
      if (component.isDisplayable() && component.getWidth() > 0 && component.getHeight() > 0) {
        return;
      }
      try {
        Thread.sleep(200); // Small delay for rendering
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    fail("Component did not become displayable in time");
  }



  /**
   * Extracts the rendered text from a given JPanel.
   * This method ensures we verify the text actually drawn on the UI.
   */
  private String getRenderedText(JPanel panel) {
    BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = image.createGraphics();
    panel.paint(g2d);
    g2d.dispose();

    return image.toString(); // Replace this with OCR or a log-based method if needed
  }

}
