package gr.ntua.medialab.application.components.modals;

import gr.ntua.medialab.application.Settings;
import gr.ntua.medialab.application.scenarios.Scenario;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileNotFoundException;

class ScenarioLoaderController {
    // view
    @FXML private TextField scenarioIdTextField;
    @FXML private Button loadButton;
    @FXML private Text statusText;

    // model
    private ScenarioLoader model;

    // status text colors
    private static final Color ERROR_COLOR = Settings.FAILED_ACTION_TEXT_FILL_COLOR();
    private static final Color SUCCESS_COLOR = Settings.SUCCESSFUL_ACTION_TEXT_FILL_COLOR();

    public void bindViewToModel(ScenarioLoader model) {
        this.model = model;

        loadButton.setOnAction(this::onLoadClicked);

        // enable the load button only when the input is not blank (i.e. whitespace-only)
        loadButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> scenarioIdTextField.getText().isBlank(),
                scenarioIdTextField.textProperty()
        ));
    }

    private void onLoadClicked(ActionEvent ignoredEvent) throws IllegalAccessError {
        if (model == null) throw new IllegalAccessError("Must bind to a model before calling this method");

        var scenarioFile = new File(model.getInputFolder(), scenarioIdTextField.getText().trim() + ".txt");
        try {
            // call the onLoad() function
            model.getOnLoad().apply(Scenario.fromFile(scenarioFile));
            statusText.setFill(SUCCESS_COLOR);
            statusText.setText("The scenario was loaded successfully");
        }
        catch (FileNotFoundException e) {
            statusText.setFill(ERROR_COLOR);
            statusText.setText("The requested scenario does not exist");
        }
        // all other thrown exceptions are: InvalidDescriptionException, IOException
        // but the behavior of the catch block is the same for all of them
        catch (Exception e) {
            statusText.setFill(ERROR_COLOR);
            statusText.setText(e.getMessage());
        }
    }
}
