package gr.ntua.medialab.application.components.menu;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

class MenuBarController {
    // view
    @FXML private MenuItem createButton;
    @FXML private MenuItem loadButton;
    @FXML private MenuItem startButton;
    @FXML private MenuItem exitButton;
    @FXML private MenuItem roundsButton;
    @FXML private MenuItem solutionButton;

    // model
    // (no reference to the model needed)

    public void bindViewToModel(MenuBar model) {
        var app = model.getApp();
        createButton.setOnAction(app::onCreateClicked);
        loadButton.setOnAction(app::onLoadClicked);
        startButton.setOnAction(app::onStartClicked);
        exitButton.setOnAction(app::onExitClicked);
        roundsButton.setOnAction(app::onRoundsClicked);
        solutionButton.setOnAction(app::onSolutionClicked);
    }
}
