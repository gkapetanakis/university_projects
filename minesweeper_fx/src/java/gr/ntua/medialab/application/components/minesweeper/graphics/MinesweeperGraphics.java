package gr.ntua.medialab.application.components.minesweeper.graphics;

import gr.ntua.medialab.application.Settings;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

// class that holds objects that control the appearance of some elements of the minesweeper class

public class MinesweeperGraphics {
    public static final Font INFO_FONT = Settings.GAME_INFO_FONT();
    public static final Font GAME_OVER_FONT = Settings.GAME_OVER_FONT();
    public static final Color GAME_OVER_FILL_COLOR = Settings.GAME_OVER_FILL_COLOR();
    public static final Color GAME_OVER_STROKE_COLOR = Settings.GAME_OVER_STROKE_COLOR();
}
