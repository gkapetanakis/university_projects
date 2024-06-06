package gr.ntua.medialab.application.components.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

class MenuScreenController {
    // view
    @FXML private Button createButton;
    @FXML private Button loadButton;
    @FXML private Button startButton;
    @FXML private Button exitButton;

    // model
    // (no reference to the model needed)

    public void bindViewToModel(MenuScreen model) {
        var app = model.getApp();
        createButton.setOnAction(app::onCreateClicked);
        loadButton.setOnAction(app::onLoadClicked);
        startButton.setOnAction(app::onStartClicked);
        exitButton.setOnAction(app::onExitClicked);
    }
}
