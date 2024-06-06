package gr.ntua.medialab.application;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// A class that only contains static constants, used to control various parts of the application
// without having to track down where these constants are declared

public class Settings {
    // ! ------ * Filesystem locations (be careful when changing these) * ------ !

    // The folder in which scenarios will be saved to and loaded from
    private static final File scenarios_folder = new File("scenarios");

    // The folder in which the solution (mine positions) of the last game played is written to
    private static final File solution_folder = new File("solution");

    // The file which contains the tileset for the cell graphics
    // * If this is changed, make sure to also change the tileset indexes further down in this class
    private static final File tileset_file = new File("resources/tileset.png");

    // The file which contains the font to be used for the numbers on the revealed cells
    private static final File cell_numbers_font_file = new File("resources/fontface.ttf");

    // The file which contains the font to be used for the info displayed to the player during a game
    private static final File game_info_font_file = new File("resources/fontface.ttf");

    // The file which contains the font to be used for the text displayed on a game over
    private static final File game_over_font_file = new File("resources/fontface.ttf");

    // assert all of the above exist
    static {
        if (!scenarios_folder.exists() && !scenarios_folder.mkdirs())
            throw new RuntimeException("The scenarios folder does not exist, and could not be created.");
        if (!solution_folder.exists() && !solution_folder.mkdirs())
            throw new RuntimeException("The solution folder does not exist, and could not be created.");
        if (!tileset_file.exists()) System.err.println("The tileset file does not exist");
        if (!cell_numbers_font_file.exists()) System.err.println("The cell numbers font file does not exist");
        if (!game_info_font_file.exists()) System.err.println("The game info font file does not exist");
        if (!game_over_font_file.exists()) System.err.println("The game over font file does not exist");
    }

    // ! ------ * Scenario file parameters (be careful when changing these) * ------ !

    // The total number of lines a scenario file contains, each line containing exactly one parameter
    private static final int scenario_file_total_lines = 4;

    // The line (counting from 0) which contains the scenario's difficulty value
    private static final int scenario_file_difficulty_line = 0;

    // The line (counting from 0) which contains the scenario's mine count value
    private static final int scenario_file_mine_count_line = 1;

    // The line (counting from 0) which contains the scenario's time limit value
    private static final int scenario_file_time_limit_line = 2;

    // The line (counting from 0) which indicates whether a super mine exists in the scenario
    private static final int scenario_file_super_mine_existence_line = 3;

    // ! ------ * User defined scenario settings (you can change these) * ------ !

    // All the available difficulties the user can pick when creating a scenario
    // The index of each difficulty is used to index all of the following lists
    private static final List<Integer> scenario_available_difficulties = Arrays.asList(1, 2);

    // The grid size for each difficulty
    private static final List<Integer> scenario_grid_sizes = Arrays.asList(9, 16);

    // The minimum time limit the user can pick for each difficulty when creating a scenario
    private static final List<Integer> scenario_min_time_limits = Arrays.asList(120, 240);

    // The maximum time limit the user can pick for each difficulty when creating a scenario
    private static final List<Integer> scenario_max_time_limits = Arrays.asList(180, 360);

    // The minimum mine count the user can pick for each difficulty when creating a scenario
    private static final List<Integer> scenario_min_mine_counts = Arrays.asList(9, 35);

    // The maximum mine count the user can pick for each difficulty when creating a scenario
    private static final List<Integer> scenario_max_mine_counts = Arrays.asList(11, 45);

    // Whether the user can add a super mine or not when creating a scenario
    private static final List<Boolean> scenario_super_mine_can_exist = Arrays.asList(false, true);

    // ! ------ * Game parameters (you can change these) * ------ !

    // Number of (primary) clicks before a super mine stops being 'super'
    private static final int game_super_mine_active_for = 4;

    // Controls whether empty cells without neighbors revealed by marking an active super mine
    // cause recursive reveal of their adjacent cells or not
    private static final boolean game_super_mine_procs_recursive_reveal = false;

    // Name of the file in which the mine positions will be written
    // The file will always be created in the scenarios output folder
    private static final String game_mine_positions_filename = "mines.txt";

    // ! ------ * Graphics (you MUST change a lot of these if you change the tileset file) * ------ !

    // Size of each tile on the tileset in pixels
    // The tiles are assumed to be squares
    private static final double tile_size = 64.0;

    // Indexes of each tile on the tileset
    // The graphic displayed when a cell has not been revealed
    private static final int cell_hidden_graphic_row_index = 0;
    private static final int cell_hidden_graphic_col_index = 0;

