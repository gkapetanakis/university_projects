package gr.ntua.medialab.application.components.minesweeper;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.LinkedList;
import java.util.Queue;

class MinefieldController {
    // view
    @FXML private GridPane minefieldPane;

    // model
    private Minefield model;

    // assists in communication between minefield handler and cell handler
    private Cell clickedCell;

    public void bindViewToModel(Minefield model) {
        this.model = model;

        // create the view of every cell
        for (var row : model.getCells())
            for (var cell : row)
                minefieldPane.add(cell.createView(), cell.getCol(), cell.getRow());

        // add event listeners to the cells and the minefield
        minefieldPane.setOnMouseClicked(this::onMinefieldClicked);
        for (var child : minefieldPane.getChildren())
            child.setOnMouseClicked(this::onCellClicked);
    }

    private void onCellClicked(MouseEvent mouseEvent) throws IllegalAccessError {
        if (model == null) throw new IllegalAccessError("Must bind to a model before calling this method");

        // get the clicked cell in a class field so the minefield handler can access it
        var cell = (Node) mouseEvent.getSource();
        int row = GridPane.getRowIndex(cell);
        int col = GridPane.getColumnIndex(cell);
        clickedCell = model.getCell(row, col);
    }

    private void onMinefieldClicked(MouseEvent mouseEvent) throws IllegalAccessError {
        if (model == null) throw new IllegalAccessError("Must bind to a model before calling this method");

        // on the first pimary click, set the mines and write them to a file before doing the rest
        if (!model.getFirstClickHappened()) {
            if (mouseEvent.getButton() != MouseButton.PRIMARY) return;
            model.placeMines(clickedCell.getRow(), clickedCell.getCol());
            model.writeMinesToFile();
            model.setFirstClickHappened();
        }

        // if a revealed cell is clicked, do nothing
        if (clickedCell.hasBeenRevealed()) return;

        // queue to hold the cells that will have to be processed after the handler is done
        var cellsToProcess = new LinkedList<Cell>();
        switch (mouseEvent.getButton()) {
            // left click
            case PRIMARY -> {
                if (clickedCell.hasBeenMarked()) return;
                // if cell was hidden and unmarked, click was successful
                model.incrementClicksCount(1);
                cellsToProcess.add(clickedCell); // process the cell after
            }
            // right click
            case SECONDARY -> {
                // if the cell is unmarked and no more marks can be added, do nothing
                if (!clickedCell.hasBeenMarked() &&
                        model.getMarkedCellsCount() >= model.getMaxConcurrentMarkedCellsCount()) return;

                // tryUnmark() returns true if successful
                if (!clickedCell.tryUnmark()) {
                    clickedCell.tryMark(); // at this point, tryMark() will always work
                    if (clickedCell.hasMine() && clickedCell.getMine().isSuper() &&
                            model.getClicksCount() < model.getSuperMineLifetime()) {
                        // active super mine has been marked
                        for (int col = 0; col < model.getColCount(); ++col) {
                            // disarm the entire row
                            var cell = model.getCell(clickedCell.getRow(), col);
                            var mine = cell.getMine();
                            if (mine != null) mine.tryDisarm();
                            cellsToProcess.add(cell); // process the entire row
                        }

                        for (int row = 0; row < model.getRowCount(); ++row) {
                            // disarm the entire column
                            var cell = model.getCell(row, clickedCell.getCol());
                            var mine = cell.getMine();
                            if (mine != null) mine.tryDisarm();
                            cellsToProcess.add(cell); // process the entire column
                        }
                    }
                }
            }
        }

        processCells(cellsToProcess, mouseEvent.getButton() == MouseButton.SECONDARY);
    }

    private void processCells(Queue<Cell> cellsToProcess, boolean secondaryClick) {
        while (!cellsToProcess.isEmpty()) {
            // get the next cell and pop it off the queue
            var cell = cellsToProcess.poll();
            cell.tryUnmark(); // unmark it if it's marked
            // reveal the cell and if it has no adjacent mines process every adjacent cell as well
            if (cell.tryReveal() && cell.getAdjacentMinesCount() == 0) {
                // controls whether a marked super mine's ability causes recursive reveal of empty cells
                if (secondaryClick && !model.getSuperMineActionRevealsEmptyCells()) continue;
                for (int adjRow = cell.getRow() - 1; adjRow <= cell.getRow() + 1; ++adjRow) {
                    for (int adjCol = cell.getCol() - 1; adjCol <= cell.getCol() + 1; ++adjCol) {
                        if (adjRow >= 0 && adjRow < model.getRowCount() && adjCol >= 0 && adjCol < model.getColCount()) {
                            var adjCell = model.getCell(adjRow, adjCol);
                            cellsToProcess.add(adjCell);
                        }
                    }
                }
            }
        }
    }
}
