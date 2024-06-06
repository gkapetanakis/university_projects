package gr.ntua.medialab.application.components.minesweeper;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

// getters that return primitive values are public
// getters that return objects are package-only, or return read-only objects
// for the minefield's attributes, i only created the required getters (getMineCount(), getClicksCount())
// and the method revealAllMines()

public class Minesweeper {
    private static final URL viewURL = Minesweeper.class.getResource("fxml/MinesweeperView.fxml");
    // attributes
    private final Minefield minefield;
	private final int timeLimit;

    // state
    private Timer timer;
    private final ReadOnlyIntegerWrapper remainingTime = new ReadOnlyIntegerWrapper(1);
    private final ReadOnlyBooleanWrapper gameStarted = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper gameEnded = new ReadOnlyBooleanWrapper(false);

    /**
     * Creates a new instance of the object representing the minesweeper game with the given arguments.
     * To start the game, a view should be created and (primary/left) clicked.
     * The game is meant to be played through a GUI, so clicking on the view is the only way to play it.
     * @param rowCount the number of rows of the game's grid
     * @param colCount the number of columns of the game's grid
     * @param mineCount the number of hidden mines
     * @param hasSuperMine whether one of the mines is a super mine or not
     * @param timeLimit the total time available to reveal all the empty cells after the game starts
     * @see #createView()
     */
    public Minesweeper(int rowCount, int colCount, int mineCount, boolean hasSuperMine, int timeLimit) {
        this.minefield = new Minefield(rowCount, colCount, mineCount, hasSuperMine);
        this.timeLimit = timeLimit;

        // the game starts when the first primary click happens
        gameStarted.bind(minefield.firstClickHappenedProperty());
        // the game will only ever start once, so no need to check if newValue is true
        gameStarted.addListener((observable, oldValue, newValue) -> start());

        // the game ends when either a mine detonates, or all empty cells are revealed, or the timer reaches 0
        gameEnded.bind(
                minefield.mineDetonatedProperty().or(
                minefield.allEmptyCellsRevealedProperty()).or(
                remainingTime.lessThanOrEqualTo(0))
        );
        // the game will only ever end once, so no need to check if newValue is true
        gameEnded.addListener((observable, oldValue, newValue) -> end());
    }

    Minefield getMinefield() {
        return minefield;
    }

    void start() {
        remainingTime.set(timeLimit);
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> remainingTime.set(remainingTime.get() - 1));
                    }
                },
                1000, // begin after 1000ms
                1000  // repeat every 1000ms
        );
    }

    void end() {
        // delete the timer after cancelling it just in case it will ever get reused,
        // as calling start() after calling cancel() on the same timer causes an exception to be thrown
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        minefield.revealAllMines();
    }

    /**
     * Reveals all not-yet-revealed mines of the minefield if the game has started.
     * Causes the game to be over and considered lost.
     * Does nothing if the game has not started yet.
     */
    public void revealAllMines() {
        minefield.revealAllMines();
    }

    /**
     * Returns the number of mines in the minefield.
     * @return the number of mines in the minefield.
     */
    public int getMineCount() {
        return minefield.getMineCount();
    }

    /**
     * Returns the number of primary/left clicks that have caused a change in the state of the game.
     * @return the number of primary/left clicks that have caused a change in the state of the game.
     */
    public int getClicksCount() {
        return minefield.getClicksCount();
    }

    /**
     * Returns the total available time to reveal all the empty cells after the game starts.
     * @return the total available time to reveal all the empty cells after the game starts.
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * Returns the remaining time available to reveal all the empty cells after a game has started.
     * Calling this method before calling starting the game produces undefined results.
     * @return the remaining time available to reveal all the empty cells after a game has started.
     * An undefined integer if the game hasn't started.
     */
    public int getRemainingTime() {
        return remainingTime.get();
    }

    /**
     * Returns true if the game has started.
     * @return true if the game has started.
     */
    public boolean gameHasStarted() {
        return gameStarted.get();
    }

    /**
     * Returns true if the game has ended.
     * @return true if the game has ended.
     */
    public boolean gameHasEnded() {
        return gameEnded.get();
    }

    /**
     * Returns an integer property whose value is equal to the remaining time available to reveal
     * all the empty cells after a game has started.
     * The value of the property before starting the game is undefined.
     * @return an integer property whose value is equal to the remaining time available to reveal
     * all the empty cells after a game has started.
     */
    public ReadOnlyIntegerProperty remainingTimeProperty() {
        return remainingTime.getReadOnlyProperty();
    }

    /**
     * Returns a boolean property whose value indicates whether the game has started or not.
     * @return a boolean property whose value indicates whether the game has started or not.
     */
    public ReadOnlyBooleanProperty gameHasStartedProperty() {
        return gameStarted.getReadOnlyProperty();
    }

    /**
     * Returns a boolean property whose value indicates whether the game has ended or not.
     * @return a boolean property whose value indicates whether the game has ended or not.
     */
    public ReadOnlyBooleanProperty gameHasEndedProperty() {
        return gameEnded.getReadOnlyProperty();
    }

    /**
     * Returns a boolean indicating if the player won or not.
     * Calling this method before the game has ended always returns false.
     * @return a boolean indicating if the player won or not.
     */
    public boolean playerWon() {
        return gameEnded.get() &&
                (!minefield.mineDetonatedProperty().get() && minefield.allEmptyCellsRevealedProperty().get());
    }

    /**
     * Creates and returns a view of the game, which responds to mouse inputs.
     * Primary/left clicking on the view for the first time causes the game to start.
     * The view is created by loading an FXML file.
     * Each call of this method returns a new object, but all view objects created from the same minesweper object
     * are synchronized, as they are backed by the same game state.
     * This method is NOT a getter.
     * @return a view of the game, which responds to mouse inputs.
     * @throws RuntimeException if the view's FXML file fails to load.
     */
    public Node createView() throws RuntimeException {
        try {
            var loader = new FXMLLoader();
            var controller = new MinesweeperController();
            loader.setLocation(viewURL);
            loader.setController(controller);

            var view = loader.load();
            controller.bindViewToModel(this);

            return (Node) view;
        } catch (IOException e) {
            throw new RuntimeException("Could not load the Minesweeper View");
        }
    }
}
