package gr.ntua.medialab.application.components.modals;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;

// class that holds Entries with info of finished games and displays them as a TableView

public class GameLogger {
    private static final URL viewURL = GameLogger.class.getResource("fxml/GameLoggerView.fxml");
    // record to represent a logger entry
    public record Entry(int id, int mineCount, int clickCount, int timeLimit, String winner) {}

    // state
    private int id = 1;
    private final ObservableList<Entry> entries = FXCollections.observableArrayList();

    public void addEntry(int mineCount, int clickCount, int timeLimit, String winner) {
        entries.add(new Entry(id++, mineCount, clickCount, timeLimit, winner));
    }

    public ObservableList<Entry> getEntries() {
        return entries;
    }

    public Node createView() throws RuntimeException {
        try {
            var loader = new FXMLLoader();
            var controller = new GameLoggerController();
            loader.setLocation(viewURL);
            loader.setController(controller);

            var view = loader.load();
            controller.bindViewToModel(this);

            return (Node) view;
        } catch (IOException e) {
            throw new RuntimeException("Could not load the Game Logger View");
        }
    }
}