    // The graphic displayed when a cell has been revealed
    private static final int cell_revealed_graphic_row_index = 0;
    private static final int cell_revealed_graphic_col_index = 1;

    // The graphic displayed on top of a hidden cell when it has beed marked
    private static final int cell_mark_graphic_row_index = 0;
    private static final int cell_mark_graphic_col_index = 2;

    // The graphic displayed on top of a revealed cell when it contains a mine
    private static final int mine_default_graphic_row_index = 0;
    private static final int mine_default_graphic_col_index = 3;

    // The graphic displayed on top of a revealed cell when it contains a super mine
    private static final int mine_super_graphic_row_index = 0;
    private static final int mine_super_graphic_col_index = 4;

    // The graphic displayed on top of a revealed cell when it contains a (super) mine
    // that has been disarmed (i.e. revealed by marking an active super mine)
    private static final int mine_disarmed_graphic_row_index = 0;
    private static final int mine_disarmed_graphic_col_index = 5;

    // The size at which to render the cells on the player's screen
    // Should ideally be lower than or equal to the tile size
    private static final double cell_render_size = 32.0;

    // Fill colors of the numbers displayed on revealed tiles with adjacent mines
    // The index of each color corresponds to the number that will be painted with the color
    private static final List<Color> cell_numbers_fill_colors = Arrays.asList(
            null, // 0
            Color.DEEPSKYBLUE, // 1
            Color.LAWNGREEN, // 2
            Color.GOLD, // 3
            Color.RED, // 4
            Color.BLUE, // 5
            Color.GREEN, // 6
            Color.DARKORANGE, // 7
            Color.DARKRED // 8
    );

    // Stroke color of the numbers displayed on revealed tiles with adjacent mines
    private static final Color cell_numbers_stroke_color = Color.BLACK;

    // Fill color of the text displayed on a game over
    private static final Color game_over_fill_color = Color.GOLD;

    // Stroke color of the text displayed on a game over
    private static final Color game_over_stroke_color = Color.BLACK;

    // Fill color of text indicating to the user that an action was successful
    private static final Color successful_action_text_fill_color = Color.DARKGREEN;

    // Fill color of text indicating to the user that an action failed
    private static final Color failed_action_text_fill_color = Color.DARKRED;

    // ! ------ * Sensitive settings (don't change these unless you know what you're doing) * ------ !

    private static final Image tileset; // Load the tileset
    private static final double mine_render_size = cell_render_size; // Ideally lesser or equal to the cell render size
    private static final double cell_numbers_font_size = cell_render_size / 1.2;
    private static final double game_info_font_size = cell_render_size / 2.5;
    private static final double game_over_font_size = cell_render_size * 2.0;

    // Try to load the tileset, otherwise make it equal to null
    static {
        Image temp = null;
        try {
            temp = new Image(tileset_file.toURI().toURL().toExternalForm());
            if (temp.isError()) {
                temp = null;
                throw new Exception();
            }
        }
        catch (Exception e) { System.err.println("The tileset could not be loaded"); }
        finally { tileset = temp; }
    }

    // Load a specific tile as Paint (specifically ImagePattern)
    // Return the fallback if the load fails
    private static Paint load_tile(int col, int row, Paint fallback) {
        if (tileset == null) return fallback;
        double width = tileset.getWidth() / tile_size;
        double height = tileset.getHeight() / tile_size;
        return new ImagePattern(tileset, -col, -row, width, height, true);
    }

