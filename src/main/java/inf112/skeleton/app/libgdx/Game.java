package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputAdapter;
/**
 * Handles rendering, textures and event handling (key presses)
 * Game(this class) runs client side on each players PC, so only objects and methods necessary to have client side should be used here
 *
 * ---------------------------------------------
 * DEPRECATED
 * ---------------------------------------------
 * This class has been moved to GameScreen class, as GameScreen is the class launched from the new UI.
 * This class is still being kept here for a little while due to backwards compability.
 * Check if it can be safely removed at some point.
 */
public class Game extends InputAdapter implements ApplicationListener {

    public static int BOARD_X = 12;
    public static int BOARD_Y = 12;

    /**
     * These functions are not currently in use, but inherited from superclass
     */
    @Override
    public void dispose() {

    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
