package gr.ntua.medialab.application.components.minesweeper.graphics;

import gr.ntua.medialab.application.Settings;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

// class that holds objects that control the appearance of cells

public class CellGraphics {
    public static double SIZE = Settings.CELL_SIZE();
    public static Paint HIDDEN_GRAPHIC = Settings.CELL_HIDDEN_GRAPHIC();
    public static Paint REVEALED_GRAPHIC = Settings.CELL_REVEALED_GRAPHIC();
    public static Paint MARK_GRAPHIC = Settings.CELL_MARK_GRAPHIC();
    public static Color NUMBERS_FILL_COLOR(int num) {
        return Settings.CELL_NUMBERS_FILL_COLOR(num);
    }
    public static Color NUMBERS_STROKE_COLOR = Settings.CELL_NUMBERS_STROKE_COLOR();
    public static Font NUMBERS_FONT = Settings.CELL_NUMBERS_FONT();
}
