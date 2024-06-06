package gr.ntua.medialab.application.components.modals;

import gr.ntua.medialab.application.Settings;
import gr.ntua.medialab.application.scenarios.Scenario;
import gr.ntua.medialab.application.scenarios.ScenarioConstraints;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

class ScenarioBuilderController {
    // view
    @FXML private TextField scenarioIdTextField;
    @FXML private TextField timeLimitTextField;
    @FXML private TextField totalMinesTextField;
    @FXML private ChoiceBox<Integer> difficultyChoiceBox;
    @FXML private CheckBox superMineCheckBox;
    @FXML private Button createButton;
    @FXML private Text statusText;

    // model
    private ScenarioBuilder model;

    // status text colors
    private static final Color ERROR_COLOR = Settings.FAILED_ACTION_TEXT_FILL_COLOR();
    private static final Color SUCCESS_COLOR = Settings.SUCCESSFUL_ACTION_TEXT_FILL_COLOR();

    public void bindViewToModel(ScenarioBuilder model) {
        this.model = model;

        createButton.setOnAction(this::onCreateClicked);

        // set the behavior of the other input fields based on the selected difficulty
        difficultyChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldIndex, newIndex) -> {
            int selectedDifficulty = difficultyChoiceBox.getItems().get(newIndex.intValue());
            totalMinesTextField.setPromptText(
                    ScenarioConstraints.MIN_MINE_COUNT(selectedDifficulty) + " - " +
                    ScenarioConstraints.MAX_MINE_COUNT(selectedDifficulty)
            );
            timeLimitTextField.setPromptText(
                    ScenarioConstraints.MIN_TIME_LIMIT(selectedDifficulty) + " - " +
                    ScenarioConstraints.MAX_TIME_LIMIT(selectedDifficulty)
            );
            superMineCheckBox.setDisable(!ScenarioConstraints.SUPER_MINE_CAN_EXIST(selectedDifficulty));
            // deselect the checkbox if it gets disabled
            superMineCheckBox.setSelected(superMineCheckBox.isSelected() && !superMineCheckBox.isDisabled());
        });

        // add the difficulty values
        difficultyChoiceBox.getItems().addAll(ScenarioConstraints.AVAILABLE_DIFFICULTIES);

        // intially select the first one
        difficultyChoiceBox.setValue(difficultyChoiceBox.getItems().get(0));

        // enable the create button only if all values are inputted (e.g. are not blank (i.e. whitespace-only))
        createButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> scenarioIdTextField.getText().isBlank() ||
                      timeLimitTextField.getText().isBlank() ||
                      totalMinesTextField.getText().isBlank(),
                scenarioIdTextField.textProperty(),
                timeLimitTextField.textProperty(),
                totalMinesTextField.textProperty()
        ));
    }

    private void onCreateClicked(ActionEvent ignoredEvent) throws IllegalAccessError {
        if (model == null) throw new IllegalAccessError("Must bind to a model before calling this method");

        var filename = scenarioIdTextField.getText().trim() + ".txt";
        // disallow the reserved name where the minefield writes the mines to
        if (filename.equals(Settings.GAME_MINE_POSITIONS_FILENAME())) {
            statusText.setFill(ERROR_COLOR);
            statusText.setText("This filename is reserved");
            return;
        }

        try {
            // parse the text field values, might throw NumberFormatException
            int difficulty = difficultyChoiceBox.getValue();
            int timeLimit = Integer.parseInt(timeLimitTextField.getText().trim());
            int mineCount = Integer.parseInt(totalMinesTextField.getText().trim());
            boolean superMineExists = superMineCheckBox.isSelected();

            var scenario = new Scenario(difficulty, timeLimit, mineCount, superMineExists); // InvalidValueException
            Scenario.toFile(scenario, model.getOutputFolder(), filename); // FileAlreadyExistsException, IOException
            statusText.setFill(SUCCESS_COLOR);
            statusText.setText("Scenario was created successfully");
        }
        catch (NumberFormatException e) {
            statusText.setFill(ERROR_COLOR);
            statusText.setText("Expected integers but got something else");
        }
        // all other thrown exceptions are: InvalidValueException, FileAlreadyExistsException, IOException
        // but the behavior of the catch block is the same for all of them
        catch (Exception e) {
            // invalid value given by user, file not created, or other io error
            statusText.setFill(ERROR_COLOR);
            statusText.setText(e.getMessage());
        }
    }
}
