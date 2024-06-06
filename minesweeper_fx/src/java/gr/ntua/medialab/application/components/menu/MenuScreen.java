package gr.ntua.medialab.application.components.menu;

import gr.ntua.medialab.application.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;

public class MenuScreen {
    private static final URL viewURL = MenuScreen.class.getResource("fxml/MenuScreenView.fxml");
    private final App app;

    public MenuScreen(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public Node createView() throws RuntimeException {
        try {
            var loader = new FXMLLoader();
            var controller = new MenuScreenController();
            loader.setLocation(viewURL);
            loader.setController(controller);

            var view = loader.load();
            controller.bindViewToModel(this);

            return (Node) view;
        } catch (IOException e) {
            throw new RuntimeException("Could not load the Menu Screen View");
        }
    }
}
