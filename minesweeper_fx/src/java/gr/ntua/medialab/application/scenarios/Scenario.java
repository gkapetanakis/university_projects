package gr.ntua.medialab.application.scenarios;

import java.io.File;
import java.io.IOException;

// class to represent a game configuration (called a scenario)

public class Scenario {
    private int difficulty;
    private int timeLimit;
    private int mineCount;
    private boolean superMineExists;

    public Scenario(int difficulty, int timeLimit, int mineCount, boolean superMineExists) throws InvalidValueException {
        setDifficulty(difficulty);
        setTimeLimit(timeLimit);
        setMineCount(mineCount);
        setSuperMineExists(superMineExists);
    }

    // create a scenario from a file containing a valid description
    public static Scenario fromFile(File file) throws InvalidDescriptionException, IOException {
        return ScenarioSerializer.fromFile(file);
    }

    // create a file containing a valid description representing the scenario
    public static void toFile(Scenario scenario, File outputFolder, String filename) throws IllegalArgumentException, IOException {
        ScenarioSerializer.toFile(scenario, outputFolder, filename);
    }

    public void setDifficulty(int difficulty) throws InvalidValueException {
        if (!ScenarioConstraints.AVAILABLE_DIFFICULTIES.contains(difficulty))
            throw new InvalidValueException("Invalid difficulty");

        this.difficulty = difficulty;
    }

    public void setTimeLimit(int timeLimit) throws InvalidValueException {
        if (timeLimit < ScenarioConstraints.MIN_TIME_LIMIT(difficulty) ||
            timeLimit > ScenarioConstraints.MAX_TIME_LIMIT(difficulty))
            throw new InvalidValueException("Invalid time limit");

        this.timeLimit = timeLimit;
    }

    public void setMineCount(int mineCount) throws InvalidValueException {
        if (mineCount < ScenarioConstraints.MIN_MINE_COUNT(difficulty) ||
            mineCount > ScenarioConstraints.MAX_MINE_COUNT(difficulty))
            throw new InvalidValueException("Invalid mine count");

        this.mineCount = mineCount;
    }

    public void setSuperMineExists(boolean superMineExists) throws InvalidValueException {
        if (superMineExists && !ScenarioConstraints.SUPER_MINE_CAN_EXIST(difficulty))
            throw new InvalidValueException("A super mine cannot exist at this difficulty");

        this.superMineExists = superMineExists;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getGridSize() {
        return ScenarioConstraints.GRID_SIZE(difficulty);
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getMineCount() {
        return mineCount;
    }

    public boolean getSuperMineExists() {
        return superMineExists;
    }

    static final class InvalidValueException extends Exception {
        public InvalidValueException(String message) {
            super(message);
        }
    }

    static final class InvalidDescriptionException extends Exception {
        public InvalidDescriptionException(String message) {
            super(message);
        }
    }
}
