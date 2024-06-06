package gr.ntua.medialab.application.components.minesweeper;

import gr.ntua.medialab.application.Settings;
import gr.ntua.medialab.application.components.minesweeper.graphics.CellGraphics;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.function.Function;

/*
    Simple class that contains the logic of a Cell
    Does not need a controller, due to its simplicity
    The createView() method also takes care of the controller logic
    I think all the methods are self-explanatory except for the createView() method
*/

public class Cell {
    // attributes
    private final int row;
    private final int col;

    // state
    private Mine mine;
    private int adjacentMinesCount = 0;
    private final ReadOnlyBooleanWrapper marked = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper revealed = new ReadOnlyBooleanWrapper(false);

    // methods
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean trySetMine(boolean isSuper) {
        if (mine != null) return false;

        mine = new Mine(isSuper);
        return true;
    }

    public void incrementAdjacentMinesCount(int amount) {
        adjacentMinesCount += amount;
    }

    public boolean tryMark() {
        if (marked.get() || revealed.get()) return false;

        marked.set(true);
        return true;
    }

    public boolean tryUnmark() {
        if (!marked.get()) return false;

        marked.set(false);
        return true;
    }

    public boolean tryReveal() {
        if (marked.get() || revealed.get()) return false;

        revealed.set(true);
        if (mine != null)
            mine.tryDetonate();
        return true;
    }

    public boolean hasMine() {
        return mine != null;
    }

    public boolean hasBeenMarked() {
        return marked.get();
    }

    public boolean hasBeenRevealed() {
        return revealed.get();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Mine getMine() {
        return mine;
    }

    public int getAdjacentMinesCount() {
        return adjacentMinesCount;
    }

    public ReadOnlyBooleanProperty markedProperty() {
        return marked.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty revealedProperty() {
        return revealed.getReadOnlyProperty();
    }

    /**
    *   The view is a stackpane with up to 2 layers.
    *   The botton layer is the hidden/revealed graphic.
    *   The top layer is the (entire) mine view or the adjacent mines graphic.
    *   The top layer is added only when needed, while the bottom layer is added once and never removed.
    */
    public StackPane createView() {
        // if tileset did not load correctly, add stroke to the main graphics (hidden/revealed, mark),
        // as the fallback graphics ae just plain colors
        var addStroke = !Settings.TILESET_LOADED_CORRECTLY();
        var rectSize = CellGraphics.SIZE;
        var botGraphic = new Rectangle(rectSize, rectSize, CellGraphics.HIDDEN_GRAPHIC);
        if (addStroke) botGraphic.setStroke(Color.BLACK);
        var view = new StackPane(botGraphic); // will always be added to index 0 (bottom layer)

        // behavior of view when cell gets marked
        Function<Void, Void> ifMarked = (var) -> {
            var topGraphic = new Rectangle(rectSize, rectSize, CellGraphics.MARK_GRAPHIC);
            if (addStroke) topGraphic.setStroke(Color.BLACK);
            view.getChildren().add(topGraphic); // will always be added to index 1 (top layer)
            return null;
        };

        // behavior of view if cell gets revealed
        Function<Void, Void> ifRevealed = (var) -> {
            ((Shape) view.getChildren().get(0)).setFill(CellGraphics.REVEALED_GRAPHIC); // will always be at index 0 (bottom layer)
            if (mine != null)
                view.getChildren().add(mine.createView()); // will always be added to index 1 (top layer)
            else if (adjacentMinesCount > 0) {
                var adjacentMinesText = new Text(Integer.toString(adjacentMinesCount));
                adjacentMinesText.setFont(CellGraphics.NUMBERS_FONT);
                adjacentMinesText.setFill(CellGraphics.NUMBERS_FILL_COLOR(adjacentMinesCount));
                adjacentMinesText.setStroke(CellGraphics.NUMBERS_STROKE_COLOR);
                view.getChildren().add(adjacentMinesText); // will always be added to index 1 (top layer)
            }
            return null;
        };

        // if the cell is already marked or has been revealed, the view must be created correctly
        if (marked.get()) ifMarked.apply(null);
        if (revealed.get()) ifRevealed.apply(null);

        marked.addListener((observable, oldValue, newValue) -> {
            if (newValue)
                ifMarked.apply(null);
            else
                view.getChildren().remove(1); // will always be at index 1 (top layer)
        });

        // can only change from false to true, once (no need to check if newValue is true)
        revealed.addListener((observable, oldValue, newValue) -> ifRevealed.apply(null));

        return view;
    }
}
