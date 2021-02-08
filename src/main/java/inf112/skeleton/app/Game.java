package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.math.Vector2;

public class Game extends InputAdapter implements ApplicationListener {
    private SpriteBatch batch;
    private BitmapFont font;

    // Entire map
    private TiledMap tiledMap;

    // The board layer in the map
    private TiledMapTileLayer Board;

    // Renderer & camera
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    // Camera dimensions
    private int CAMERA_X = 5;
    private int CAMERA_Y = 5;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);

        // Register input processor
        Gdx.input.setInputProcessor(this);

        loadMapLayers();
        initializeRendering();
    }

    /**
     * Load map layers into each member variable
     */
    private void loadMapLayers(){
        // Load .tmx file
        TmxMapLoader tmxMap = new TmxMapLoader();
        tiledMap = tmxMap.load("assets/test-map.tmx");

        // Load board layer from entire map to board layer
        Board  = (TiledMapTileLayer) tiledMap.getLayers().get("Board");
    }

    /**
     * Initialize camera and renderer, sets view/renderer to tiledMap
     */
    private void initializeRendering(){
        // Initialize camera object
        camera = new OrthographicCamera();
        // Set camera to orthographic
        camera.setToOrtho(false, CAMERA_X, CAMERA_Y);
        // Set camera X-position
        camera.position.x = 2.5F;
        // Update changes to camera
        camera.update();

        // Initialize renderer                                           v--- tile size
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1F/300F);
        // Set view to camera
        renderer.setView(camera);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        // Render frame
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
