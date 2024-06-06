package gr.ntua.medialab.application;

import gr.ntua.medialab.application.components.menu.MenuBar;
import gr.ntua.medialab.application.components.menu.MenuScreen;
import gr.ntua.medialab.application.components.minesweeper.Minesweeper;
import gr.ntua.medialab.application.components.modals.GameLogger;
import gr.ntua.medialab.application.components.modals.ScenarioBuilder;
import gr.ntua.medialab.application.components.modals.ScenarioLoader;
import gr.ntua.medialab.application.scenarios.Scenario;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

// Main class and starting point of the module

public class App extends Application {
    private Scenario currentScenario;
    private Minesweeper currentGame;
    private Timer resetTimer; // used to transition between an ended game and the menu screen

    private final MenuBar menuBar = new MenuBar(this);
    private final MenuScreen menuScreen = new MenuScreen(this);
    private final ScenarioBuilder scenarioBuilder = new ScenarioBuilder(Settings.SCENARIOS_FOLDER());
    private final ScenarioLoader scenarioLoader = new ScenarioLoader(Settings.SCENARIOS_FOLDER(), scenario -> {
        // when 'Load' is clicked, just get the created scenario
        this.currentScenario = scenario;
        return null;
    });
    private final GameLogger gameLogger = new GameLogger();
    private final VBox root = new VBox();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // every time a node is added or removed, resize the stage
        root.getChildren().addListener((ListChangeListener<? super Node>) change -> stage.sizeToScene());
        root.getChildren().add(menuBar.createView());
        root.getChildren().add(menuScreen.createView());

        stage.setOnCloseRequest(windowEvent -> exitGracefully()); // x button pressed
        stage.setScene(new Scene(root));
        stage.setTitle("MediaLab Minesweeper");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    // used to create the GameLogger, the ScenarioBuilder and the ScenarioLoader
    private Stage createModal(Parent root, String title) {
        var modal = new Stage();
        modal.addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) modal.close();
        });
        modal.setOnCloseRequest(windowEvent -> modal.close()); // x button pressed
        // every time a node is added or removed, resize the modal
        root.getChildrenUnmodifiable().addListener((ListChangeListener<? super Node>) change -> modal.sizeToScene());
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setScene(new Scene(root));
        modal.setTitle(title);
        modal.centerOnScreen();
        modal.setResizable(false);
        return modal;
    }

    // application -> create
    public void onCreateClicked(ActionEvent ignoredEvent) {
        var modal = createModal(new Group(scenarioBuilder.createView()), "Scenario Builder");
        modal.show();
    }

    // application -> load
    public void onLoadClicked(ActionEvent ignoredEvent) {
        var modal = createModal(new Group(scenarioLoader.createView()), "Scenario Loader");
        modal.show();
    }

    // application -> start
    public void onStartClicked(ActionEvent ignoredEvent) {
        if (currentScenario == null) return;

        if (resetTimer != null) {
            resetTimer.cancel();
            resetTimer.purge();
            resetTimer = null;
        }

        currentGame = new Minesweeper(
                currentScenario.getGridSize(),
                currentScenario.getGridSize(),
                currentScenario.getMineCount(),
                currentScenario.getSuperMineExists(),
                currentScenario.getTimeLimit()
        );

        // when the game ends, create a new entry in the GameLogger
        currentGame.gameHasEndedProperty().addListener((observable, oldValue, newValue) -> {
            gameLogger.addEntry(
                    currentGame.getMineCount(),
                    currentGame.getClicksCount(),
                    // was not sure if the total time elapsed or the total available time was needed
                    currentGame.getTimeLimit(), // or currentGame.getTimeLimit() - currentGame.getRemainingTime()
                    currentGame.playerWon() ? "Player" : "CPU"
            );

            resetTimer = new Timer();
            resetTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            // call Platform.runLater() as changing the root's nodes from another thread is not allowed
                            Platform.runLater(() -> {
                                // last child will always be either the game or the menu screen
                                root.getChildren().remove(root.getChildren().size() - 1);
                                root.getChildren().add(menuScreen.createView());
                                resetTimer.purge();
                                resetTimer = null;
                            });
                        }
                    },
                    5000 // run after 5 sec
            );
        });

        // last child will always be either the game or the menu screen
        root.getChildren().remove(root.getChildren().size() - 1);
        root.getChildren().add(currentGame.createView());
    }

    // application -> exit
    public void onExitClicked(ActionEvent ignoredEvent) {
        exitGracefully();
    }


    // details -> rounds
    public void onRoundsClicked(ActionEvent ignoredEvent) {
        var modal = createModal(new Group(gameLogger.createView()), "Match History");
        modal.show();
    }

    // details -> solution
    public void onSolutionClicked(ActionEvent ignoredEvent) {
        currentGame.revealAllMines();
    }

    private void exitGracefully() {
        // kill all threads
        if (currentGame != null) currentGame.revealAllMines(); // cancels timer
        if (resetTimer != null) resetTimer.cancel(); // cancel this timer as well
        Platform.exit(); // then exit
    }
}