    // Load the requested font from a file, or fall back to the default font if unable to
    private static Font load_font(String url, double size) {
        var font = Font.loadFont(url, size);
        return (font == null) ? Font.font(size) : font;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // ! ------ * Everything this class exports * ------ !
    
    public static File SCENARIOS_FOLDER() {
        return scenarios_folder;
    }

    public static File SOLUTION_FOLDER() {
        return solution_folder;
    }

    public static int SCENARIO_FILE_TOTAL_LINES() {
        return scenario_file_total_lines;
    }

    public static int SCENARIO_FILE_DIFFICULTY_LINE() {
        return scenario_file_difficulty_line;
    }

    public static int SCENARIO_FILE_MINE_COUNT_LINE() {
        return scenario_file_mine_count_line;
    }

    public static int SCENARIO_FILE_TIME_LIMIT_LINE() {
        return scenario_file_time_limit_line;
    }

    public static int SCENARIO_FILE_SUPER_MINE_EXISTENCE_LINE() {
        return scenario_file_super_mine_existence_line;
    }

    public static List<Integer> SCENARIO_AVAILABLE_DIFFICULTIES() {
        return Collections.unmodifiableList(scenario_available_difficulties);
    }

    public static int SCENARIO_GRID_SIZE(int difficulty) {
        return scenario_grid_sizes.get(scenario_available_difficulties.indexOf(difficulty));
    }

    public static int SCENARIO_MIN_TIME_LIMIT(int difficulty) {
        return scenario_min_time_limits.get(scenario_available_difficulties.indexOf(difficulty));
    }

    public static int SCENARIO_MAX_TIME_LIMIT(int difficulty) {
        return scenario_max_time_limits.get(scenario_available_difficulties.indexOf(difficulty));
    }

    public static int SCENARIO_MIN_MINE_COUNT(int difficulty) {
        return scenario_min_mine_counts.get(scenario_available_difficulties.indexOf(difficulty));
    }

    public static int SCENARIO_MAX_MINE_COUNT(int difficulty) {
        return scenario_max_mine_counts.get(scenario_available_difficulties.indexOf(difficulty));
    }

    public static boolean SCENARIO_SUPER_MINE_CAN_EXIST(int difficulty) {
        return scenario_super_mine_can_exist.get(scenario_available_difficulties.indexOf(difficulty));
    }

    public static int GAME_SUPER_MINE_ACTIVE_FOR() {
        return game_super_mine_active_for;
    }

    public static boolean GAME_SUPER_MINE_PROCS_RECURSIVE_REVEAL() {
        return game_super_mine_procs_recursive_reveal;
    }

    public static String GAME_MINE_POSITIONS_FILENAME() {
        return game_mine_positions_filename;
    }

    public static double CELL_SIZE() {
        return cell_render_size;
    }

    public static double MINE_SIZE() {
        return mine_render_size;
    }

    public static boolean TILESET_LOADED_CORRECTLY() {
        return tileset != null;
    }

    public static Paint CELL_HIDDEN_GRAPHIC() {
        return load_tile(cell_hidden_graphic_col_index, cell_hidden_graphic_row_index, Color.DARKGREY);
    }

    public static Paint CELL_REVEALED_GRAPHIC() {
        return load_tile(cell_revealed_graphic_col_index, cell_revealed_graphic_row_index, Color.GREY);
    }

    public static Paint CELL_MARK_GRAPHIC() {
        return load_tile(cell_mark_graphic_col_index, cell_mark_graphic_row_index, Color.YELLOW);
    }

    public static Paint MINE_DEFAULT_GRAPHIC() {
        return load_tile(mine_default_graphic_col_index, mine_default_graphic_row_index, Color.BLACK);
    }

    public static Paint MINE_SUPER_GRAPHIC() {
        return load_tile(mine_super_graphic_col_index, mine_super_graphic_row_index, Color.RED);
    }

    public static Paint MINE_DISARMED_GRAPHIC() {
        return load_tile(mine_disarmed_graphic_col_index, mine_disarmed_graphic_row_index, Color.GREEN);
    }

    public static Color CELL_NUMBERS_FILL_COLOR(int num) {
        if (num >= cell_numbers_fill_colors.size())
            System.err.println("Color for number '" + num +
                    "' was requested, even though only '" + cell_numbers_fill_colors.size() +
                    "' colors have been provided");
        return cell_numbers_fill_colors.get(num % cell_numbers_fill_colors.size());
    }

    public static Color CELL_NUMBERS_STROKE_COLOR() {
        return cell_numbers_stroke_color;
    }

    public static Font CELL_NUMBERS_FONT() {
        String url = null;
        try { url = cell_numbers_font_file.toURI().toURL().toExternalForm(); }
        catch (MalformedURLException e) { System.err.println("Cell numbers font could not be loaded"); }
        return load_font(url, cell_numbers_font_size);
    }

    public static Font GAME_INFO_FONT() {
        String url = null;
        try { url = game_info_font_file.toURI().toURL().toExternalForm(); }
        catch (MalformedURLException e) { System.err.println("Game info font could not be loaded"); }
        return load_font(url, game_info_font_size);
    }

    public static Font GAME_OVER_FONT() {
        String url = null;
        try { url = game_over_font_file.toURI().toURL().toExternalForm(); }
        catch (MalformedURLException e) { System.err.println("Game over font could not be loaded"); }
        return load_font(url, game_over_font_size);
    }

    public static Color GAME_OVER_FILL_COLOR() {
        return game_over_fill_color;
    }

    public static Color GAME_OVER_STROKE_COLOR() {
        return game_over_stroke_color;
    }

    public static Color SUCCESSFUL_ACTION_TEXT_FILL_COLOR() {
        return successful_action_text_fill_color;
    }

    public static Color FAILED_ACTION_TEXT_FILL_COLOR() {
        return failed_action_text_fill_color;
    }
}
