package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.GameHost;
import inf112.skeleton.app.game.GamePlayer;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;

import java.util.ArrayList;
import java.util.List;

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
