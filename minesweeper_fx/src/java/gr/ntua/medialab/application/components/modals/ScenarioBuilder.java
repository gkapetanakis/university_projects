package gr.ntua.medialab.application.components.modals;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.net.URL;

// class that creates scenarios based on input by the user

public class ScenarioBuilder {
    private static final URL viewURL = ScenarioBuilder.class.getResource("fxml/ScenarioBuilderView.fxml");
    private final File outputFolder; // where to save scenarios to

    public ScenarioBuilder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public File getOutputFolder() {
        return outputFolder;
    }

    public Node createView() throws RuntimeException {
        try {
            var loader = new FXMLLoader();
            var controller = new ScenarioBuilderController();
            loader.setLocation(viewURL);
            loader.setController(controller);

            var view = loader.load();
            controller.bindViewToModel(this);

            return (Node) view;
        } catch (IOException e) {
            throw new RuntimeException("Could not load the Scenario Builder View");
        }
    }
}
