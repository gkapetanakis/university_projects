package gr.ntua.medialab.application.components.menu;

import gr.ntua.medialab.application.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;

public class MenuBar {
    private static final URL viewURL = MenuBar.class.getResource("fxml/MenuBarView.fxml");
    private final App app;

    public MenuBar(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public Node createView() throws RuntimeException {
        try {
            var loader = new FXMLLoader();
            var controller = new MenuBarController();
            loader.setLocation(viewURL);
            loader.setController(controller);

            var view = loader.load();
            controller.bindViewToModel(this);

            return (Node) view;
        } catch (IOException e) {
            throw new RuntimeException("Could not load the Menu Bar View");
        }
    }
}
