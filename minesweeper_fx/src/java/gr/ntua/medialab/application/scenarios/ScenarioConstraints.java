package gr.ntua.medialab.application.scenarios;

import gr.ntua.medialab.application.Settings;

import java.util.List;

// class to hold the available difficulties of a scenario, as well as the costraints set by each one

public class ScenarioConstraints {
    public static List<Integer> AVAILABLE_DIFFICULTIES = Settings.SCENARIO_AVAILABLE_DIFFICULTIES();

    public static int GRID_SIZE(int difficulty) {
        return Settings.SCENARIO_GRID_SIZE(difficulty);
    }

    public static int MIN_TIME_LIMIT(int difficulty) {
        return Settings.SCENARIO_MIN_TIME_LIMIT(difficulty);
    }

    public static int MAX_TIME_LIMIT(int difficulty) {
        return Settings.SCENARIO_MAX_TIME_LIMIT(difficulty);
    }

    public static int MIN_MINE_COUNT(int difficulty) {
        return Settings.SCENARIO_MIN_MINE_COUNT(difficulty);
    }

    public static int MAX_MINE_COUNT(int difficulty) {
        return Settings.SCENARIO_MAX_MINE_COUNT(difficulty);
    }

    public static boolean SUPER_MINE_CAN_EXIST(int difficulty) {
        return Settings.SCENARIO_SUPER_MINE_CAN_EXIST(difficulty);
    }
}
