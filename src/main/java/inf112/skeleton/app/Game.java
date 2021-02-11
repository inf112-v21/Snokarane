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

import java.util.ArrayList;
import java.util.List;

/**
 * Handles rendering, textures and event handling (key presses)
 * Currently this class also contains most of the game logic
 */
public class Game extends InputAdapter implements ApplicationListener {
    // For rendering text to screen
    private SpriteBatch batch;
    private BitmapFont font;

    // Board dimensions (amount of tiles in X and Y direction)
    private int BOARD_X = 5;
    private int BOARD_Y = 5;

    // Entire map (graphic)
    private TiledMap tiledMap;

    // Layers from the tiledMap
    private TiledMapTileLayer boardLayer;
    private TiledMapTileLayer playerLayer;
    private TiledMapTileLayer flagLayer;

    // Flags on the map are stored here for easy access
    List<Flag> flagPositions;

    // Cells for each player state
    private TiledMapTileLayer.Cell playerNormal;
    private TiledMapTileLayer.Cell playerWon;

    // Player object that stores player win state, flags visited and coordinates
    Player player;

    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    /**
     * Initialize all libgdx objects:
     *  Batch, font, input processor, textures, map layers, camera and renderer,
     * and Fetch flags from flag layer
     *
     * This function is called on libgdx startup
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);

        // Register input processor to handle key presses
        Gdx.input.setInputProcessor(this);

        // Create a player to move around the screen
        player = new Player();

        // Load all separate map layers
        loadMapLayers();
        // Load in all the different textures for the player
        loadPlayerTextures();
        // Start camera/rendering
        initializeRendering();

        // Get all flag positions from Flag layer and store them as Flag objects
        flagPositions = getFlagPositions();
    }

    /**
     * Load all map layers into their own member variable
     */
    private void loadMapLayers(){
        // Load .tmx file into tiledMap member
        TmxMapLoader tmxMap = new TmxMapLoader();
        tiledMap = tmxMap.load("assets/test-map.tmx");

        // Separate each layer from tiledMap
        boardLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Board");
        playerLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Player");
        flagLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Flag");
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
        StaticTiledMapTile playerWonStaticTile = new StaticTiledMapTile(splitTextures[0][2]);

        // Set player state cells to corresponding tiles
        playerNormal = new TiledMapTileLayer.Cell().setTile(playerNormalStaticTile);
        playerWon = new TiledMapTileLayer.Cell().setTile(playerWonStaticTile);
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
        camera.update();

        // Initialize renderer                                  v--- 300F is tile size
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1F/300F);
        // Set renderer to view camera
        renderer.setView(camera);
    }

    /**
     * This function is called by libgdx when a key is released.
     *
     * @param keyCode keycode of key released
     * @return true if keyrelease was handled
     */
    @Override
    public boolean keyUp (int keyCode){
        boolean keyHandled = false;

        // Clear current player cell regardless of whether player moved, since it's set again in render()
        playerLayer.setCell(player.getX(), player.getY(), new TiledMapTileLayer.Cell());

        switch (keyCode){
            case Input.Keys.LEFT:
                if (player.getX() > 0){
                    // Update player position to left
                    player.move(-1, 0);

                    keyHandled = true;
                }
                break;
            case Input.Keys.RIGHT:
                if (player.getX() < BOARD_X-1){
                    // Update player position to right
                    player.move(1, 0);

                    keyHandled = true;
                }
                break;
            case Input.Keys.UP:
                if (player.getY() < BOARD_Y-1){
                    // Update player position to up
                    player.move(0, 1);

                    keyHandled = true;
                }
                break;
            case Input.Keys.DOWN:
                if (player.getY() > 0){
                    // Update player position to down
                    player.move(0, -1);

                    keyHandled = true;
                }
                break;
            default:
                break;
        }

        // There aren't any good places to check for win conditions right now, so we will have to do this here,
        // As the keyUp function is the best place to handle something that is to be checked every time we press a key
        if (keyHandled){
            checkForFlags();
            checkIfPlayerWon();
        }

        // Return key press flag
        return keyHandled;
    }

    /**
     * Check if player moved on a flag
     */
    public void checkForFlags(){
        // Check if player moved onto a flag
        for (Flag f : flagPositions){
            if (f.getX() == player.getX() && f.getY() == player.getY()){
                player.visitFlag(f);
            }
        }
    }

    /**
     * This function sets player win state to true if the visited flags amount equal all flags count in map
     */
    public void checkIfPlayerWon(){
        if (player.getVisitedFlags().size() == flagPositions.size()){
            player.isWinner = true;
        }
    }

    /**
     * Get all flag positions in layer flag layer
     * @return list of all flags found
     */
    private List<Flag> getFlagPositions(){
        List<Flag> flags = new ArrayList<>();

        for (int i = 0; i <= flagLayer.getWidth(); i++){
            for (int j = 0; j <= flagLayer.getHeight(); j++){
                // getCell returns null if nothing is found in the current cell in this layer
                if (flagLayer.getCell(i, j) != null){
                    flags.add(new Flag(i, j));
                }
            }
        }
        return flags;
    }

    /**
     * Render all objects and text to the screen
     */
    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        // Shows player texture depending on if player has won or not
        if (player.isWinner){
            playerLayer.setCell(player.getX(), player.getY(), playerWon);
        } else {
            // Set player cell to render
            playerLayer.setCell(player.getX(), player.getY(), playerNormal);
        }

        // Render current frame to screen
        renderer.render();

        // Write some text to show the player they won
        // This has to be placed after renderer.render() else it doesn't show on screen
        if (player.isWinner){
            batch.begin();
            font.draw(batch, "Player won!", 200, 100);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
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
