# **Pawns Board Game**
## **CS3500 Object-Oriented Design Assignment**

#### **Team Members**
- **Mohamed Ibrahim**
- **Anish Machiraju**
- **Ripandeep Kaur**

---

## **Overview**
The **Pawns Board Game** is a strategic turn-based game where two players (**RED** and **BLUE**) compete to maximize their score by placing cards strategically on a board. Players can place pawns, control territories, and influence the board using **cards with unique costs and influence grids**.

This project follows the **MVC (Model-View-Controller)** design pattern:
- **Model**: Manages the game state, enforces rules, and tracks scores.
- **View**: Displays the board state and handles user interactions.
- **Controller**: Handles input from users and AI strategies.

The game includes **computer-controlled AI players** that follow different **strategy-based decision-making approaches**.

---

## **Model Overview (`cs3500.pawnsboard.model`)**
The **Model** maintains the game state, enforces the game rules, and provides player interactions. The key features include:

- **Board Representation**: A **grid-based** system with `Cell` objects that contain **pawns, cards, or empty spaces**.
- **Turn-Based Play**: Players take turns playing cards or passing.
- **Deck & Hand Management**: Each player has a **deck** and a **hand** from which they select cards to play.
- **Scoring System**: Players gain points based on **card placements** and **territory control**.
- **Influence System**: Cards modify board spaces using **predefined influence grids**.
- **Game End Conditions**: The game ends when neither player can make a valid move.

---

## **Class Overview**
| **Class/Interface**       | **Description** |
|---------------------------|----------------|
| `PawnsBoardModel` (Interface) | Defines core **game operations** like card placement, passing, and scoring. |
| `PawnsBoardModelImpl` | Implements the **game logic** and enforces turn-based rules. |
| `Card` (Interface) | Represents a card’s attributes (**name, cost, value, influence grid**). |
| `CardImp` | Implements `Card`. Represents an **immutable card object** with influence mechanics. |
| `Cell` | Represents a **board cell**, which may contain **pawns or a placed card**. |
| `DeckConfigReader` | Loads deck configurations from external files. |
| `Game` | Manages overall **gameplay flow** and determines the winner. |

---

## **Class Invariants**
Class invariants ensure **data integrity** and **logical consistency** throughout the game.

### **Enforced Rules in `PawnsBoardModelImpl`**
1. **Deck Size Constraint**
    - Each player’s deck must have **at least enough cards** to potentially fill the board.
   ```java
   if (redDeck.size() < rows * cols || blueDeck.size() < rows * cols) {
       throw new IllegalArgumentException("Each deck must have enough cards to fill the board.");
   }
   ```
2. **Turn-Based Play**
    - Players **must** take turns. No player can move twice in a row.
3. **Influence Grid Validity**
    - Each card must have a **valid influence pattern** and at least **one center (`C`)** in the 5x5 grid.
4. **Legal Move Validation**
    - A card **cannot be played** unless the player has enough pawns to cover the cost.

---

## **View Implementation (`cs3500.pawnsboard.view`)**
The **View** provides a **graphical user interface (GUI)** to visualize the game state.

### **Features:**
✔ **Clickable Board:** Players can select board cells with mouse clicks.  
✔ **Hand Selection:** Players can highlight cards before playing them.  
✔ **Keyboard Input:**
- Press `Enter` to **confirm** a move.
- Press `Space` to **pass** the turn.  
  ✔ **Resizing Support:** The GUI remains **functional** even when resized.  
  ✔ **Game Over Display:** The view **automatically detects and displays** when the game ends.

### **Class Overview**
| **Class**                 | **Description** |
|---------------------------|----------------|
| `PawnsBoardGUIViewImpl` | Implements the **graphical user interface** for the game. |
| `BoardPanel` | Handles **board rendering** and cell selection. |
| `HandPanel` | Displays the player’s **current hand** and highlights selections. |
| `InfoPanel` | Displays **game messages** and the current player's turn. |

---

## **Controller (`cs3500.pawnsboard.controller`)**
The **Controller** processes user inputs and communicates with the **model** and **view**.

### **Responsibilities:**
- Listens to **mouse clicks** on the board and cards.
- Handles **keyboard events** (`Enter` to confirm, `Space` to pass).
- Updates the **game state** based on player actions.

### **Stub Controller**
To support automated testing, we implemented a **Stub Controller** that:
- Logs user interactions (`System.out` messages).
- Ensures **valid move selection** and **turn order compliance**.

---

## **AI Strategies (`cs3500.pawnsboard.Strategy`)**
This game includes **computer-controlled players** that follow different strategies to decide moves.

### **Implemented Strategies**
| **Strategy**             | **Description** |
|--------------------------|----------------|
| `FillFirstStrategy` | Chooses the **first valid** move. |
| `MaximizeRowScoreStrategy` | Selects a move that **maximizes** the player's row score. |
| `ControlBoardStrategy` | Prioritizes moves that **increase board control**. |
| `MinimaxStrategy` (Extra Credit) | Predicts the **opponent's best move** and minimizes its impact. |
| `ChainedStrategy` (Extra Credit) | **Combines multiple strategies**, using the first that provides a valid move. |

---

## **Extra Credit Implementations**
We extended the project with **advanced AI strategies**:

✔ **Control Board Strategy:** Selects moves that maximize **territory control**.  
✔ **Minimax Strategy:** Uses a **recursive decision-making approach** to **minimize the opponent’s best move**.  
✔ **Chained Strategy:** Allows **strategy combinations**, making AI **more flexible**.

### **Extra Credit Tests**
- **`testControlBoardStrategy()`** verifies that AI prioritizes board control.
- **`testMinimaxStrategy()`** ensures AI selects moves that minimize the opponent's strongest play.
- **`testChainedStrategy()`** confirms AI falls back to secondary strategies if the first one fails.

---

## **Running the Game**
### **Compile & Run**
```sh
javac -d bin $(find src -name "*.java")
java -cp bin cs3500.pawnsboard.PawnsBoardGame
```

### **Screenshots for Submission**
The game was tested and **screenshots were taken** at various states:
1. **Start of Game**
2. **Card and Cell Selected (Red Player's Turn)**
3. **Card Selected (Blue Player's Turn)**
4. **Mid-Game State with Several Played Cards**

These screenshots are available in the `screenshots/` directory.

---

## **Testing**
### **Unit Tests**
The project includes **JUnit tests** for:
- **Model Behavior**
- **View Rendering**
- **AI Strategies**
- **Controller Input Handling**

### **Run Tests**
```sh
javac -cp "lib/junit-4.13.2.jar:bin" -d bin $(find test -name "*.java")
java -cp "lib/junit-4.13.2.jar:bin" org.junit.runner.JUnitCore cs3500.pawnsboard.Strategy.StrategyTest
```

---

## **Future Improvements**
- **Enhanced AI:** Implement **adaptive learning** to improve strategy over time.
- **Network Multiplayer:** Enable remote gameplay with **client-server communication**.
- **Card Customization:** Allow **players to create their own cards** with unique effects.

---

## **Acknowledgments**
This project was developed as part of **Northeastern University’s CS3500** course on **Object-Oriented Design**.

