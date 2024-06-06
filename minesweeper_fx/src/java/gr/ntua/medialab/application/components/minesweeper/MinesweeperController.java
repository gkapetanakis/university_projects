package gr.ntua.medialab.application.components.minesweeper;

import gr.ntua.medialab.application.components.minesweeper.graphics.MinesweeperGraphics;
import javafx.beans.binding.When;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

class MinesweeperController {
    // view
    @FXML private GridPane infoBar;
    @FXML private Text totalMinesDisplay;
    @FXML private Text markedCellsDisplay;
    @FXML private Text remainingTimeDisplay;
    @FXML private Group minefieldParent;
    @FXML private Text gameOverView;

    // model
    // (no reference to the model needed)

    public void bindViewToModel(Minesweeper model) {
        var minefield = model.getMinefield();

        // change the font of every text child
        for (var child : infoBar.getChildren())
            if (child instanceof Text)
                ((Text) child).setFont(MinesweeperGraphics.INFO_FONT);

        // consume mouse clicks if the game has ended
        minefieldParent.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (model.gameHasEnded()) mouseEvent.consume();
        });

        totalMinesDisplay.setText(Integer.toString(minefield.getMineCount()));

        // marked cells text gets automatically updated
        markedCellsDisplay.textProperty().bind(minefield.markedCellsCountProperty().asString());

        // display the remaining time if the game has started
        remainingTimeDisplay.textProperty().bind(
                new When(model.gameHasStartedProperty())
                        .then(model.remainingTimeProperty().asString())
                        .otherwise("-")
        );

        // create and add the minefield view
        minefieldParent.getChildren().add(minefield.createView());

        gameOverView.setFont(MinesweeperGraphics.GAME_OVER_FONT);
        gameOverView.setFill(MinesweeperGraphics.GAME_OVER_FILL_COLOR);
        gameOverView.setStroke(MinesweeperGraphics.GAME_OVER_STROKE_COLOR);
        // the game over view is only visible after the game is over
        gameOverView.visibleProperty().bind(model.gameHasEndedProperty());
        // display the correct game over text based on if the player won or lost
        gameOverView.textProperty().bind(
                new When(minefield.mineDetonatedProperty().or(model.remainingTimeProperty().lessThanOrEqualTo(0)))
                        .then("You lose...")
                        .otherwise("You win!")
        );
    }
}
