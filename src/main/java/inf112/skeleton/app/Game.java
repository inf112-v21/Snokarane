package inf112.skeleton.app;

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
import com.badlogic.gdx.math.Vector2;

public class Game extends InputAdapter implements ApplicationListener {
    private SpriteBatch batch;
    private BitmapFont font;

    // Board dimensions
    private int BOARD_X = 5;
    private int BOARD_Y = 5;

    // Entire map
    private TiledMap tiledMap;

    // Layers in the tiledMap
    private TiledMapTileLayer Board;
    private TiledMapTileLayer Player;
    private TiledMapTileLayer Flag;

    // Cell for player state
    private TiledMapTileLayer.Cell playerNormal;

    // Player position initialized at 0, 0
    private Vector2 playerPos = new Vector2(0, 0);

    // Renderer & camera
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

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
        Flag   = (TiledMapTileLayer) tiledMap.getLayers().get("Flag");
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
        // Set camera to orthographic, size board dimensions
        camera.setToOrtho(false, BOARD_X, BOARD_Y);
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

    /**
     * This function is called by libgdx when a key is released.
     *
     * @param keyCode keycode of key released
     * @return true if keyrelease was handled
     */
    @Override
    public boolean keyUp (int keyCode){
        // Clear current player cell regardless of whether player moved
        Player.setCell((int)playerPos.x, (int)playerPos.y, new TiledMapTileLayer.Cell());

        switch (keyCode){
            case Input.Keys.LEFT:
                if (playerPos.x > 0){
                    // Update player position
                    playerPos.x-=1;

                    // Key release was handled, so return true
                    return true;
                }
                break;
            case Input.Keys.RIGHT:
                if (playerPos.x < BOARD_X-1){
                    // Update player position
                    playerPos.x+=1;

                    // Key release was handled, so return true
                    return true;
                }
                break;
            case Input.Keys.UP:
                if (playerPos.y < BOARD_Y-1){
                    // Update player position
                    playerPos.y+=1;

                    // Key release was handled, so return true
                    return true;
                }
                break;
            case Input.Keys.DOWN:
                if (playerPos.y > 0){
                    // Update player position
                    playerPos.y-=1;

                    // Key release was handled, so return true
                    return true;
                }
                break;
            default:
                break;
        }

        // Key press wasn't handled, so return false
        return false;
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
