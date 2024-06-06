package gr.ntua.medialab.application.components.minesweeper.graphics;

import gr.ntua.medialab.application.Settings;
import javafx.scene.paint.Paint;

// class that holds objects that control the appearance of mines

public class MineGraphics {
    public static double SIZE = Settings.MINE_SIZE();
    public static Paint DEFAULT_GRAPHIC = Settings.MINE_DEFAULT_GRAPHIC();
    public static Paint SUPER_GRAPHIC = Settings.MINE_SUPER_GRAPHIC();
    public static Paint DISARMED_GRAPHIC = Settings.MINE_DISARMED_GRAPHIC();
}
