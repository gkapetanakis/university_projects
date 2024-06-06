package gr.ntua.medialab.application.components.minesweeper;

import gr.ntua.medialab.application.Settings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// all getters that return primitive values are public
// all getters that return objects are package-only, or return read-only objects
// all setters are package-only

public class Minefield {
    private static final URL viewURL = Minefield.class.getResource("fxml/MinefieldView.fxml");
    // attributes
    private final int rowCount;
    private final int colCount;
    private final int mineCount;
    private final boolean hasSuperMine;
    // how long (in successful primary clicks) the super mines retain their ability
    private final int superMineLifetime = Settings.GAME_SUPER_MINE_ACTIVE_FOR();
    private final int maxConcurrentMarkedCellsCount;
    // if true, empty cells without adjacent mines revealed by marking a super mine also cause recursive reveal
    private final boolean superMineActionRevealsEmptyCells = Settings.GAME_SUPER_MINE_PROCS_RECURSIVE_REVEAL();
    private final List<List<Cell>> cells = new ArrayList<>();

    // state
    private int clicksCount = 0;
    private final ReadOnlyBooleanWrapper firstClickHappened = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyIntegerWrapper markedCellsCount = new ReadOnlyIntegerWrapper(0);
    private final ReadOnlyBooleanWrapper mineDetonated = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper allEmptyCellsRevealed = new ReadOnlyBooleanWrapper(false);

    // methods
    public Minefield(int rowCount, int colCount, int mineCount, boolean hasSuperMine) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.mineCount = mineCount;
        this.hasSuperMine = hasSuperMine;
        this.maxConcurrentMarkedCellsCount = mineCount;

        // create the cells
        for (int row = 0; row < rowCount; ++row) {
            cells.add(new ArrayList<>());
            for (int col = 0; col < colCount; ++col) {
                var cell = new Cell(row, col);
                // increment and decrement the markedCellsCount automatically
                cell.markedProperty().addListener((observable, oldValue, newValue) ->
                    markedCellsCount.set(markedCellsCount.get() + ((newValue) ? 1 : -1))
                );
                cells.get(row).add(cell);
            }
        }
    }

    // place mines randomly, while ensuring that the area around the given coordinates remains safe
    // this ensures that the first clicked cell is always empty and has no adjacent mines
    void placeMines(int firstClickRowIndex, int firstClickColIndex) {
        var rng = new Random(); // to generate coordinates
        int minesRemaining = mineCount;
        while (minesRemaining > 0) {
            int row, col;
            do {
                row = rng.nextInt(rowCount);
                col = rng.nextInt(colCount);
            } while (row >= firstClickRowIndex - 1 && row <= firstClickRowIndex + 1 &&
                    col >= firstClickColIndex - 1 && col <= firstClickColIndex + 1);

            var cell = getCell(row, col);
            if (cell.trySetMine(hasSuperMine && minesRemaining == mineCount)) {
                // trySetMine() returns true if successful
                --minesRemaining;
                // update adjacent cells
                for (int adjRow = row - 1; adjRow <= row + 1; ++adjRow) {
                    for (int adjCol = col - 1; adjCol <= col + 1; ++adjCol) {
                        if (adjRow >= 0 && adjRow < rowCount && adjCol >= 0 && adjCol < colCount) {
                            var adjCell = getCell(adjRow, adjCol);
                            adjCell.incrementAdjacentMinesCount(1);
                        }
                    }
                }
            }
        }

        createPropertyBindings();
    }

    // create a file with the coordinates of each mine, plus some info on which mines are super
    void writeMinesToFile() {
        var file = new File(Settings.SOLUTION_FOLDER(), Settings.GAME_MINE_POSITIONS_FILENAME());
        try (var writer = new FileWriter(file)) { // writer auto-closes when the block is over
            for (var row : cells)
                for (var cell : row) {
                    if (!cell.hasMine()) continue;
                    writer.write(
                            cell.getRow() + " " +
                            cell.getCol() + " " +
                            (cell.getMine().isSuper() ? "1" : "0") + "\n"
                    );
                }
        } catch (IOException e) {
            // if any exceptions occur, the game should not be disrupted, so only a diagnostic is outputted
            System.err.println("Could not open file '" + file.getName() + "' for writing");
        }
    }

    // creates the bindings of the detonatedMine and the allEmptyCellsRevealed properties,
    // so they can update automatically
    // gets called automatically after all mines are placed (in placeMines())
    private void createPropertyBindings() {
        BooleanExpression detonatedMineExpression = new SimpleBooleanProperty(false);
        BooleanExpression allEmptyCellsRevealedExpression = new SimpleBooleanProperty(true);

        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < colCount; ++col) {
                var cell = getCell(row, col);
                if (cell.hasMine())
                    // if any mine detonates, the entire expression evaluates to true
                    detonatedMineExpression = detonatedMineExpression.or(cell.getMine().detonatedProperty());
                else
                    // if any cell remains hidden, the entire expression evaluates to false
                    allEmptyCellsRevealedExpression = allEmptyCellsRevealedExpression.and(cell.revealedProperty());
            }
        }

        mineDetonated.bind(detonatedMineExpression);
        allEmptyCellsRevealed.bind(allEmptyCellsRevealedExpression);
    }

    void incrementClicksCount(int amount) {
        clicksCount += amount;
    }

    void setFirstClickHappened() {
        firstClickHappened.set(true);
    }

    List<List<Cell>> getCells() {
        return Collections.unmodifiableList(cells);
    }

    // convenience method, to avoid writing get().get() every time
    Cell getCell(int row, int col) {
        return cells.get(row).get(col);
    }

    public void revealAllMines() {
        if (!firstClickHappened.get()) return;

        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < colCount; ++col) {
                var cell = getCell(row, col);
                if (cell.hasMine()) cell.tryReveal();
            }
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColCount() {
        return colCount;
    }

    public int getMineCount() {
        return mineCount;
    }

    public boolean getHasSuperMine() {
        return hasSuperMine;
    }

    public int getSuperMineLifetime() {
        return superMineLifetime;
    }

    public int getMaxConcurrentMarkedCellsCount() {
        return maxConcurrentMarkedCellsCount;
    }

    public boolean getSuperMineActionRevealsEmptyCells() {
        return superMineActionRevealsEmptyCells;
    }

    public int getClicksCount() {
        return clicksCount;
    }

    public int getMarkedCellsCount() {
        return markedCellsCount.get();
    }

    public boolean getFirstClickHappened() {
        return firstClickHappened.get();
    }

    public ReadOnlyIntegerProperty markedCellsCountProperty() {
        return markedCellsCount.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty firstClickHappenedProperty() {
        return firstClickHappened.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty mineDetonatedProperty() {
        return mineDetonated.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty allEmptyCellsRevealedProperty() {
        return allEmptyCellsRevealed.getReadOnlyProperty();
    }

    public Node createView() {
        try {
            var loader = new FXMLLoader();
            var controller = new MinefieldController();
            loader.setLocation(viewURL);
            loader.setController(controller);

            var view = loader.load();
            controller.bindViewToModel(this);

            return (Node) view;
        } catch (IOException e) {
            throw new RuntimeException("Could not load the Minefield View");
        }
    }
}
