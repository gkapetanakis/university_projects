package gr.ntua.medialab.application.components.modals;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

class GameLoggerController {
    // view
    @FXML private TableView<GameLogger.Entry> loggerDisplay;
    @FXML private TableColumn<GameLogger.Entry, String> gamesColumn;
    @FXML private TableColumn<GameLogger.Entry, Number> minesColumn;
    @FXML private TableColumn<GameLogger.Entry, Number> clicksColumn;
    @FXML private TableColumn<GameLogger.Entry, Number> timeColumn;
    @FXML private TableColumn<GameLogger.Entry, String> winnerColumn;

    // model
    // (no reference to the model needed)

    public void bindViewToModel(GameLogger model) {
        loggerDisplay.setPlaceholder(new Text("No data to show.\nCome back after playing a few rounds."));
        // bind the TableView to the entries of the model
        loggerDisplay.setItems(model.getEntries());
        // set the way values for the table columns are created from the entries of the model
        gamesColumn.setCellValueFactory(entry -> new SimpleStringProperty("Game " + entry.getValue().id()));
        minesColumn.setCellValueFactory(entry -> new SimpleIntegerProperty(entry.getValue().mineCount()));
        clicksColumn.setCellValueFactory(entry -> new SimpleIntegerProperty(entry.getValue().clickCount()));
        timeColumn.setCellValueFactory(entry -> new SimpleIntegerProperty(entry.getValue().timeLimit()));
        winnerColumn.setCellValueFactory(entry -> new SimpleStringProperty(entry.getValue().winner()));
    }
}
