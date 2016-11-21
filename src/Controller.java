/**
 * This is the Controller class for the 8-Tiles game. It extends Initializable so that we may initialize our board upon startup.
 * Our GUI contains a Solve button, Set Grid button, Start New Game button, Exit button, and of course the Grid buttons.
 * In our functions, we call the back-end (the Board class & the SearchTree class) functions and update the interface as needed.
 *
 * The @FXML Tags are used to inject information from the .fxml file into our Controller.
 */

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    int buttonsSet = 0;
    int numMoves = 0;
    boolean manualSet = false;
    Board board;

    @FXML
    Button solve, setGrid, exitButton, startNewGameButton;
    @FXML
    Label numMovesLabel;
    @FXML
    ArrayList<Button> buttons;

    /**
     * Prepares all the buttons for the GUI
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetGridForNewGame();
        prepareSolveButton();
        prepareExitButton();
        prepareSetGridButton();
        prepareStartNewButton();
    }

    /**
     * Sets up the board to play a new game
     */
    public void resetGridForNewGame() {
        board = new Board(1);
        buttonsSet = 0;
        numMoves = 0;
        displayCurrentNumberOfMoves();
        board = new Board(1);
        prepareAllGridButtons();
    }

    /**
     * Displays the current number of moves
     */
    public void displayCurrentNumberOfMoves() {
        numMovesLabel.setText("Number of moves: " + numMoves);
    }

    /**
     * Calls configureGridButton on all buttons in our buttons ArrayList
     */
    public void prepareAllGridButtons() {
        for (int i = 0; i < board.grid.length; i++) {
            for (int j = 0; j < board.grid[0].length; j++) {
                int buttonVal = board.grid[i][j];
                Button button = buttons.get(buttonsSet++);
                configureGridButton(button, buttonVal);
            }
        }
    }

    /**
     * Adds the desired style to the Grid Button and then adds the Button Value to its text. Sets visibility based on text.
     * @param button
     * @param buttonVal
     */
    public void configureGridButton(Button button, int buttonVal) {
        button.setStyle("-fx-font:22 system; -fx-base: #5477af");
        button.setText(Integer.toString(buttonVal));
        setButtonVisibility(button, buttonVal);
        button.setOnAction(event -> gridButtonHandler(event));
    }

    /**
     * Sets the style of the Exit button and then adds the appropriate button handler which will exit the game upon click.
     */
    public void prepareExitButton() {
        exitButton.setStyle("-fx-font: 15 system; -fx-base: #ff0000;");
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        });
    }

    /**
     * Clears out all the values of the GridButtons
     */
    public void prepareSetGridButton() {
        setGrid.setOnAction(event -> displayBlankGrid());
    }

    /**
     * Adds the resetGridForNewGame as a handler for the Start New Button
     */
    public void prepareStartNewButton() {
        startNewGameButton.setOnAction(event -> resetGridForNewGame());
    }

    /**
     * remaps the values of a given Board to the buttons
     * if the board is solved, displays victory prompt
     * @param aBoard
     */
    public void updateButtons(Board aBoard) {
        remapButtons(aBoard);
        if (aBoard.isSolved()) {
            victoryPrompt();
        }
    }

    /**
     * Displays the victory dialog
     */
    public static void victoryPrompt() {
        Dialog dialog = new Dialog();
        dialog.setTitle("Congratulations!");
        dialog.setContentText("You solved the board!");
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE));
        dialog.show();
    }

    /**
     * Given a board, updates the values of the buttons
     * @param aBoard
     */
    public void remapButtons(Board aBoard) {
        int buttonsChecked = 0;
        for (int i = 0; i < aBoard.grid.length; i++) {
            for (int j = 0; j < aBoard.grid[0].length; j++) {
                int buttonVal = aBoard.grid[i][j];
                Button button = buttons.get(buttonsChecked++);
                button.setText(Integer.toString(buttonVal));
                setButtonVisibility(button,buttonVal);
            }
        }
    }

    /**
     * Updates the values of the global board to the values of the buttons
     */
    public void remapBoardValues() {
        int buttonCounter = 0;
        board.mapFromIntegerToCoord.clear();
        for (int i = 0; i < board.grid.length; i++) {
            for (int j = 0; j < board.grid[0].length; j++) {
                Button b = buttons.get(buttonCounter++);
                int num = Integer.parseInt(b.getText());
                board.grid[i][j] = num;
                board.mapFromIntegerToCoord.put(num, new Coordinate(i, j));
            }
        }

    }

    /**
     * Displays the prompt letting the user know their board is unsolvable
     */
    private void unsolvablePrompt() {
        Dialog dialog = new Dialog();
        dialog.setTitle("Woops!");
        dialog.setContentText("Looks like that board was unsolvable, displaying the best found solution.");
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE));
        try {
            dialog.showAndWait();
        } catch (Exception e) {
        }
    }

    /**
     * Given a SearchTree, will either display a step by step solution to the solved board from the current board configuration
     * @param searchTree
     */
    private void playSolveAnimation(SearchTree searchTree) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(300), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateButtons(searchTree.getNextBoard());
                remapBoardValues();
                updateNumberOfMovesAndDisplay();
            }
        }));
        timeline.setCycleCount(searchTree.path.size());
        timeline.playFromStart();
    }

    /**
     * adds 1 to the current number of moves and displays it
     */
    public void updateNumberOfMovesAndDisplay() {
        numMovesLabel.setText("Number of moves: " + ++numMoves);
    }

    /**
     * Adds the button handler to the solve button that will call autoSolve() on the global board and
     * if the board is unsolvable (a heuristic greater than 0) will let the user know and display the best found board
     * otherwise, displays the animation to a step-by-step solution to the solved board from the current board configuration
     */
    public void prepareSolveButton() {
        solve.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SearchTree searchTree = board.autoSolve();
                searchTree.createPath();
                if (searchTree.bestBoardHeuristic > 0) {
                    unsolvablePrompt();
                    updateButtons(searchTree.bestBoardFound);
                    remapBoardValues();
                } else {
                    playSolveAnimation(searchTree);
                }
            }
        });
        solve.setStyle("-fx-font: 15 system; -fx-base: #b6e7c9;");
    }

    /**
     * Removes all values from grid buttons
     */
    public void displayBlankGrid() {
        buttonsSet = 0;
        numMoves = 0;
        displayCurrentNumberOfMoves();
        manualSet = true;
        for (Button b : buttons) {
            b.setText("");
            b.setVisible(true);
        }

    }

    /**
     * The button handler for grid buttons. If manualSet is desired, will give the next clicked button the value of buttonsSet + 1
     * and then reset the value of manualSet to false
     * otherwise will move the clicked button to the empty slot if it is a valid move and display the updated number of moves
     * @param event
     */
    public void gridButtonHandler(ActionEvent event) {
        Button b = (Button) event.getSource();
        if (!manualSet && buttonsSet == 9) {
            int move = Integer.parseInt(b.getText());
            if (board.isValidMove(move)) {
                board.makeMove(move);
                updateNumberOfMovesAndDisplay();
                updateButtons(board);
            }
        } else if (b.getText().length() == 0) {
            if (buttonsSet == 0)
                b.setVisible(false);
            b.setText(Integer.toString(buttonsSet++));
            if (buttonsSet == 9) {
                manualSet = false;
                remapBoardValues();
            }
        }
    }

    /**
     * If the value of a button is 0, will make it invisible.
     * @param button
     * @param buttonVal
     */
    public void setButtonVisibility(Button button, int buttonVal) {
        if (buttonVal == 0) {
            button.setVisible(false);
        } else {
            button.setVisible(true);
        }
    }



}
