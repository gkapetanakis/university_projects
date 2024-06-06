package gr.ntua.medialab.application.scenarios;

import gr.ntua.medialab.application.Settings;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

// helper class to make the Scenario class more readable and separate some of the logic

class ScenarioSerializer {
    // number of parameters to be read from the file
    private static final int PARAM_COUNT = Settings.SCENARIO_FILE_TOTAL_LINES();

    // index of each parameter read
    private static final int DIFF_INDEX = Settings.SCENARIO_FILE_DIFFICULTY_LINE();
    private static final int TIME_INDEX = Settings.SCENARIO_FILE_TIME_LIMIT_LINE();
    private static final int MINE_INDEX = Settings.SCENARIO_FILE_MINE_COUNT_LINE();
    private static final int SUPER_MINE_INDEX = Settings.SCENARIO_FILE_SUPER_MINE_EXISTENCE_LINE();

    // parse the given file and return a Scenario object if the file contains a valid description
    static Scenario fromFile(File file) throws Scenario.InvalidDescriptionException, IOException {
        var parameters = new ArrayList<Integer>(PARAM_COUNT);
        try (var reader = new BufferedReader(new FileReader(file))) {
            for (int line = 0; line < PARAM_COUNT; ++line)
                parameters.add(Integer.parseInt(reader.readLine()));

            return new Scenario(
                parameters.get(DIFF_INDEX),
                parameters.get(TIME_INDEX),
                parameters.get(MINE_INDEX),
                parameters.get(SUPER_MINE_INDEX) == 1
            );
        }
        // thrown by Integer.parseInt()
        catch (NumberFormatException e) {
            throw new Scenario.InvalidDescriptionException("Unexpected character in scenario file");
        }
        // thrown by Config()
        catch (Scenario.InvalidValueException e) {
            throw new Scenario.InvalidDescriptionException("Bad description in scenario file");
        }
    }

    // create a file containing a valid Scenario description at the specified location, with the specified filename
    static void toFile(Scenario scenario, File outputFolder, String filename) throws IllegalArgumentException, IOException {
        if (filename == null) throw new IllegalArgumentException("Given filename cannot be null");

        var scenarioFile = new File(outputFolder, filename);

        if (!scenarioFile.createNewFile())
            throw new FileAlreadyExistsException("Scenario file already exists");

        // block can throw IOException
        try (var writer = new FileWriter(scenarioFile)){
            writer.write(scenario.getDifficulty() + "\n");
            writer.write(scenario.getMineCount() + "\n");
            writer.write(scenario.getTimeLimit() + "\n");
            writer.write(scenario.getSuperMineExists() ? "1\n" : "0\n");
        }
    }
}
