package gr.ntua.medialab.application.components.modals;

import gr.ntua.medialab.application.scenarios.Scenario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.Function;

// class that loads scenarios based on input by the user

public class ScenarioLoader {
    private static final URL viewURL = ScenarioLoader.class.getResource("fxml/ScenarioLoaderView.fxml");
    private final File inputFolder; // where to load scenarios from
    private final Function<Scenario, Void> onLoad; // the function to call when 'Load' is clicked

    public ScenarioLoader(File inputFolder, Function<Scenario, Void> onLoad) {
        this.inputFolder = inputFolder;
        this.onLoad = onLoad;
    }

    public File getInputFolder() {
        return inputFolder;
    }

    public Function<Scenario, Void> getOnLoad() {
        return onLoad;
    }

    public Node createView() throws RuntimeException {
        try {
            var loader = new FXMLLoader();
            var controller = new ScenarioLoaderController();
            loader.setLocation(viewURL);
            loader.setController(controller);

            var view = loader.load();
            controller.bindViewToModel(this);

            return (Node) view;
        } catch (IOException e) {
            throw new RuntimeException("Could not load the Scenario Loader View");
        }
    }
}
