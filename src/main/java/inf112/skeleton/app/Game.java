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
    private TiledMapTileLayer Player;

    // Cell for player state
    private TiledMapTileLayer.Cell playerNormal;

    // Player position initialized at 0, 0
    private Vector2 playerPos = new Vector2(0, 0);

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
        loadPlayerTextures();
        initializeRendering();
    }

    /**
     * Load map layers into each member variable
     */
    private void loadMapLayers(){
        // Load .tmx file
        TmxMapLoader tmxMap = new TmxMapLoader();
        tiledMap = tmxMap.load("assets/test-map.tmx");

        // Load all layer from entire map to seperate layers
        Board  = (TiledMapTileLayer) tiledMap.getLayers().get("Board");
        Player = (TiledMapTileLayer) tiledMap.getLayers().get("Player");
    }

    /**
     * Load player texture and split into each player state
     */
    private void loadPlayerTextures(){
        // Load the entire player texture
        Texture rawPlayerTexture = new Texture("assets/player.png");

        // Split player texture into seperate regions
        TextureRegion [][] splitTextures = TextureRegion.split(rawPlayerTexture,300, 300);

        // Put the texture regions into seperate tiles
        StaticTiledMapTile playerNormalStaticTile = new StaticTiledMapTile(splitTextures[0][0]);

        // Set player state cells to corresponding tiles
        playerNormal = new TiledMapTileLayer.Cell().setTile(playerNormalStaticTile);
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

        // Set player cell to render
        Player.setCell((int) playerPos.x, (int) playerPos.y, playerNormal);

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
