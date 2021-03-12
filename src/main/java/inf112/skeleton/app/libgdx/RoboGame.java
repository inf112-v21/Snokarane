package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.libgdx.screens.MenuScreen;
import com.badlogic.gdx.Game;

import java.util.ArrayList;
import java.util.List;

public class RoboGame extends Game{
    public SpriteBatch batch;
    public BitmapFont font;
    public Skin skin;

    // Board dimensions
    public static int BOARD_X = 10;
    public static int BOARD_Y = 10;

    public OrthogonalTiledMapRenderer renderer;
    public TiledMap tiledMap;

    public void create(){
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);
        this.skin = new Skin(Gdx.files.internal("skins/skin/glassy-ui.json"));

        // Load .tmx file from disk
        tiledMap = loadTileMapFromFile("10x10-testmap.tmx");

        // Start camera/rendering
        initializeRendering();

        setScreen(new MenuScreen(this));

     }

    /**
     * Initialize camera and renderer, sets view/renderer to tiledMap
     */
    private void initializeRendering(){
        // Initialize camera object
        OrthographicCamera camera = new OrthographicCamera();
        // Set camera to orthographic, size board dimensions
        camera.setToOrtho(false, BOARD_X, BOARD_Y+2f); // TODO fix hardcoded y values for card deck visuals
        // Set camera X-position
        camera.position.x = 5F;
        camera.position.y = 4F;
        camera.update();

        // Initialize renderer                                  v--- 300F is tile size
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1F/300F);
        // Set renderer to view camera
        renderer.setView(camera);
    }

    /**
     * @return TiledMap object loaded from path
     * @param path path to .tmx file for map
     */
    public TiledMap loadTileMapFromFile(String path){
        return new TmxMapLoader().load(path);
    }

     // Libgdx memory management
    public void dispose () {
        batch.dispose();
        renderer.dispose();
        font.dispose();
    }

    // Call super.render() here or libgdx won't recognize the render call as anything useful
    public void render() {
        super.render();
    }
}