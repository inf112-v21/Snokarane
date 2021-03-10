package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.libgdx.RoboGame;

/**
 * Generic interface for UI Screens with menus, buttons, text fields and pictures etc.
 */
public interface IUiScreen extends Screen {
    /**
     * Set stage, and set game to arg variable
     * Initialize screen width and height,
     * and call loadUIVisuals and loadUIIntreractibles
     *
     * @param game game to be passed into this.game
     */
    void startScreen(RoboGame game);

    /**
     * Load visual UI elements on screen
     */
    void loadUIVisuals();

    /**
     * Load intractable UI elements on screen
     */
    void loadUIIntractables();
}
