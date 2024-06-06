package gr.ntua.medialab.application.components.minesweeper;

import gr.ntua.medialab.application.components.minesweeper.graphics.MineGraphics;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/*
    Simple class that contains the logic of a Mine
    Does not need a controller, due to its simplicity
    The createView() method also takes care of the controller logic
    I think all the methods are self-explanatory except for the createView() method
*/

public class Mine {
    // attributes
    private final boolean isSuper; // indicates super mine

    // state
    private final ReadOnlyBooleanWrapper detonated = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper disarmed = new ReadOnlyBooleanWrapper(false);

    // methods
    public Mine(boolean isSuper) {
        this.isSuper = isSuper;
    }

    public boolean tryDetonate() {
        if (disarmed.get()) return false;

        detonated.set(true);
        return true;
    }

    public boolean tryDisarm() {
        if (detonated.get()) return false;

        disarmed.set(true);
        return true;
    }

    public boolean isSuper() {
        return isSuper;
    }

    public boolean hasDetonated() {
        return detonated.get();
    }

    public boolean hasBeenDisarmed() {
        return disarmed.get();
    }

    public ReadOnlyBooleanProperty detonatedProperty() {
        return detonated.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty disarmedProperty() {
        return disarmed.getReadOnlyProperty();
    }

    /**
     *   The view is a stackpane with up to 2 layers.
     *   The botton layer is the default/super mine graphic.
     *   The top layer is the disarmed graphic.
     *   The top layer is added only when needed, while the bottom layer is added once and never removed.
     */
    public StackPane createView() {
        var rectSize = MineGraphics.SIZE;
        var view = new StackPane(
                new Rectangle(rectSize, rectSize,
                        isSuper ? MineGraphics.SUPER_GRAPHIC : MineGraphics.DEFAULT_GRAPHIC) // always added
        );

        // if the mine has already been disarmed, the view must be created correctly
        if (disarmed.get())
            view.getChildren().add(new Rectangle(rectSize, rectSize,
                    MineGraphics.DISARMED_GRAPHIC)); // might be added

        // can only change from false to true, once (no need to check if newValue is true)
        disarmed.addListener((observable, oldValue, newValue) ->
                view.getChildren().add(new Rectangle(rectSize, rectSize,
                        MineGraphics.DISARMED_GRAPHIC))  // might be added
        );

        return view;
    }
}
